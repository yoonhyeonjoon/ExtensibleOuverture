package com.vlm.extensibleouverture.ui.front.bars

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vlm.extensibleouverture.ui.front.FrontSections

@Composable
fun VlmTopBar(
    tabs : Array<FrontSections>,
    currentRoute : String,
    navigateToRoute : (String) -> Unit,
)
{
    val routes = remember { tabs.map { it.route }}
    val currentSection = tabs.first { it.route == currentRoute }
    var tabIndex by remember { mutableStateOf(0) }

    TabRow(tabIndex)
    {
        tabs.forEachIndexed{ index, FrontSections ->
            Tab(selected = tabIndex ==index, onClick = {
                tabIndex = index
                navigateToRoute(FrontSections.route)
            }) {
                Text(text = stringResource(id = FrontSections.title), modifier = Modifier.padding(30.dp))
            }
        }
    }

}