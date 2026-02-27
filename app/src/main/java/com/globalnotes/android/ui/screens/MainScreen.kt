package com.globalnotes.android.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen() {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isTablet = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    if (isTablet) {
        TabletLayout()
    } else {
        PhoneLayout()
    }
}

@Composable
fun TabletLayout() {
    Row(modifier = Modifier.fillMaxSize()) {
        // Panel 1: Sidebar (Static)
        SidebarPanel(
            modifier = Modifier.width(220.dp)
        )
        
        VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        
        // Panel 2: Notes List
        NotesListPanel(
            modifier = Modifier.width(280.dp)
        )
        
        VerticalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        
        // Panel 3: Editor
        EditorPanel(
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PhoneLayout() {
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    
    // On phone, we use a Modal Drawer for the Sidebar
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                SidebarPanel(modifier = Modifier.fillMaxSize())
            }
        }
    ) {
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                NotesListPanel(
                    onNoteClick = { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail) },
                    onMenuClick = { /* Open drawer */ }
                )
            },
            detailPane = {
                EditorPanel(
                    onBackClick = { navigator.navigateBack() }
                )
            }
        )
    }
}

@Composable
fun SidebarPanel(modifier: Modifier = Modifier) {
    com.globalnotes.android.ui.components.SidebarPanel(modifier = modifier)
}

@Composable
fun NotesListPanel(
    modifier: Modifier = Modifier,
    onNoteClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    com.globalnotes.android.ui.components.NotesListPanel(
        modifier = modifier,
        onNoteClick = onNoteClick,
        onMenuClick = onMenuClick
    )
}

@Composable
fun EditorPanel(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    com.globalnotes.android.ui.components.EditorPanel(
        modifier = modifier,
        onBackClick = onBackClick
    )
}
