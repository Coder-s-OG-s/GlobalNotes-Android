package com.globalnotes.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SidebarPanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Global Notes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            item { SectionLabel("MY WORKSPACE") }
            
            item { SidebarItem("All Notes", Icons.Outlined.Notes, badge = "12") }
            item { SidebarItem("Recent", Icons.Outlined.History) }
            item { SidebarItem("Favorites", Icons.Outlined.StarOutline) }
            item { SidebarItem("Archived", Icons.Outlined.Archive) }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item { SectionLabel("FOLDERS") }
            item { FolderItem("Personal", Color(0xFF4CAF50), count = "5") }
            item { FolderItem("Work", Color(0xFF2196F3), count = "3") }
            item { FolderItem("Project X", Color(0xFFFF9800), count = "4") }
            item { NewButton("+ New Folder") }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item { SectionLabel("LABELS") }
            item { LabelItem("Priority", Color.Red) }
            item { LabelItem("In Progress", Color.Yellow) }
            item { NewButton("+ Create new label") }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

        // User Profile
        UserFooter()
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SidebarItem(text: String, icon: ImageVector, badge: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        if (badge != null) {
            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text(badge, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun FolderItem(name: String, color: Color, count: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Folder, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(count, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
    }
}

@Composable
fun LabelItem(name: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun NewButton(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clickable { }
            .padding(vertical = 8.dp, horizontal = 8.dp)
    )
}

@Composable
fun UserFooter() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text("AP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Ayush Patel", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("Free Plan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
