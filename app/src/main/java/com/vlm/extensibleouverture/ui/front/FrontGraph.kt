package com.vlm.extensibleouverture.ui.front

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.accompanist.insets.ProvideWindowInsets
import com.vlm.extensibleouverture.ui.front.gathering.Gathering
import com.vlm.extensibleouverture.ui.front.profile.Profile
import com.vlm.extensibleouverture.ui.front.profile.ProfileContext
import com.vlm.extensibleouverture.viewmodels.FrontViewModel
import com.vlm.extensibleouverture.viewmodels.SignUpLibraryViewModel
import com.vlm.extensibleouverture.viewmodels.UserViewModelParkGolf
import com.vlm.extensibleouverture.ui.front.bars.VlmBottomBar
import com.vlm.extensibleouverture.ui.front.chatting.Chatting
import com.vlm.extensibleouverture.ui.front.history.History
import com.vlm.extensibleouverture.ui.front.writing.Writing
import com.vlm.vlmlibrary.compose.scaffold.VlmScaffold
import com.vlm.vlmlibrary.compose.theme.VlmTheme

@OptIn(
    ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    kotlinx.coroutines.ExperimentalCoroutinesApi::class,
    coil.annotation.ExperimentalCoilApi::class
)
fun NavGraphBuilder.frontGraph(
    signinLibraryViewModel : SignUpLibraryViewModel,
    userViewModel : UserViewModelParkGolf,
    frontViewModel : FrontViewModel,
    signResponse : (String, NavBackStackEntry) -> Unit,
    modifier : Modifier = Modifier,
){
    composable(
        FrontDestination.FRONT.route,
//        arguments = listOf( navArgument("userId") { defaultValue = "me" })
    ){ from ->
        ProvideWindowInsets {
            VlmTheme {
                val tabs = remember { FrontSections.values() }
                val navController = rememberNavController()
                VlmScaffold(
//                    topBar = { TopAppBar { /* Top app bar content */ }},
                    bottomBar = { VlmBottomBar(navController = navController, tabs = tabs) }
                ) { innerPaddingModifier ->
                    FrontNavGraph(
                        modifier = Modifier.padding(innerPaddingModifier),
                        userViewModel = userViewModel,
                        frontViewModel = frontViewModel,
                        navController = navController,
                        signResponse = signResponse
                    )
                }
            }
        }
    }

}



fun NavGraphBuilder.addFrontGraph(
    modifier: Modifier = Modifier,
    userViewModel   : UserViewModelParkGolf,
    frontViewModel  : FrontViewModel,
    signResponse : (String, NavBackStackEntry) -> Unit
) {
    composable(FrontSections.WRITING.route) { from ->
        Writing(modifier, userViewModel)//Feed(onSnackClick = { id -> onSnackSelected(id, from) }, modifier)
    }
    composable(FrontSections.CHATTING.route) { from ->
        Chatting(modifier)//Feed(onSnackClick = { id -> onSnackSelected(id, from) }, modifier)
    }
    composable(FrontSections.GATHERING.route) { from ->
        Gathering(frontViewModel = frontViewModel)
//        GatheringFakeData.generating()
//        FakeGathering(frontViewModel = FakeFrontViewModel)//Search(onSnackClick = { id -> onSnackSelected(id, from) }, modifier)
    }
    composable(FrontSections.HISTORY.route) { from ->
        History(modifier)//Cart(onSnackClick = { id -> onSnackSelected(id, from) }, modifier)
    }
    composable(FrontSections.PROFILE.route) { navBack ->
        Profile(modifier, userViewModel = userViewModel, content = { ProfileContext(userViewModel) }){ signState ->
            signResponse(signState, navBack)
        }
    }
}

@Composable
fun FrontNavGraph(
    modifier: Modifier = Modifier,
    userViewModel : UserViewModelParkGolf,
    frontViewModel : FrontViewModel,
    navController: NavHostController = rememberNavController(),
    startDestination: String = FrontSections.GATHERING.sectionName,
    signResponse : (String, NavBackStackEntry) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        navigation(
            route = FrontSections.GATHERING.sectionName,
            startDestination = FrontSections.GATHERING.route
        ) {
            addFrontGraph(
                modifier = modifier,
                userViewModel = userViewModel,
                frontViewModel = frontViewModel,
                signResponse = signResponse
            )
        }
//        composable(
//            "${MainDestinations.HISTORY_ROUTE}/{$SNACK_ID_KEY}",
//            arguments = listOf(navArgument(SNACK_ID_KEY) { type = NavType.LongType })
//        ) { backStackEntry ->
//            val arguments = requireNotNull(backStackEntry.arguments)
//            val snackId = arguments.getLong(SNACK_ID_KEY)
//            SnackDetail(
//                snackId = snackId,
//                upPress = {
//                    navController.navigateUp()
//                }
//            )
//        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
