package com.globalnotes.android.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalnotes.android.viewmodel.NoteViewModel
import com.globalnotes.android.ui.theme.*

private val NUMBER_LIST_REGEX = Regex("^(\\d+)\\. ")

private enum class PaperStyle(val label: String) {
    PLAIN("Plain"), LINED("Lined"), GRID("Grid"), DOTTED("Dotted")
}

@Composable
fun EditorPanel(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    onBackClick: () -> Unit = {}
) {
    val note = viewModel.selectedNote
    var titleState   by remember(note?.id) { mutableStateOf(TextFieldValue(note?.title   ?: "")) }
    var contentState by remember(note?.id) { mutableStateOf(TextFieldValue(note?.content ?: "")) }

    var isBold          by remember { mutableStateOf(false) }
    var isItalic        by remember { mutableStateOf(false) }
    var isUnderline     by remember { mutableStateOf(false) }
    var isStrikethrough by remember { mutableStateOf(false) }

    val isBulletList by remember {
        derivedStateOf {
            val cursor = contentState.selection.start
            val t = contentState.text
            if (t.isEmpty()) false
            else {
                val ls = t.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)) + 1
                val le = t.indexOf('\n', cursor).let { if (it == -1) t.length else it }
                t.substring(ls, le).startsWith("• ")
            }
        }
    }
    val isNumberedList by remember {
        derivedStateOf {
            val cursor = contentState.selection.start
            val t = contentState.text
            if (t.isEmpty()) false
            else {
                val ls = t.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)) + 1
                val le = t.indexOf('\n', cursor).let { if (it == -1) t.length else it }
                NUMBER_LIST_REGEX.containsMatchIn(t.substring(ls, le))
            }
        }
    }

    var selectedPaper by remember { mutableStateOf(PaperStyle.LINED) }
    var fontSize      by remember { mutableStateOf(16f) }   // sp, clamped 10–32
    var selectedColor by remember { mutableStateOf(Color(0xFF211B15)) }

    val wordCount by remember {
        derivedStateOf {
            contentState.text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        }
    }

    val scrollState = rememberScrollState()

    if (note == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a note to start editing", color = Color(0xFF8C8479))
        }
        return
    }

    val paperBg = when (selectedPaper) {
        PaperStyle.PLAIN  -> Color(0xFFFFFFFF)
        PaperStyle.LINED  -> Color(0xFFFDFBF7)
        PaperStyle.GRID   -> Color(0xFFFAF9F7)
        PaperStyle.DOTTED -> Color(0xFFFBFAF8)
    }

    Scaffold(
        topBar = {
            EditorTopBar(
                title            = titleState.text.ifBlank { "Untitled" },
                isFavorite       = note.isFavorite,
                onBackClick      = onBackClick,
                onFavoriteToggle = { viewModel.toggleFavorite(note.id) },
                onDelete         = { viewModel.deleteNote(note.id) }
            )
        },
        bottomBar = {
            EditorBottomBar(
                isBold          = isBold,
                isItalic        = isItalic,
                isUnderline     = isUnderline,
                isStrikethrough = isStrikethrough,
                isBulletList    = isBulletList,
                isNumberedList  = isNumberedList,
                fontSize        = fontSize,
                selectedColor   = selectedColor,
                onBold = {
                    val sel = contentState.selection
                    if (!sel.collapsed)
                        contentState = contentState.withSpan(SpanStyle(fontWeight = FontWeight.Bold))
                    else
                        isBold = !isBold
                },
                onItalic = {
                    val sel = contentState.selection
                    if (!sel.collapsed)
                        contentState = contentState.withSpan(SpanStyle(fontStyle = FontStyle.Italic))
                    else
                        isItalic = !isItalic
                },
                onUnderline = {
                    val sel = contentState.selection
                    if (!sel.collapsed)
                        contentState = contentState.withSpan(SpanStyle(textDecoration = TextDecoration.Underline))
                    else
                        isUnderline = !isUnderline
                },
                onStrikethrough = {
                    val sel = contentState.selection
                    if (!sel.collapsed)
                        contentState = contentState.withSpan(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    else
                        isStrikethrough = !isStrikethrough
                },
                onDecrease = {
                    val sel = contentState.selection
                    val newSize = (fontSize - 2f).coerceAtLeast(10f)
                    if (!sel.collapsed) {
                        contentState = contentState.withSpan(SpanStyle(fontSize = newSize.sp))
                    } else {
                        fontSize = newSize
                        contentState = contentState.withSpanAll(SpanStyle(fontSize = newSize.sp))
                    }
                },
                onIncrease = {
                    val sel = contentState.selection
                    val newSize = (fontSize + 2f).coerceAtMost(32f)
                    if (!sel.collapsed) {
                        contentState = contentState.withSpan(SpanStyle(fontSize = newSize.sp))
                    } else {
                        fontSize = newSize
                        contentState = contentState.withSpanAll(SpanStyle(fontSize = newSize.sp))
                    }
                },
                onBulletList = {
                    contentState = contentState.toggleBulletOnCurrentLine()
                    viewModel.updateNoteContent(note.id, contentState.text)
                },
                onNumberedList = {
                    contentState = contentState.toggleNumberedOnCurrentLine()
                    viewModel.updateNoteContent(note.id, contentState.text)
                },
                onColor = { c ->
                    val sel = contentState.selection
                    if (!sel.collapsed)
                        contentState = contentState.withSpan(SpanStyle(color = c))
                    else
                        selectedColor = c
                }
            )
        },
        containerColor = paperBg,
        modifier       = modifier.fillMaxSize().imePadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // ── Paper style chips ─────────────────────────────────────────────
            PaperStyleRow(selected = selectedPaper, onSelect = { selectedPaper = it })
            HorizontalDivider(color = Color(0xFFEDE9E3), thickness = 0.5.dp)

            // ── Title ─────────────────────────────────────────────────────────
            BasicTextField(
                value         = titleState,
                onValueChange = {
                    titleState = it
                    viewModel.updateNoteTitle(note.id, it.text)
                },
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 26.sp,
                    color      = Color(0xFF211B15)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 6.dp),
                decorationBox = { inner ->
                    if (titleState.text.isEmpty()) {
                        Text(
                            "Untitled Note",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 26.sp
                            ),
                            color = Color(0xFFD1CECB)
                        )
                    }
                    inner()
                }
            )

            // ── Metadata ──────────────────────────────────────────────────────
            Text(
                text     = "LAST EDITED ${note.time.uppercase()} · $wordCount WORDS",
                style    = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.8.sp,
                    fontSize      = 10.sp
                ),
                color    = Color(0xFFB5ADA4),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
            )

            // ── Content (with optional paper background) ───────────────────────
            val lineSpacing = 28.dp
            val bodyStyle = MaterialTheme.typography.bodyLarge.copy(
                lineHeight     = lineSpacing.value.sp,
                fontSize       = fontSize.sp,
                fontWeight     = if (isBold) FontWeight.Bold else FontWeight.Normal,
                fontStyle      = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                textDecoration = when {
                    isUnderline && isStrikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
                    isUnderline     -> TextDecoration.Underline
                    isStrikethrough -> TextDecoration.LineThrough
                    else            -> TextDecoration.None
                },
                color          = selectedColor
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 600.dp)
            ) {
                when (selectedPaper) {
                    PaperStyle.LINED  -> LinedCanvas(lineSpacing, Color(0xFFE8E3DC))
                    PaperStyle.GRID   -> GridCanvas(lineSpacing, Color(0xFFE8E3DC))
                    PaperStyle.DOTTED -> DottedCanvas(lineSpacing, Color(0xFFD1CECB))
                    PaperStyle.PLAIN  -> {}
                }

                BasicTextField(
                    value         = contentState,
                    onValueChange = { new ->
                        val processed = autoHandleListContinuation(new, contentState)
                        contentState = processed
                        viewModel.updateNoteContent(note.id, processed.text)
                    },
                    textStyle = bodyStyle,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                        .padding(bottom = 60.dp),
                    decorationBox = { inner ->
                        if (contentState.text.isEmpty()) {
                            Text("Start writing...", style = bodyStyle.copy(color = Color(0xFFD1CECB)))
                        }
                        inner()
                    }
                )
            }
        }
    }
}

// ── Paper style chip row ──────────────────────────────────────────────────────

@Composable
private fun PaperStyleRow(selected: PaperStyle, onSelect: (PaperStyle) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        PaperStyle.entries.forEach { style ->
            val isSelected = selected == style
            Surface(
                onClick = { onSelect(style) },
                shape   = RoundedCornerShape(50),
                color   = Color.Transparent,
                border  = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) WorkspaceAmber else Color(0xFFD1CECB)
                )
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint     = WorkspaceAmber,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        style.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) WorkspaceAmber else Color(0xFF8C8479)
                    )
                }
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun EditorTopBar(
    title: String,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(color = Color(0xFFFDFBF7), shadowElevation = 1.dp) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier           = Modifier.size(18.dp),
                    tint               = Color(0xFF211B15)
                )
            }
            Text(
                text     = title,
                style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color    = Color(0xFF211B15),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector        = if (isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = "Favorite",
                    modifier           = Modifier.size(22.dp),
                    tint               = if (isFavorite) WorkspaceAmber else Color(0xFF8C8479)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = "Share",
                    modifier           = Modifier.size(22.dp),
                    tint               = Color(0xFF8C8479)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    modifier           = Modifier.size(22.dp),
                    tint               = Color(0xFF8C8479)
                )
            }
        }
    }
}

// ── Bottom bar (3 rows) ───────────────────────────────────────────────────────

@Composable
private fun EditorBottomBar(
    isBold: Boolean,
    isItalic: Boolean,
    isUnderline: Boolean,
    isStrikethrough: Boolean,
    isBulletList: Boolean,
    isNumberedList: Boolean,
    fontSize: Float,
    selectedColor: Color,
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onUnderline: () -> Unit,
    onStrikethrough: () -> Unit,
    onBulletList: () -> Unit,
    onNumberedList: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onColor: (Color) -> Unit
) {
    Surface(
        color    = Color(0xFFF5F2EE),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider(color = Color(0xFFEDE9E3), thickness = 0.5.dp)

            // Row 1: Text formatting
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FmtToggleBtn(Icons.Default.FormatBold,       "Bold",          isBold,          onBold)
                FmtToggleBtn(Icons.Default.FormatItalic,     "Italic",        isItalic,        onItalic)
                FmtToggleBtn(Icons.Default.FormatUnderlined, "Underline",     isUnderline,     onUnderline)
                FmtToggleBtn(Icons.Default.StrikethroughS,   "Strikethrough", isStrikethrough, onStrikethrough)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(Color(0xFFD1CECB))
                )
                FmtToggleBtn(Icons.Default.FormatListBulleted, "Bullets",  isBulletList,  onBulletList)
                FmtToggleBtn(Icons.Default.FormatListNumbered, "Numbered", isNumberedList, onNumberedList)
                FmtBtn(Icons.Default.CheckBox,               "Checkbox")  {}
            }

            HorizontalDivider(color = Color(0xFFEDE9E3), thickness = 0.5.dp)

            // Row 2: Media actions
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MediaBtn(Icons.Outlined.Image,      "Photo")  {}
                MediaBtn(Icons.Outlined.Mic,        "Audio")  {}
                MediaBtn(Icons.Outlined.AttachFile, "File")   {}
                MediaBtn(Icons.Outlined.Brush,      "Sketch") {}
            }

            HorizontalDivider(color = Color(0xFFEDE9E3), thickness = 0.5.dp)

            // Row 3: Font size controls + color swatches
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // A− button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (fontSize <= 10f) Color(0xFFEDE9E3) else Color.Transparent)
                        .clickable(enabled = fontSize > 10f) { onDecrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp
                        ),
                        color = if (fontSize <= 10f) Color(0xFFD1CECB) else Color(0xFF5C5550)
                    )
                    Text(
                        "−",
                        style    = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color    = if (fontSize <= 10f) Color(0xFFD1CECB) else Color(0xFF5C5550),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                    )
                }

                // Current size label
                Text(
                    "${fontSize.toInt()}sp",
                    style    = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color    = WorkspaceAmber,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // A+ button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (fontSize >= 32f) Color(0xFFEDE9E3) else Color.Transparent)
                        .clickable(enabled = fontSize < 32f) { onIncrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        ),
                        color = if (fontSize >= 32f) Color(0xFFD1CECB) else Color(0xFF5C5550)
                    )
                    Text(
                        "+",
                        style    = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color    = if (fontSize >= 32f) Color(0xFFD1CECB) else Color(0xFF5C5550),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Color swatches
                val swatches = listOf(
                    Color(0xFF211B15), WorkspaceAmber,
                    Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047)
                )
                swatches.forEach { c ->
                    val isSel = selectedColor == c
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(c)
                            .then(
                                if (isSel) Modifier.border(2.dp, Color(0xFFF5F2EE), CircleShape)
                                else Modifier
                            )
                            .clickable { onColor(c) }
                    )
                }
            }
        }
    }
}

// ── Small composables ─────────────────────────────────────────────────────────

@Composable
private fun FmtToggleBtn(icon: ImageVector, label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) WorkspaceAmber.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon, label,
            modifier = Modifier.size(20.dp),
            tint     = if (active) WorkspaceAmber else Color(0xFF5C5550)
        )
    }
}

@Composable
private fun FmtBtn(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, label, modifier = Modifier.size(20.dp), tint = Color(0xFF5C5550))
    }
}

@Composable
private fun MediaBtn(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier              = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(2.dp)
    ) {
        Icon(icon, label, modifier = Modifier.size(22.dp), tint = Color(0xFF6B9E9E))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF8C8479), fontSize = 10.sp)
    }
}

// ── Canvas paper backgrounds ──────────────────────────────────────────────────

@Composable
private fun LinedCanvas(lineSpacing: Dp, lineColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacingPx = lineSpacing.toPx()
        val leftPad   = 24.dp.toPx()
        var y = spacingPx
        while (y < size.height) {
            drawLine(
                color       = lineColor,
                start       = Offset(leftPad, y),
                end         = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += spacingPx
        }
    }
}

@Composable
private fun GridCanvas(lineSpacing: Dp, lineColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacingPx = lineSpacing.toPx()
        var y = spacingPx
        while (y < size.height) {
            drawLine(color = lineColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
            y += spacingPx
        }
        var x = spacingPx
        while (x < size.width) {
            drawLine(color = lineColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
            x += spacingPx
        }
    }
}

// ── Span helper ───────────────────────────────────────────────────────────────

private fun TextFieldValue.withSpan(spanStyle: SpanStyle): TextFieldValue {
    val sel = selection
    if (sel.collapsed) return this
    return TextFieldValue(
        annotatedString = buildAnnotatedString {
            append(annotatedString)
            addStyle(spanStyle, sel.min, sel.max)
        },
        selection = sel
    )
}

/** Apply a span across the entire text (forces re-render even with no selection). */
private fun TextFieldValue.withSpanAll(spanStyle: SpanStyle): TextFieldValue {
    if (text.isEmpty()) return this
    return TextFieldValue(
        annotatedString = buildAnnotatedString {
            append(annotatedString)
            addStyle(spanStyle, 0, text.length)
        },
        selection = selection
    )
}

// ── List helpers ──────────────────────────────────────────────────────────────

private fun TextFieldValue.toggleBulletOnCurrentLine(): TextFieldValue {
    val cursor = selection.start
    val ls = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)) + 1
    val le = text.indexOf('\n', cursor).let { if (it == -1) text.length else it }
    val line = text.substring(ls, le)
    return if (line.startsWith("• ")) {
        val newText = text.substring(0, ls) + line.drop(2) + text.substring(le)
        TextFieldValue(text = newText, selection = TextRange((cursor - 2).coerceAtLeast(ls)))
    } else {
        val numPrefix = NUMBER_LIST_REGEX.find(line)?.value ?: ""
        val cleanLine = line.drop(numPrefix.length)
        val newText = text.substring(0, ls) + "• " + cleanLine + text.substring(le)
        val newCursor = (cursor - numPrefix.length + 2).coerceAtLeast(ls + 2)
        TextFieldValue(text = newText, selection = TextRange(newCursor))
    }
}

private fun TextFieldValue.toggleNumberedOnCurrentLine(): TextFieldValue {
    val cursor = selection.start
    val ls = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)) + 1
    val le = text.indexOf('\n', cursor).let { if (it == -1) text.length else it }
    val line = text.substring(ls, le)
    val numMatch = NUMBER_LIST_REGEX.find(line)
    return if (numMatch != null) {
        val pLen = numMatch.value.length
        val newText = text.substring(0, ls) + line.drop(pLen) + text.substring(le)
        TextFieldValue(text = newText, selection = TextRange((cursor - pLen).coerceAtLeast(ls)))
    } else {
        // Determine number from previous numbered lines
        val prevLines = if (ls == 0) emptyList() else text.substring(0, ls - 1).split('\n')
        var num = 1
        for (i in prevLines.indices.reversed()) {
            val m = NUMBER_LIST_REGEX.find(prevLines[i])
            if (m != null) { num = m.groupValues[1].toInt() + 1; break }
        }
        val bulletLen = if (line.startsWith("• ")) 2 else 0
        val cleanLine = line.drop(bulletLen)
        val prefix = "$num. "
        val newText = text.substring(0, ls) + prefix + cleanLine + text.substring(le)
        val newCursor = (cursor - bulletLen + prefix.length).coerceAtLeast(ls + prefix.length)
        TextFieldValue(text = newText, selection = TextRange(newCursor))
    }
}

/** Auto-insert list prefix when Enter is pressed, or stop the list on an empty bullet/number line. */
private fun autoHandleListContinuation(new: TextFieldValue, old: TextFieldValue): TextFieldValue {
    if (new.text.length != old.text.length + 1) return new
    val insertPos = new.selection.start - 1
    if (insertPos < 0 || new.text[insertPos] != '\n') return new

    val ls = new.text.lastIndexOf('\n', insertPos - 1) + 1
    val line = new.text.substring(ls, insertPos)

    return when {
        line == "• " -> {
            // Empty bullet — stop list
            val cleaned = new.text.substring(0, ls) + new.text.substring(insertPos + 1)
            TextFieldValue(text = cleaned, selection = TextRange(ls))
        }
        line.startsWith("• ") -> {
            val newText = new.text.substring(0, insertPos + 1) + "• " + new.text.substring(insertPos + 1)
            TextFieldValue(text = newText, selection = TextRange(insertPos + 3))
        }
        else -> {
            val match = NUMBER_LIST_REGEX.find(line)
            when {
                match != null && line == match.value -> {
                    // Empty numbered line — stop list
                    val cleaned = new.text.substring(0, ls) + new.text.substring(insertPos + 1)
                    TextFieldValue(text = cleaned, selection = TextRange(ls))
                }
                match != null -> {
                    val nextPrefix = "${match.groupValues[1].toInt() + 1}. "
                    val newText = new.text.substring(0, insertPos + 1) + nextPrefix + new.text.substring(insertPos + 1)
                    TextFieldValue(text = newText, selection = TextRange(insertPos + 1 + nextPrefix.length))
                }
                else -> new
            }
        }
    }
}

@Composable
private fun DottedCanvas(spacing: Dp, dotColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacingPx = spacing.toPx()
        var y = spacingPx
        while (y < size.height) {
            var x = spacingPx
            while (x < size.width) {
                drawCircle(color = dotColor, radius = 1.5f, center = Offset(x, y))
                x += spacingPx
            }
            y += spacingPx
        }
    }
}
