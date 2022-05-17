package com.vlm.extensibleouverture.user.signUp

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vlm.extensibleouverture.data.UserPrivateParkGolf
import com.vlm.extensibleouverture.data.UserPublicParkGolf
import com.vlm.extensibleouverture.viewmodels.SignUpLibraryViewModel
import com.vlm.extensibleouverture.viewmodels.UserViewModelParkGolf
import com.vlm.vlmlibrary.*
import com.vlm.vlmlibrary.compose.dialog.BasicConfirmDialog
import com.vlm.vlmlibrary.compose.dialog.ConfirmDialogWithContext
import com.vlm.vlmlibrary.compose.notosanFontFamily
import com.vlm.vlmlibrary.compose.theme.ColorWhite50TransParent
import com.vlm.vlmlogin.objects.GlobalAppInfo
import com.vlm.vlmlogin.objects.GlobalFirebase
import com.vlm.vlmlogin.objects.GlobalFirebase.fireauth
import com.vlm.vlmlogin.signUp.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Integer.max




//로그인 순서
/** (필수) : 닉네임 -> 생일 -> 폰넘버 -> 성별 -> 키 -> 사진등록 ->
 *  (옵션) 옵션인트로 ->담배 -> 술 -> 체형 -> 종교 -> 혈액형 ->  MBTI ->
 *           직업 -> 학력 -> 전공 -> 취미 -> 성격 -> 자기소개 **/

enum class SignUpPage{
    Nickname, Birthday, PhoneNumber, Gender, ImageUpload,
    OptionIntro, Smoke, Drinking, Body, Religion, BloodType, Height, MBTI,
    Job, Hobby, Major, School, SelfIntroduce, Character
}


@ExperimentalCoroutinesApi
@SuppressLint("MutableCollectionMutableState")
@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable //signType은 회원가입 도중에 중단되었던 것인지 아닌지를 판별한다
fun SignUpProcess(signType : Int, //0이면 폰인증 안된 최초가입상태, 1이면 인증도중 중단된상태
                  signinLibraryViewModel : SignUpLibraryViewModel,
                  userViewModel : UserViewModelParkGolf,
                  response:(String) -> Unit){

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val selectedPage = rememberSaveable { mutableStateOf(SignUpPage.Nickname) }

    val nickName = rememberSaveable{ mutableStateOf("") }
    val rememberNickLength = rememberSaveable{ mutableStateOf(0) }

    val birthday = rememberSaveable{ mutableStateOf("") }
    val rememberBirthdayLength = rememberSaveable{ mutableStateOf(0) }

    val phoneNumber = rememberSaveable{ mutableStateOf("") }
    val phoneAuthentification = rememberSaveable{ mutableStateOf(false) }
    val rememberPhoneNumberLength = rememberSaveable{ mutableStateOf(0) }

    val resultMsg = rememberSaveable{ mutableStateOf("") }

    val gender = rememberSaveable{ mutableStateOf("") } //"남성", "여성"

    val smoke = rememberSaveable{ mutableStateOf("정보없음") }
    val drinking = rememberSaveable{ mutableStateOf("정보없음") }
    val body = rememberSaveable{ mutableStateOf("정보없음") }

    val religion = rememberSaveable{ mutableStateOf("정보없음") }
    val bloodType = rememberSaveable{ mutableStateOf("정보없음") }
    val mbtiType = rememberSaveable{ mutableStateOf("XXXX") } //MBTI 초기값을 XXXX로 주고 X가 없을때 리턴함

    val height = rememberSaveable{ mutableStateOf("") }
    val rememberHeightLength = rememberSaveable{ mutableStateOf(0) }

    val job = rememberSaveable{ mutableStateOf("") }
    val rememberJobLength = rememberSaveable{ mutableStateOf(0) }

    val school = rememberSaveable{ mutableStateOf("") }
    val rememberSchoolLength = rememberSaveable{ mutableStateOf(0) }

    val major = rememberSaveable{ mutableStateOf("") }
    val rememberMajorLength = rememberSaveable{ mutableStateOf(0) }

    val hobby = rememberSaveable{ mutableStateOf("") }
    val rememberHobbyLength = rememberSaveable{ mutableStateOf(0) }

    val personalityCheckSet = rememberSaveable { mutableStateOf(hashSetOf<String>()) }

    val selfIntroduceText = rememberSaveable{ mutableStateOf("") }
    val selfIntroduceTextLength = rememberSaveable{ mutableStateOf(0) }

    val getPoints = rememberSaveable{ mutableStateOf(0) }

    val phoneNumberComposeInfo = PhoneNumberComposeInfo(
        fid = userViewModel.userFid,
        eid = userViewModel.userEid,
        password = userViewModel.passwordContainer)

    val bonusState = MutableTransitionState(false)

    ///처음으로 돌아가기
    val dialogBackPageEnable = rememberSaveable{ mutableStateOf(false) }
    if(dialogBackPageEnable.value)
    {
        BasicConfirmDialog(
            title = "처음으로 돌아갑니다",
            textSize = 22.sp,
            leftText =  "취소",
            rightText = "확인",
            rightClick = { response("front") },
            leftClick = {  dialogBackPageEnable.value = false },
            onDismissClicked = { dialogBackPageEnable.value = false }
        )
    }

    ///최종 가입
    val dialogSignupEnable = rememberSaveable{ mutableStateOf(false) }
    if(dialogSignupEnable.value)
    {
        ConfirmDialogWithContext(
            title = "입력하신 정보로 가입합니다",
            context = "${getPoints.value} 볼루니를 받을수 있어요",
            contextArrangement = Arrangement.Center,
            leftText = "취소",
            rightText = "확인",
            rightClick = {

                /**basic Info**/
                if(userViewModel.userBasicData.value == null){
                    userViewModel.userBasicData.value = UserPublicParkGolf(fid = userViewModel.userFid)
                }


                userViewModel.userBasicData.value?.fid = userViewModel.userFid
                userViewModel.userBasicData.value?.eid = userViewModel.userEid

                userViewModel.userBasicData.value?.nickname = nickName.value
                userViewModel.userBasicData.value?.birthday = if(birthday.value == "") 20001212 else birthday.value.toInt()
                userViewModel.userBasicData.value?.gender   = if(gender.value == "남성") 1 else 2
                userViewModel.userBasicData.value?.smoking  = if(smoke.value == "흡연") 1 else 2
                userViewModel.userBasicData.value?.drinking = when(drinking.value){
                    "안마셔요" -> 1
                    "가끔 마셔요" -> 2
                    "남들만큼 마셔요" -> 3
                    "술을 좋아해요" -> 4
                    else -> 1
                }
                userViewModel.userBasicData.value?.body = when(body.value){
                    "사다리꼴" -> 1
                    "모래시계" -> 2
                    "직사각형" -> 3
                    "역삼각형" -> 4
                    "삼각형" -> 5
                    "둥근형" -> 6
                    "타원형" -> 7
                    else -> 1
                }
                userViewModel.userBasicData.value?.religion = when(religion.value){
                    "종교없음" -> 1
                    "불교" -> 2
                    "개신교" -> 3
                    "천주교" -> 4
                    "원불교" -> 5
                    "유교" -> 6
                    "천도교" -> 7
                    "기타" ->  8
                    else -> 1
                }
                userViewModel.userBasicData.value?.bloodType = bloodType.value
                userViewModel.userBasicData.value?.mbti = mbtiType.value
                userViewModel.userBasicData.value?.tall = if(height.value == "0") 0 else height.value.toInt()
                userViewModel.userBasicData.value?.job = job.value
                userViewModel.userBasicData.value?.lastSchool = school.value
                userViewModel.userBasicData.value?.major = major.value
                userViewModel.userBasicData.value?.hobby = hobby.value
                userViewModel.userBasicData.value?.personality = //에러 방지
                    if(personalityCheckSet.value.toString() == "") ""
                    else personalityCheckSet.value.toString().replace("[","").replace("]","") // "[item1, item2]" 에서 [ ] 제거
                userViewModel.userBasicData.value?.selfIntroduce = selfIntroduceText.value
                userViewModel.userBasicData.value?.certificationPhone = 2 //휴대폰 인증, 2면 인증이다
                userViewModel.userBasicData.value?.basic_info_confirmed = true //이제는 안쓰게될지 모르지만 일단은 넣어둠

                /**private Info**/
                if(userViewModel.userPrivateData.value == null){
                    userViewModel.userPrivateData.value = UserPrivateParkGolf(fid = userViewModel.userFid)
                }
                userViewModel.userPrivateData.value?.fid = userViewModel.userFid
                userViewModel.userPrivateData.value?.eid = userViewModel.userEid
                userViewModel.userPrivateData.value?.storePoint = getPoints.value
                userViewModel.userPrivateData.value?.fid = userViewModel.userFid


                /** 앱 상태를 확인하고 state가 4면 현재 초기 출시 상태이므로 포인트를 더 준다*/
                GlobalFirebase.colRefAppInformation.get().addOnSuccessListener { docSnap ->
                    try {
                        GlobalAppInfo.appState = docSnap.getLong("appState")!!.toInt()
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                        GlobalAppInfo.appState = 0
                    }

                    if(GlobalAppInfo.appState == 4)
                    {
//                        userPrivatedata.storePoint = getPoints.value + LoginObject.ADDITIONAL_POINT
//                        Toast.makeText(context,"신규 출시로 인한 볼루니 250개를 \n 추가로 드려요", Toast.LENGTH_SHORT).show()
                    }

                    //최종등록
                    coroutineScope.launch {
                        userViewModel.runSignin(context = context, userViewModel = userViewModel)
                    }


                }.addOnFailureListener {
                    Firebase.crashlytics.recordException(it)
                }

            },
            leftClick = { dialogSignupEnable.value = false },
            onDismissClicked = { dialogSignupEnable.value = false }
        )
    }

    //애니메이션 효과
    LaunchedEffect(key1 = getPoints.value) {
        if(getPoints.value != 0)
        {
            bonusState.apply { targetState = true }
            delay(1500L)
            bonusState.apply { targetState = false }
        }
    }


    //포인트 지급
    when(selectedPage.value)
    {
        SignUpPage.Smoke -> { getPoints.value = max(getPoints.value, 0) }
        SignUpPage.Drinking -> { getPoints.value = max(getPoints.value, 1) }
        SignUpPage.Body -> { getPoints.value = max(getPoints.value, 2) }
        SignUpPage.Religion -> { getPoints.value = max(getPoints.value, 3) }
        SignUpPage.BloodType -> { getPoints.value = max(getPoints.value, 4) }
        SignUpPage.MBTI -> {getPoints.value = max(getPoints.value, 5) }
        SignUpPage.Job -> { getPoints.value = max(getPoints.value, 6) }
        SignUpPage.School -> { getPoints.value = max(getPoints.value, 7) }
        SignUpPage.Major -> { getPoints.value = max(getPoints.value, 8) }
        SignUpPage.Hobby -> { getPoints.value = max(getPoints.value, 10) }
        SignUpPage.Character -> { getPoints.value = max(getPoints.value, 12) }
        SignUpPage.SelfIntroduce -> { getPoints.value = max(getPoints.value, 15) }
        else -> { }
    }


    Box(Modifier.fillMaxSize()){

        when(selectedPage.value)
        {
            SignUpPage.Nickname -> {
                SignupNickName(
                    nickName = nickName,
                    rememberTextLength = rememberNickLength,
                    backPage = { dialogBackPageEnable.value = true  },
                    nextPage = { selectedPage.value = SignUpPage.Birthday }
                )
            }

            SignUpPage.Birthday -> {
                SignupBirthday(
                    birthday = birthday,
                    rememberTextLength = rememberBirthdayLength,
                    backPage = { selectedPage.value = SignUpPage.Nickname },
                    nextPage = {
                        if(signType == 0 && !phoneAuthentification.value) { selectedPage.value =
                            SignUpPage.PhoneNumber
                        } //0이면 폰인증 안된 최초가입상태
                        else{ selectedPage.value = SignUpPage.Gender
                        } //1이면 폰인증은 완료된 가입 계속진행 상태
                    })
            }

            SignUpPage.PhoneNumber -> {
                SignupPhoneNumber(
                    phonenumberInfo = phoneNumberComposeInfo,
                    phoneNumber = phoneNumber,
                    resultMsg = resultMsg,
                    rememberTextLength = rememberPhoneNumberLength,
                    backPage = {
                        selectedPage.value = SignUpPage.Birthday
                   },
                    nextPage = {
                        phoneAuthentification.value = true
                        /*** 중요 : [SignupPhoneNumber]에서 Firebase auth 등록이 완료 되므로 fid가 최초로 생기는 지점이 된다
                         *  따라서 이곳에서 viewmodel - user 부분의 fid를 업데이트 해주어야 fid를 필요로 하는 다음 process가 진행된다
                         ***/
                        /*** 중요 : [SignupPhoneNumber]에서 Firebase auth 등록이 완료 되므로 fid가 최초로 생기는 지점이 된다
                         *  따라서 이곳에서 viewmodel - user 부분의 fid를 업데이트 해주어야 fid를 필요로 하는 다음 process가 진행된다
                         ***/
                        userViewModel.userFid = fireauth.currentUser?.uid?:"null"
                        selectedPage.value = SignUpPage.Gender
                    })
            }

            SignUpPage.Gender -> {
                SignupGender(
                    selectedOption = gender,
                    backPage = {
                        if(signType == 0 && !phoneAuthentification.value) { selectedPage.value =
                            SignUpPage.PhoneNumber
                        } //0이면 폰인증 안된 최초가입상태
                        else{ selectedPage.value = SignUpPage.Birthday
                        } //1이면 폰인증은 완료된 가입 계속진행 상태
                    },
                    nextPage = { selectedPage.value = SignUpPage.Height })
            }


            SignUpPage.Height -> {
                SignupOptionHeight(
                    selectedOption = height,
                    rememberTextLength = rememberHeightLength,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Gender },
                    nextPage = { selectedPage.value = SignUpPage.ImageUpload })
            }


            SignUpPage.ImageUpload -> {
                SignupOptionImageUpload(
                    fid = userViewModel.userFid,
                    signupLibraryViewModel = signinLibraryViewModel,
//                    selectedOption = gender,
                    backPage = { selectedPage.value = SignUpPage.Height },
                    nextPage = { selectedPage.value = SignUpPage.OptionIntro })
            }

            /**아래 부분부터는 옵션으로 적용**/

            SignUpPage.OptionIntro -> {
                SignupOptionIntro(
                    backPage = { selectedPage.value = SignUpPage.ImageUpload },
                    nextPage = { selectedPage.value = SignUpPage.Smoke })
            }


            SignUpPage.Smoke -> {
                SignupOptionSmoke(
                    selectedOption = smoke,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.ImageUpload },
                    nextPage = { selectedPage.value = SignUpPage.Drinking })
            }

            SignUpPage.Drinking -> {
                SignupOptionDrinking(
                    selectedOption = drinking,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Smoke },
                    nextPage = { selectedPage.value = SignUpPage.Body })
            }


            SignUpPage.Body -> {
                SignupOptionBody(
                    selectedOption = body,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Drinking },
                    nextPage = { selectedPage.value = SignUpPage.Religion })
            }

            SignUpPage.Religion -> {
                SignupOptionReligion(
                    selectedOption = religion,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Body },
                    nextPage = { selectedPage.value = SignUpPage.BloodType })
            }

            SignUpPage.BloodType -> {
                SignupOptionBloodType(
                    selectedOption = bloodType,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Religion },
                    nextPage = { selectedPage.value = SignUpPage.MBTI })
            }

            SignUpPage.MBTI -> {
                SignupOptionMBTItype(
                    selectedOption = mbtiType,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.BloodType },
                    nextPage = { selectedPage.value = SignUpPage.Job })
            }

            SignUpPage.Job -> {
                SignupOptionJob(
                    selectedOption = job,
                    rememberTextLength = rememberJobLength,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.MBTI },
                    nextPage = { selectedPage.value = SignUpPage.School })
            }

            SignUpPage.School -> {
                SignupOptionSchool(
                    selectedOption = school,
                    rememberTextLength = rememberSchoolLength,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Job },
                    nextPage = { selectedPage.value = SignUpPage.Major })
            }

            SignUpPage.Major -> {
                SignupOptionMajor(
                    selectedOption = major,
                    rememberTextLength = rememberMajorLength,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.School },
                    nextPage = { selectedPage.value = SignUpPage.Hobby })
            }

            SignUpPage.Hobby -> {
                SignupOptionHobby(
                    selectedOption = hobby,
                    rememberTextLength = rememberHobbyLength,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Major },
                    nextPage = { selectedPage.value = SignUpPage.Character })
            }

            SignUpPage.Character -> {
                SignupOptionCharacter(
                    personalityCheckSet = personalityCheckSet,
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Hobby },
                    nextPage = { selectedPage.value = SignUpPage.SelfIntroduce })
            }

            SignUpPage.SelfIntroduce -> {
                SignupOptionSelfIntroduce(
                    selfIntroduceText = selfIntroduceText,
                    rememberTextLength = selfIntroduceTextLength,
                    backPage = { selectedPage.value = SignUpPage.Character },
                    exit = { /*최종 가입 과정 호출*/dialogSignupEnable.value = true },
                    nextPage = {/*최종 가입 과정 호출*/dialogSignupEnable.value = true })
            }
            else -> {

            }
        }


        AnimatedVisibility(
            visibleState = bonusState,
            enter = fadeIn(animationSpec = tween(1000, 0)),
            exit = fadeOut(animationSpec = tween(1000, 0))
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Box(Modifier.background(ColorWhite50TransParent)){
                    Image(
                        imageVector = ImageVector.vectorResource(id = com.vlm.vlmlibrary.R.drawable.ic_ico_balloony),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .padding(top = 20.dp, end = 35.dp)
                    )
                    Text(
                        modifier = Modifier.padding(start = 35.dp, top = 22.dp, end = 35.dp),
                        textAlign = TextAlign.Center,
                        text = "${getPoints.value} point 지급",
                        fontSize = 15.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

    }


    //뒤로가기
    BackHandler(enabled = true){

        when(selectedPage.value)
        {
            SignUpPage.Nickname -> {
                dialogBackPageEnable.value = true
            }
            SignUpPage.Birthday -> {
                selectedPage.value = SignUpPage.Nickname
            }

            SignUpPage.PhoneNumber -> {
                selectedPage.value = SignUpPage.Birthday
            }

            SignUpPage.Gender -> {
                if(signType == 0 && !phoneAuthentification.value) { selectedPage.value =
                    SignUpPage.PhoneNumber
                } //0이면 폰인증 안된 최초가입상태
                else{ selectedPage.value = SignUpPage.Birthday
                } //1이면 폰인증은 완료된 가입 계속진행 상태
            }

            SignUpPage.Height -> {
                selectedPage.value = SignUpPage.Gender
            }

            SignUpPage.ImageUpload -> {
                selectedPage.value = SignUpPage.Gender
            }

            SignUpPage.Smoke -> {
                selectedPage.value = SignUpPage.ImageUpload
            }

            SignUpPage.Drinking -> {
                selectedPage.value = SignUpPage.Smoke
            }

            SignUpPage.Body -> {
                selectedPage.value = SignUpPage.Drinking
            }

            SignUpPage.Religion -> {
                selectedPage.value = SignUpPage.Body
            }

            SignUpPage.BloodType -> {
                selectedPage.value = SignUpPage.Religion
            }

            SignUpPage.MBTI -> {
                selectedPage.value = SignUpPage.BloodType
            }

            SignUpPage.Job -> {
                selectedPage.value = SignUpPage.MBTI
            }

            SignUpPage.School -> {
                selectedPage.value = SignUpPage.Job
            }

            SignUpPage.Major -> {
                selectedPage.value = SignUpPage.School
            }

            SignUpPage.Hobby -> {
                selectedPage.value = SignUpPage.Major
            }


            SignUpPage.Character -> {
                selectedPage.value = SignUpPage.Hobby
            }


            SignUpPage.SelfIntroduce -> {
                selectedPage.value = SignUpPage.Character
            }

            else -> {}
        }
    }




}
