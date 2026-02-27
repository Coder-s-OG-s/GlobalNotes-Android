package com.globalnotes.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import kotlinx.coroutines.launch
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass

import com.globalnotes.android.viewmodel.NoteViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen() {
    val noteViewModel: NoteViewModel = viewModel()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isTablet = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isTablet) {
            TabletLayout(noteViewModel)
        } else {
            PhoneLayout(noteViewModel)
        }
    }
}

@Composable
fun TabletLayout(viewModel: NoteViewModel) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Panel 1: Sidebar
        SidebarPanel(
            modifier = Modifier.width(260.dp),
            viewModel = viewModel
        )
        
        VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        
        // Panel 2: Notes List
        NotesListPanel(
            modifier = Modifier.width(320.dp),
            viewModel = viewModel
        )
        
        VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        
        // Panel 3: Editor
        EditorPanel(
            modifier = Modifier.weight(1f),
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PhoneLayout(viewModel: NoteViewModel) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                windowInsets = WindowInsets.statusBars
            ) {
                SidebarPanel(viewModel = viewModel)
            }
        }
    ) {
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                NotesListPanel(
                    viewModel = viewModel,
                    onNoteClick = { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail) },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            detailPane = {
                EditorPanel(
                    viewModel = viewModel,
                    onBackClick = { navigator.navigateBack() }
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SidebarPanel(modifier: Modifier = Modifier, viewModel: NoteViewModel) {
    com.globalnotes.android.ui.components.SidebarPanel(modifier = modifier, viewModel = viewModel)
}

@Composable
fun NotesListPanel(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    onNoteClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    com.globalnotes.android.ui.components.NotesListPanel(
        modifier = modifier,
        viewModel = viewModel,
        onNoteClick = onNoteClick,
        onMenuClick = onMenuClick
    )
}

@Composable
fun EditorPanel(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    onBackClick: () -> Unit = {}
) {
    com.globalnotes.android.ui.components.EditorPanel(
        modifier = modifier,
        viewModel = viewModel,
        onBackClick = onBackClick
    )
}
