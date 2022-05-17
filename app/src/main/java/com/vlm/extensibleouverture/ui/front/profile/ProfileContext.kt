package com.vlm.extensibleouverture.ui.front.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vlm.extensibleouverture.data.UserPrivateParkGolf
import com.vlm.extensibleouverture.data.UserPublicParkGolf
import com.vlm.vlmlogin.modules.viewmodels.UserViewModelInterface

//@Preview(showBackground = true)
//@Composable
//fun ProfileContextPreview(){
//    FakeParkGolfUserViewModel.userBasicData.value?.fid = "NotInvalid"
//    FakeParkGolfUserViewModel.userBasicData.value?.nickname = "닉네임들어감"
//    ProfileContext(FakeParkGolfUserViewModel)
//}

@Composable
fun ProfileContext(userViewModel : UserViewModelInterface<UserPublicParkGolf, UserPrivateParkGolf>) {
    Column(Modifier.clickable { }) {
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
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    text = userViewModel.userBasicData.value?.nickname?:"알수 없음"
                )

            }
        }

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
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.33f)
            ) {

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            style = MaterialTheme.typography.body1,
                            text = "포인트"
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(
                            style = MaterialTheme.typography.body1,
                            text = "1273"
                        )
                    }
                }
            }

            Box(
                modifier =
                Modifier
//                        .background(color = Indigo50)
                    .height(110.dp)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.33f)
            )
            {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            style = MaterialTheme.typography.body1,
                            text = "매칭 회수"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            style = MaterialTheme.typography.body1,
                            text = "1273"
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .height(110.dp)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.33f)
            )  {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            style = MaterialTheme.typography.body1,
                            text = "받은 좋아요"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            style = MaterialTheme.typography.body1,
                            text = "1273"
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        )
        {
            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 15.dp),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Normal,
                    text =  "성별 : ${if(userViewModel.userBasicData.value?.gender == 1) {"남성"} else { "여성" }}"
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    modifier = Modifier
                        .padding(start = 15.dp),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Normal,
                    text =  "소속 클럽 : ${if(userViewModel.userBasicData.value?.affiliate == "") { "없 음" } else {userViewModel.userBasicData.value?.affiliate}}"
                )
            }

        }
        Spacer(Modifier.height(5.dp))
    }
}