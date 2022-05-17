package com.vlm.extensibleouverture.ui.front.writing

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.AMPMHours
import com.chargemap.compose.numberpicker.Hours
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.vlm.extensibleouverture.data.GatheringData
import com.vlm.extensibleouverture.data.UserPrivateParkGolf
import com.vlm.extensibleouverture.data.UserPublicParkGolf


import com.vlm.vlmfirebase.firebase.firebaseTimeConverter
import com.vlm.vlmlogin.modules.viewmodels.UserViewModelInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


import com.vlm.vlmlibrary.compose.button.DefaultInputButton
import com.vlm.vlmlibrary.compose.calendar.CalendarCaller
import com.vlm.vlmlibrary.compose.editText.EditTextWithTitleDefault
import com.vlm.vlmlibrary.compose.numberPicker.CustomizedHoursNumberPicker
import com.vlm.vlmlibrary.compose.numberPicker.CustomizedListItemPicker

import com.vlm.extensibleouverture.viewmodels.FakeParkGolfUserViewModel
import com.vlm.extensibleouverture.viewmodels.FrontViewModel
import com.vlm.vlmlibrary.compose.notosanFontFamily
import com.vlm.vlmlibrary.compose.numberPicker.CustomizedNumberPicker

import com.vlm.vlmlibrary.compose.theme.Yellow100
import com.vlm.vlmlibrary.compose.theme.Red50

//@Preview(showBackground = true)
//@Composable
//fun CalendarExamplePrev(){
//    CalendarCaller()
//}

@Preview(showBackground = true)
@Composable
fun WritingSketchPreview(modifier: Modifier = Modifier) {
    Writing(userViewModel = FakeParkGolfUserViewModel)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Writing (modifier: Modifier = Modifier, userViewModel : UserViewModelInterface<UserPublicParkGolf, UserPrivateParkGolf>) {

    val frontViewModel = FrontViewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    //현재 시간 처리
    val currentDate = firebaseTimeConverter(Date(), dateFormat = "yyyy/MM/dd/HH/mm/ss")
    val currentYear = currentDate.split("/")[0]
    val currentMonth = currentDate.split("/")[1]
    val currentDay = currentDate.split("/")[2]
    val currentAbsHour = currentDate.split("/")[3].toInt()
    val currentAMPM = if(currentAbsHour > 12) AMPMHours.DayTime.PM  else AMPMHours.DayTime.AM
    val currentHour = if(currentAbsHour > 12) currentAbsHour - 12 else currentAbsHour
    val currentMinute = currentDate.split("/")[4]

    val title = rememberSaveable{ mutableStateOf("") }
    var participantNumber by remember { mutableStateOf(0) }
    var startTime: Hours by remember { mutableStateOf(AMPMHours(currentHour, 0, currentAMPM )) }
    var endTime: Hours by remember { mutableStateOf(AMPMHours(currentHour, 0, currentAMPM )) }

    var companionNumber by rememberSaveable{ mutableStateOf(0) }


    var dayRemember by remember { mutableStateOf(currentDay.toInt()) }
    var monthRemember by remember { mutableStateOf(currentMonth.toInt()) }
    var yearRemember by remember { mutableStateOf(currentYear.toInt()) }


    val golflinkName = listOf(
        "송백파크골프장",
        "신안동남강둔치골프장",
        "와룡파크골프장",
        "와룡2파크골프장",
        "초전파크골프장",
        "대평파크골프장",
        "가방파크골프장",
        "정촌2파크골프장"
    )

    var golflink by remember { mutableStateOf(golflinkName[0]) }

    LazyColumn(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {

        item{

            Spacer(
                modifier
                    .padding(top = 30.dp, bottom = 10.dp, start = 15.dp, end = 15.dp)
                    .background(Red50)
                    .height(1.dp)
                    .fillMaxWidth())

            EditTextWithTitleDefault(
                title  = "제 목",
                titleSize = 23,
                hint  = "즐거운 파크 골프 함께 해요",
                underLine = false,
                padding = 16,
                text = title.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { /*nextPage?.let { it() }*/ }),
                onTextChanged = { response ->
                    title.value = response
                    if (title.value.length > 1000)
                    {
                        //                    Toast.makeText(context,"1000자리 이하로 입력해주세요", Toast.LENGTH_SHORT).show()
                        title.value = response.substring(0 until 1000)
                    }
                }
            )


            Divider(modifier.padding(top = 20.dp, bottom = 10.dp, start = 15.dp, end = 15.dp))


            Row(
                modifier= Modifier.fillMaxWidth()/*.offset(y = (-5).dp)*/,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){

                Column(modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.5f),
                        verticalArrangement = Arrangement.Center
                )
                {


                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "모집인원",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = notosanFontFamily,
                            fontWeight = FontWeight.Bold)

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = if(companionNumber == 0) "동행자 없음" else "(동행자 ${companionNumber}명)",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = notosanFontFamily,
                            fontWeight = FontWeight.Normal)


//                        //Underline
//                        Spacer(Modifier
//                            .padding(top = 2.dp, bottom = 15.dp)
//                            .background(Red200)
//                            .height(3.dp).fillMaxWidth()
//                        )
//                    }
                }

                Column(modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.5f),
                    verticalArrangement = Arrangement.Center
                )
                {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "지역 : 진주",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Bold)

                }
            }

            Row(
                modifier= Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){

                Column(modifier = Modifier.requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.5f),
                    verticalArrangement = Arrangement.Center
                )
                {

                    Box(modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .height(IntrinsicSize.Max)
                        .align(Alignment.CenterHorizontally)/*.background(Lime100)*/){

                        CustomizedNumberPicker( //https://github.com/ChargeMap/Compose-NumberPicker
                            value = participantNumber,
                            range = 3 downTo 1,
                            onValueChange = {
                                participantNumber = it
                                companionNumber = 3 - it
                            }
                        )

                        Row(modifier = Modifier
                            .fillMaxSize()
                            /*.background(Lime100)*/
                        )
                        {
                            Text(
                                modifier = Modifier
                                    .offset(x = 25.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically)
                                /*.background(Lime400)*/,
                                text = "명",
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = notosanFontFamily,
                                fontWeight = FontWeight.Bold)
                        }

                    }
                }

                Column(
                    modifier = Modifier.requiredWidth(LocalConfiguration.current.screenWidthDp.dp * 0.5f),
                    verticalArrangement = Arrangement.Center
                )
                {
                    Box(modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .width(IntrinsicSize.Max)
                        .height(IntrinsicSize.Max)
                        .align(Alignment.CenterHorizontally)/*.background(Lime100)*/){

                        CustomizedListItemPicker( //ListItemPicker(
                            label = { it },
                            value = golflink,
                            onValueChange = { golflink = it },
                            list = golflinkName,
                            textStyle =  TextStyle(
                                fontFamily = notosanFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.W100,
                                lineHeight = 10.sp
                            )
                        )

                    }

                }

            }

            Divider(modifier.padding(top = 10.dp, bottom = 10.dp, start = 15.dp, end = 15.dp))

            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.Center
            ){

                Text(
                    modifier = Modifier.padding(start = 20.dp, end = 1.dp)/*.width(100.dp)*/ /*.background(Lime200)*/,
                    text = "모임 시간",
                    fontSize = 23.sp,
                    textAlign = TextAlign.Start,
                    fontFamily = notosanFontFamily,
                    fontWeight = FontWeight.Bold)

//                //Underline
//                Spacer(Modifier
//                    .padding(start = 20.dp, top = 2.dp, bottom = 15.dp)
//                    .background(Red200)
//                    .height(3.dp).fillMaxWidth()
//                )

            }




            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){

                Column(modifier = Modifier
                    .weight(4f)
                    .fillMaxHeight()/*.background(Purple200)*/)
                {


                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center){

                        Row(modifier = Modifier.weight(8f)/*.background(Lime100)*/)
                        {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)){

                                Column(modifier = Modifier.fillMaxWidth()) {

                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "$yearRemember 년  $monthRemember 월  $dayRemember 일",
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center,
                                        fontFamily = notosanFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(Modifier.height(20.dp))

                                    CalendarCaller(
                                        modifier = Modifier/*.fillMaxWidth()*/.align(Alignment.CenterHorizontally)/*.padding(10.dp)*/,
                                        title = "날짜 선택",
                                        textColor = Color.Black,
                                        backgroundColor = Yellow100){ day, month, year ->
                                        dayRemember = day
                                        monthRemember = month
                                        yearRemember = year





                                    }
                                }

                            }

                        }

                    }
                }

                Column(modifier = Modifier.weight(6f))
                {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp)/*.background(Amber300)*/,
                        text = "시작 시간",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Bold)


                    CustomizedHoursNumberPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        dividersColor = MaterialTheme.colors.primary,
                        value = startTime, //FullHours(12, 30),//, AMPMHours.DayTime.AM), //
                        onValueChange = {
                            startTime = it
                        },
                        hoursDivider = {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center,
                                text = "시"
                            )
                        },
                        minutesDivider = {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center,
                                text = "분"
                            )
                        },
                        textStyle =  TextStyle(
                            fontFamily = notosanFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W100,
                            lineHeight = 10.sp
                        )
                    )



                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)/*.background(Amber300)*/,
                        text = "종료 시간",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Bold)


                    CustomizedHoursNumberPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        dividersColor = MaterialTheme.colors.primary,
                        value = endTime, //FullHours(12, 30),//, AMPMHours.DayTime.AM), //
                        onValueChange = {
                            endTime = it
                        },
                        hoursDivider = {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center,
                                text = "시"
                            )
                        },
                        minutesDivider = {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center,
                                text = "분"
                            )
                        },
                        textStyle =  TextStyle(
                            fontFamily = notosanFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W100,
                            lineHeight = 10.sp
                        )
                    )

                }

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentSize()
                    .padding(24.dp)
            ) {

//                val startTimeParser2 = "${(startTime as AMPMHours).dayTime.name}/${startTime.hours}/${startTime.minutes}"
//                val endTimeParser2 = "${(endTime as AMPMHours).dayTime.name}/${endTime.hours}/${endTime.minutes}"
//                Text("${startTimeParser2} / ${endTimeParser2}")

                DefaultInputButton(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Black,
                    modifier = Modifier
                        .padding(top = 0.dp)
                        .align(Alignment.End),
                    title = "모임 등록")
                {

                    val docPath = FirebaseFirestore.getInstance().collection("myTest").document()

                    if(userViewModel.userFid != "")
                    {

                        //simple process
                        val startTimeParser = "${(startTime as AMPMHours).dayTime.name}/${startTime.hours}/${startTime.minutes}"
                        val endTimeParser = "${(endTime as AMPMHours).dayTime.name}/${endTime.hours}/${endTime.minutes}"
                        val gatheringDate = try {
                            Date.from(LocalDate.of( yearRemember , monthRemember , dayRemember).atStartOfDay(ZoneId.systemDefault()).toInstant())
                        }
                        catch (e:Exception){
                            Date()
                        }


                        //writing
                        val gatheringData = GatheringData(
                            documentFid                    = docPath.id, //: String = "",
                            title                          = if(title.value == "") "즐거운 파크골프 함께 해요" else title.value, //: String = "",
                            maxParticipantNum              = 4, //: Int = -1,
                            gatheringDate                  = gatheringDate,
                            currentParticipantNum          = companionNumber + 1, //: Int = -1,
                            gatheringStartTime             = startTimeParser, //: Date = Date(1),
                            gatheringEndTime               = endTimeParser, //: Date = Date(1),
                            writer                         = userViewModel.userBasicData.value?.nickname?:"알수없음", //: String = "unknown",
                            writerFid                      = userViewModel.userBasicData.value?.fid?:"알수없음", //: String = "unknown",
                            vistorNum                      = 0, //: Int = -1,
                            location                       = "진주", //: String = "unknown",
                            affiliate                      = userViewModel.userBasicData.value?.affiliate?:"소속없음",
                            golflink                       = golflink,
                            participantFidList             = listOf(), //: List<String> = listOf<String>()
                        )

                        frontViewModel.uploadToFirebase(dataList = listOf(docPath to gatheringData.toHashMap())/*List<Pair<DocumentReference, Any>>)*/)
                        { state, response ->
                            when (state) {
                                FrontViewModel.UploadResponse.UploadSuccess -> {
                                    Toast.makeText(context, "등록 되었습니다", Toast.LENGTH_LONG).show()

                                }
                                FrontViewModel.UploadResponse.UploadDataEmpty -> {

                                }
                                FrontViewModel.UploadResponse.UploadFailure -> {
                                    when(response){
                                        is FirebaseFirestoreException -> {
                                            if(response.code.toString() == "PERMISSION_DENIED"){
                                                Toast.makeText(context, "Permission이 거부 되었습니다. 문제가 반복되면 개발자에게 문의바랍니다(6713)", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                }
                            }

                        }

                    }
                    else
                    {
                        Toast.makeText(context, "로그인 후 사용할 수 있습니다", Toast.LENGTH_LONG).show()
                    }




                }
            }
        }
    }

}