package com.vlm.extensibleouverture.ui.front

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.vlm.extensibleouverture.ui.front.bars.VlmBottomBar
import com.vlm.vlmlibrary.compose.theme.VlmTheme


@Preview
@Composable
private fun JetsnackBottomNavPreview() {
    VlmTheme {
        VlmBottomBar(
            navController = rememberNavController(),
            tabs = FrontSections.values()
        )
    }
}

//
//@Composable
//fun FrontPage() {
//    val context = LocalContext.current
//
//    ProvideWindowInsets {
//        VlmTheme {
//            val tabs = remember { FrontSections.values() }
//            val navController = rememberNavController()
//            VlmScaffold(
//                bottomBar = { VlmBottomBar(navController = navController, tabs = tabs) }
//            ) { innerPaddingModifier ->
//                FrontNavGraph(
//                    navController = navController,
//                    modifier = Modifier.padding(innerPaddingModifier),
//                    userViewModel = userViewModel
//                )
//            }
//        }
//    }
//}