package com.vlm.extensibleouverture.ui.front.gathering

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vlm.extensibleouverture.viewmodels.FakeFrontViewModel
import com.vlm.extensibleouverture.viewmodels.FrontViewModelInterface


@Preview(showBackground = true)
@Composable
fun PreviewGathering(){
    Gathering(FakeFrontViewModel)
}

@Composable
fun Gathering(frontViewModel : FrontViewModelInterface) {

    val pagingItems = frontViewModel.gatheringDataFlow.collectAsLazyPagingItems()
    val isRefreshing by frontViewModel.gatheringDataRefreshing.collectAsState()

    pagingItems.apply {
        when (loadState.refresh) { //https://yoon-dailylife.tistory.com/122
            is LoadState.Loading -> {
                frontViewModel.setGatheringDataRefresh(true)
            }
            else -> {
                frontViewModel.setGatheringDataRefresh(false)
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            pagingItems.refresh()
            pagingItems.loadState
        },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                // Pass the SwipeRefreshState + trigger through
                state = state,
                refreshTriggerDistance = trigger,
                // Enable the scale animation
                scale = true,
                // Change the color and shape
                backgroundColor = MaterialTheme.colors.primary,
                shape = MaterialTheme.shapes.small,
            )
        }
    ){
        LazyColumn(
            reverseLayout = false,
            //            state = scrollState,
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.statusBars,
                additionalTop = 10.dp
            ),
            modifier = Modifier
                //            .testTag(ConversationTestTag)
                .padding(top = 15.dp)
                .fillMaxSize()
        ) {

            items(pagingItems) { item ->
                GatheringUI(item){ gatheringData ->

                    //클릭시 리스폰스

                }
            }
        }
    }

}


//            item{
//
//                Column(/*Modifier.align(Alignment.CenterVertically)*/) {
//
//                    Box(modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.CenterHorizontally)) {


//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier
//            .fillMaxSize()
//            .wrapContentSize()
//            .padding(24.dp)
//    ) {
//        Spacer(Modifier.height(24.dp))
//
//    }
//    item {
//        LazyRow(
//            Modifier
//                .padding(bottom = 50.dp)
//                .fillMaxSize()
//                .background(compose.theme.ColorWhite))
//        {
//
//            item{
//
//                Column(/*Modifier.align(Alignment.CenterVertically)*/) {
//
//                    Box(modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.CenterHorizontally)) {
//
//                        Box(
//                            Modifier
//                                .align(Alignment.TopCenter)
//                                .padding(horizontal = 10.dp)/*.fillMaxWidth(0.5f)*/
//                        )
//                        {
//                            Row{
//                                Column(
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                )
//                                {
//                                    Text(
//                                        modifier = Modifier.padding(bottom = 10.dp),
//                                        textAlign = TextAlign.Center,
//                                        text = "의사소통",
//                                        fontSize = 15.sp,
//                                        fontFamily = notosanFontFamily,
//                                        fontWeight = FontWeight.Normal
//                                    )
//
//                                    listOptions1.forEach { text ->
//
//                                        val isChecked: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
//                                        val characterTitle: MutableState<String> = rememberSaveable { mutableStateOf(text) }
//                                        val randomColor: MutableState<Int> = rememberSaveable { mutableStateOf(0) }
//
//                                        VlmCheckButton(
//                                            onClick = {
//                                                if(personalityCheckSet.value.size > 5 && !isChecked.value)
//                                                {
//                                                    Toast.makeText(context,"6개 이하로 선택해주세요", Toast.LENGTH_SHORT).show()
//                                                }
//                                                else
//                                                {
//                                                    if(!isChecked.value) personalityCheckSet.value.add(characterTitle.value)
//                                                    else personalityCheckSet.value.remove(characterTitle.value)
//                                                    println("${personalityCheckSet.value.size}")
//                                                    randomColor.value = setRandomNumber()
//                                                    isChecked.value = !isChecked.value
//                                                }
//                                            },
//                                            isChecked = isChecked,
//                                            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
//                                            shape = RoundedCornerShape(10.dp),
//                                            border = BorderStroke(1.dp,color = Color.Gray),
//                                            checkedAbleBackGroundColor = colorSeed[randomColor.value],
//                                            text = characterTitle.value,
//                                            textStyle = simpleTextStyle,
//                                        )
//                                        Spacer(modifier = Modifier.height(10.dp))
//                                    }
//
//                                }
//
//                                Column(
//                                    Modifier.padding(start = 10.dp),
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                )
//                                {
//                                    Text(
//                                        modifier = Modifier.padding(bottom = 10.dp),
//                                        textAlign = TextAlign.Center,
//                                        text = "자기개발",
//                                        fontSize = 15.sp,
//                                        fontFamily = notosanFontFamily,
//                                        fontWeight = FontWeight.Normal
//                                    )
//
//                                    listOptions2.forEach { text ->
//
//                                        val isChecked: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
//                                        val characterTitle: MutableState<String> = rememberSaveable { mutableStateOf(text) }
//                                        val randomColor: MutableState<Int> = rememberSaveable { mutableStateOf(0) }
//
//                                        VlmCheckButton(
//                                            onClick = {
//                                                if(personalityCheckSet.value.size > 5 && !isChecked.value)
//                                                {
//                                                    Toast.makeText(context,"6개 이하로 선택해주세요", Toast.LENGTH_SHORT).show()
//                                                }
//                                                else
//                                                {
//                                                    if(!isChecked.value) personalityCheckSet.value.add(characterTitle.value)
//                                                    else personalityCheckSet.value.remove(characterTitle.value)
//                                                    randomColor.value = setRandomNumber()
//                                                    isChecked.value = !isChecked.value
//                                                }
//                                            },
//                                            isChecked = isChecked,
//                                            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
//                                            shape = RoundedCornerShape(10.dp),
//                                            border = BorderStroke(1.dp,color = Color.Gray),
//                                            checkedAbleBackGroundColor = colorSeed[randomColor.value],
//                                            text = characterTitle.value,
//                                            textStyle = simpleTextStyle,
//                                        )
//
//                                        Spacer(modifier = Modifier.height(10.dp))
//                                    }
//                                }
//
//                                Column(
//                                    Modifier.padding(start = 10.dp),
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                )
//                                {
//                                    Text(
//                                        modifier = Modifier.padding(bottom = 10.dp),
//                                        textAlign = TextAlign.Center,
//                                        text = "문제해결",
//                                        fontSize = 15.sp,
//                                        fontFamily = notosanFontFamily,
//                                        fontWeight = FontWeight.Normal
//                                    )
//
//                                    listOptions3.forEach { text ->
//
//                                        val isChecked: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
//                                        val characterTitle: MutableState<String> = rememberSaveable { mutableStateOf(text) }
//                                        val randomColor: MutableState<Int> = rememberSaveable { mutableStateOf(0) }
//
//                                        VlmCheckButton(
//                                            onClick = {
//                                                if(personalityCheckSet.value.size > 5 && !isChecked.value)
//                                                {
//                                                    Toast.makeText(context,"6개 이하로 선택해주세요", Toast.LENGTH_SHORT).show()
//                                                }
//                                                else
//                                                {
//                                                    if(!isChecked.value) personalityCheckSet.value.add(characterTitle.value)
//                                                    else personalityCheckSet.value.remove(characterTitle.value)
//                                                    randomColor.value = setRandomNumber()
//                                                    isChecked.value = !isChecked.value
//                                                }
//                                            },
//                                            isChecked = isChecked,
//                                            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
//                                            shape = RoundedCornerShape(10.dp),
//                                            border = BorderStroke(1.dp,color = Color.Gray),
//                                            checkedAbleBackGroundColor = colorSeed[randomColor.value],
//                                            text = characterTitle.value,
//                                            textStyle = simpleTextStyle,
//                                        )
//
//                                        Spacer(modifier = Modifier.height(10.dp))
//                                    }
//                                }
//
//
//                                Column(
//                                    Modifier.padding(start = 10.dp),
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                )
//                                {
//                                    Text(
//                                        modifier = Modifier.padding(bottom = 10.dp),
//                                        textAlign = TextAlign.Center,
//                                        text = "팀워크",
//                                        fontSize = 15.sp,
//                                        fontFamily = notosanFontFamily,
//                                        fontWeight = FontWeight.Normal
//                                    )
//
//                                    listOptions4.forEach { text ->
//
//                                        val isChecked: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
//                                        val characterTitle: MutableState<String> = rememberSaveable { mutableStateOf(text) }
//                                        val randomColor: MutableState<Int> = rememberSaveable { mutableStateOf(0) }
//
//                                        VlmCheckButton(
//                                            onClick = {
//                                                if(personalityCheckSet.value.size > 5 && !isChecked.value)
//                                                {
//                                                    Toast.makeText(context,"6개 이하로 선택해주세요", Toast.LENGTH_SHORT).show()
//                                                }
//                                                else
//                                                {
//                                                    if(!isChecked.value) personalityCheckSet.value.add(characterTitle.value)
//                                                    else personalityCheckSet.value.remove(characterTitle.value)
//                                                    randomColor.value = setRandomNumber()
//                                                    isChecked.value = !isChecked.value
//                                                }
//                                            },
//                                            isChecked = isChecked,
//                                            contentPadding = PaddingValues(start = 6.dp, top = 4.dp, end = 6.dp, bottom = 4.dp),
//                                            shape = RoundedCornerShape(10.dp),
//                                            border = BorderStroke(1.dp,color = Color.Gray),
//                                            checkedAbleBackGroundColor = colorSeed[randomColor.value],
//                                            text = characterTitle.value,
//                                            textStyle = simpleTextStyle,
//
//                                            )
//
//                                        Spacer(modifier = Modifier.height(10.dp))
//                                    }
//                                }
//
//                                Column(
//                                    Modifier.padding(start = 10.dp),
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                )
//                                {
//                                    Text(
//                                        modifier = Modifier.padding(bottom = 10.dp),
//                                        textAlign = TextAlign.Center,
//                                        text = "리더쉽",
//                                        fontSize = 15.sp,
//                                        fontFamily = notosanFontFamily,
//                                        fontWeight = FontWeight.Normal
//                                    )
//
//                                    listOptions5.forEach { text ->
//
//                                        val isChecked: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
//                                        val characterTitle: MutableState<String> = rememberSaveable { mutableStateOf(text) }
//                                        val randomColor: MutableState<Int> = rememberSaveable { mutableStateOf(0) }
//
//                                        VlmCheckButton(
//                                            onClick = {
//                                                if(personalityCheckSet.value.size > 5 && !isChecked.value)
//                                                {
//                                                    Toast.makeText(context,"6개 이하로 선택해주세요", Toast.LENGTH_SHORT).show()
//                                                }
//                                                else
//                                                {
//                                                    if(!isChecked.value) personalityCheckSet.value.add(characterTitle.value)
//                                                    else personalityCheckSet.value.remove(characterTitle.value)
//                                                    randomColor.value = setRandomNumber()
//                                                    isChecked.value = !isChecked.value
//                                                }
//                                            },
//                                            isChecked = isChecked,
//                                            contentPadding = PaddingValues(start = 6.dp, top = 4.dp, end = 6.dp, bottom = 4.dp),
//                                            shape = RoundedCornerShape(10.dp),
//                                            border = BorderStroke(1.dp,color = Color.Gray),
//                                            checkedAbleBackGroundColor = colorSeed[randomColor.value],
//                                            text = characterTitle.value,
//                                            textStyle = simpleTextStyle,
//
//                                            )
//
//                                        Spacer(modifier = Modifier.height(10.dp))
//                                    }
//                                }
//
//                                Column(
//                                    Modifier.padding(start = 10.dp),
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                )
//                                {
//                                    Text(
//                                        modifier = Modifier.padding(bottom = 10.dp),
//                                        textAlign = TextAlign.Center,
//                                        text = "윤리의식",
//                                        fontSize = 15.sp,
//                                        fontFamily = notosanFontFamily,
//                                        fontWeight = FontWeight.Normal
//                                    )
//
//                                    listOptions6.forEach { text ->
//
//                                        val isChecked: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
//                                        val characterTitle: MutableState<String> = rememberSaveable { mutableStateOf(text) }
//                                        val randomColor: MutableState<Int> = rememberSaveable { mutableStateOf(0) }
//
//                                        VlmCheckButton(
//                                            onClick = {
//                                                if(personalityCheckSet.value.size > 5 && !isChecked.value)
//                                                {
//                                                    Toast.makeText(context,"6개 이하로 선택해주세요", Toast.LENGTH_SHORT).show()
//                                                }
//                                                else
//                                                {
//                                                    if(!isChecked.value) personalityCheckSet.value.add(characterTitle.value)
//                                                    else personalityCheckSet.value.remove(characterTitle.value)
//                                                    randomColor.value = setRandomNumber()
//                                                    isChecked.value = !isChecked.value
//                                                }
//                                            },
//                                            isChecked = isChecked,
//                                            contentPadding = PaddingValues(start = 6.dp, top = 4.dp, end = 6.dp, bottom = 4.dp),
//                                            shape = RoundedCornerShape(10.dp),
//                                            border = BorderStroke(1.dp,color = Color.Gray),
//                                            checkedAbleBackGroundColor = colorSeed[randomColor.value],
//                                            text = characterTitle.value,
//                                            textStyle = simpleTextStyle,
//
//                                            )
//
//                                        Spacer(modifier = Modifier.height(10.dp))
//                                    }
//                                }
//                            }
//
//                        }
//
//
//
//                    }
//
//                }
//            }
//
//        }
//    }
//}