package com.vlm.extensibleouverture.ui.front.gathering

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vlm.extensibleouverture.data.GatheringData
import com.vlm.extensibleouverture.data.mocked.GatheringFakeData
import com.vlm.vlmfirebase.firebase.firebaseTimeConverter

@Preview(showBackground = true)
@Composable
fun FakePreviewGathering(){
    FakeGathering()
}

@Composable
fun FakeGathering() {
    GatheringFakeData.fixedDataSet()
    LazyColumn(
        reverseLayout = false,
        modifier = Modifier
            //            .testTag(ConversationTestTag)
            .padding(top = 15.dp)
            .fillMaxSize()
    ) {
        items(GatheringFakeData.fixedFakeData) { item ->
            GatheringUI(item){

            }
        }


    }
}


@Composable
fun GatheringUI(gatheringData: GatheringData?, response : (gatheringData: GatheringData?) -> Unit) {
    Column(Modifier.clickable{ response(gatheringData) } ) {
        Spacer(Modifier.height(20.dp))
        Divider()
        //title
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
        )
        {
            Row(
                modifier = Modifier
//                        .background(Lime100)
                    .fillMaxSize()
            )
            {
                Text(
                    modifier = Modifier
//                                .background(Lime400)
                        .align(Alignment.CenterVertically)
                        .padding(start = 10.dp),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold,
                    text = gatheringData?.title ?: "제목 없음"
                )

            }
        }

        //sub infro - id/affilliate/date
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
        )
        {
            Row(
                modifier = Modifier
//                        .background(Lime200)
                    .fillMaxSize()
            )
            {

                val dateConverting = firebaseTimeConverter(
                    date = gatheringData?.writingDate,
                    dateFormat = "yyyy.MM.dd"
                )
                val subInfos =
                    "${gatheringData?.writer}님 • ${gatheringData?.affiliate} – ${dateConverting} "
                Text(
                    modifier = Modifier
//                                .background(Lime400)
                        .align(Alignment.CenterVertically)
                        .padding(start = 15.dp),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Normal,
                    text = subInfos
                )

            }
        }

        //location & link name
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
        )
        {
            Row(
                modifier = Modifier
//                        .background(Lime400)
                    .fillMaxSize()
            )
            {
                val subInfos = "${gatheringData?.location} • ${gatheringData?.golflink}"
                Text(
                    modifier = Modifier
//                                .background(Red50)
                        .align(Alignment.CenterVertically)
                        .padding(start = 15.dp),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Normal,
                    text = subInfos
                )

            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            Modifier
//                        .background(color = Purple50)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Box(
                modifier =
                Modifier
//                        .background(color = Indigo50)
                    .height(110.dp)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.5f)
            ) {
                //작성자
                Text(
                    modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold,
                    text = "시작 시간"
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    //AM/12/0
                    val dateConverting = firebaseTimeConverter(
                        date = gatheringData?.gatheringDate,
                        dateFormat = "yyyy.MM.dd"
                    )

                    val ampmCov = when (gatheringData?.gatheringStartTime?.split("/")?.get(0)) {
                        "AM" -> "오전"
                        "PM" -> "오후"
                        else -> "오전"
                    }

                    val textInfo = "$ampmCov ${
                        gatheringData?.gatheringStartTime?.split("/")
                            ?.get(1)
                    }시 ${gatheringData?.gatheringStartTime?.split("/")?.get(2)}분"

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            style = MaterialTheme.typography.body1,
                            text = dateConverting
                        )

                        Text(
                            style = MaterialTheme.typography.body1,
                            text = textInfo
                        )
                    }
                }
            }

            Box(
                modifier =
                Modifier
//                        .background(color = Indigo50)
                    .height(110.dp)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.5f)
            ) {
                //작성자
                Text(
                    modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold,
                    text = "종료 시간"
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    //AM/12/0
                    val dateConverting = firebaseTimeConverter(
                        date = gatheringData?.gatheringDate,
                        dateFormat = "yyyy.MM.dd"
                    )

                    val ampmCov = when (gatheringData?.gatheringEndTime?.split("/")?.get(0)) {
                        "AM" -> "오전"
                        "PM" -> "오후"
                        else -> "오전"
                    }

                    val textInfo = "$ampmCov ${gatheringData?.gatheringEndTime?.split("/")?.get(1)}시 ${gatheringData?.gatheringStartTime?.split("/")?.get(2)}분"

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            style = MaterialTheme.typography.body1,
                            text = dateConverting
                        )

                        Text(
                            style = MaterialTheme.typography.body1,
                            text = textInfo
                        )
                    }
                }
            }

        }


        //participant number
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
        )
        {
            Row(
                modifier = Modifier
//                        .background(Lime400)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.End
            )
            {
                val subInfos = "${gatheringData?.currentParticipantNum} / ${gatheringData?.maxParticipantNum} >"
                Text(
                    modifier = Modifier
//                       .background(Red50)
                        .align(Alignment.CenterVertically)
                        .padding(end = 25.dp),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    text = subInfos
                )

            }
        }
    }
}