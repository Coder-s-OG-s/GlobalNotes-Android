package com.globalnotes.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.globalnotes.android.viewmodel.NoteViewModel
import com.globalnotes.android.ui.theme.*

@Composable
fun SidebarPanel(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    onSignOut: () -> Unit = {}
) {
    val context     = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val displayName = currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: currentUser?.email?.substringBefore("@") ?: "User"
    val photoUrl = currentUser?.photoUrl?.toString()

    val allNotes      = viewModel.notes.filter { !it.isArchived }
    val favCount      = allNotes.count { it.isFavorite }
    val folderCounts  = allNotes.groupBy { it.folder }.mapValues { it.value.size }
    val folders       = folderCounts.keys.sorted()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFFFDFBF7))
            .statusBarsPadding()
    ) {
        // ── User Profile ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 20.dp)
        ) {
            Box(modifier = Modifier.size(72.dp)) {
                if (photoUrl != null) {
                    AsyncImage(
                        model              = photoUrl,
                        contentDescription = null,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFE8DDD0), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD4C5B0))
                            .border(2.dp, Color(0xFFE8DDD0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF8C7B6A)
                        )
                    }
                }
                // Online dot
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                        .border(2.dp, Color(0xFFFDFBF7), CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text  = displayName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 22.sp
                ),
                color = Color(0xFF211B15)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(50), color = Color(0xFFF0EAE0)) {
                Text(
                    text     = "FREE PLAN",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    style    = MaterialTheme.typography.labelSmall.copy(
                        fontWeight    = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        fontSize      = 10.sp
                    ),
                    color = Color(0xFFBC742E)
                )
            }
        }

        HorizontalDivider(color = Color(0xFFEDE9E3))

        // ── Scrollable content ───────────────────────────────────────────────
        LazyColumn(modifier = Modifier.weight(1f)) {

            // My Workspace pill
            item {
                Surface(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape     = RoundedCornerShape(50),
                    color     = Color(0xFFF3E9D8),
                    onClick   = {}
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Computer,
                            contentDescription = null,
                            tint     = WorkspaceAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "My Workspace",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = WorkspaceAmber
                        )
                    }
                }
            }

            // ── LIBRARY ──────────────────────────────────────────────────────
            item { SidebarSectionLabel("LIBRARY") }

            item {
                LibraryRow(
                    text       = "All Notes",
                    icon       = Icons.Outlined.Description,
                    count      = allNotes.size,
                    isSelected = viewModel.currentFilter == "All Notes",
                    onClick    = { viewModel.updateFilter("All Notes") }
                )
            }
            item {
                LibraryRow(
                    text       = "Recent",
                    icon       = Icons.Outlined.AccessTime,
                    count      = null,
                    isSelected = false,
                    onClick    = {}
                )
            }
            item {
                LibraryRow(
                    text       = "Favorites",
                    icon       = Icons.Outlined.Star,
                    count      = if (favCount > 0) favCount else null,
                    isSelected = viewModel.currentFilter == "Favorites",
                    onClick    = { viewModel.updateFilter("Favorites") }
                )
            }

            // ── FOLDERS ──────────────────────────────────────────────────────
            item {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 20.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text     = "FOLDERS",
                        style    = MaterialTheme.typography.labelSmall.copy(
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color    = WorkspaceAmber,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add folder",
                        tint     = WorkspaceAmber,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            items(folders) { folder ->
                FolderCard(
                    name       = folder,
                    count      = folderCounts[folder] ?: 0,
                    isSelected = viewModel.currentFilter == folder,
                    onClick    = { viewModel.updateFilter(folder) }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // ── Bottom actions ───────────────────────────────────────────────────
        HorizontalDivider(color = Color(0xFFEDE9E3))
        Column(modifier = Modifier.padding(vertical = 8.dp).navigationBarsPadding()) {
            BottomActionRow(Icons.Outlined.Settings, "Settings") {}
            BottomActionRow(Icons.Outlined.Palette, "Theme") {}
            BottomActionRow(Icons.Outlined.ExitToApp, "Log Out") {
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                onSignOut()
            }
        }
    }
}

// ── Private composables ──────────────────────────────────────────────────────

@Composable
private fun SidebarSectionLabel(text: String) {
    Text(
        text     = text,
        style    = MaterialTheme.typography.labelSmall.copy(
            fontWeight    = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        ),
        color    = WorkspaceAmber,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 4.dp)
    )
}

@Composable
private fun LibraryRow(
    text: String,
    icon: ImageVector,
    count: Int?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = if (isSelected) WorkspaceAmber else Color(0xFF8C8479),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text,
            style      = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (isSelected) Color(0xFF211B15) else Color(0xFF3D3530),
            modifier   = Modifier.weight(1f)
        )
        if (count != null && count > 0) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = WorkspaceAmber
            )
        }
    }
}

@Composable
private fun FolderCard(
    name: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val folderColor = folderColorFor(name)
    val folderIcon  = folderIconFor(name)

    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape           = RoundedCornerShape(16.dp),
        color           = Color.White,
        shadowElevation = 1.dp,
        onClick         = onClick
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(folderColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    folderIcon,
                    contentDescription = null,
                    tint     = folderColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF211B15)
                )
                Text(
                    "$count notes",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8C8479)
                )
            }

            // Radio indicator
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) WorkspaceAmber else Color.Transparent)
                    .border(
                        width  = 2.dp,
                        color  = if (isSelected) WorkspaceAmber else Color(0xFFD1CECB),
                        shape  = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionRow(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = Color(0xFF6B9E9E),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5C5550)
        )
    }
}

private fun folderColorFor(folder: String): Color = when (folder.lowercase()) {
    "work"     -> WorkspaceTagWork
    "personal" -> WorkspaceTagPersonal
    "writing"  -> WorkspaceTagWriting
    "design"   -> WorkspaceTagDesign
    "travel"   -> WorkspaceTagTravel
    else       -> WorkspaceAmber
}

private fun folderIconFor(folder: String): ImageVector = when (folder.lowercase()) {
    "work"     -> Icons.Filled.Work
    "personal" -> Icons.Filled.Person
    "writing"  -> Icons.Filled.Create
    "design"   -> Icons.Filled.Palette
    "travel"   -> Icons.Filled.Flight
    else       -> Icons.Filled.Folder
}
