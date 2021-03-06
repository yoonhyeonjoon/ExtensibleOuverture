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




//????????? ??????
/** (??????) : ????????? -> ?????? -> ????????? -> ?????? -> ??? -> ???????????? ->
 *  (??????) ??????????????? ->?????? -> ??? -> ?????? -> ?????? -> ????????? ->  MBTI ->
 *           ?????? -> ?????? -> ?????? -> ?????? -> ?????? -> ???????????? **/

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
@Composable //signType??? ???????????? ????????? ??????????????? ????????? ???????????? ????????????
fun SignUpProcess(signType : Int, //0?????? ????????? ?????? ??????????????????, 1?????? ???????????? ???????????????
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

    val gender = rememberSaveable{ mutableStateOf("") } //"??????", "??????"

    val smoke = rememberSaveable{ mutableStateOf("????????????") }
    val drinking = rememberSaveable{ mutableStateOf("????????????") }
    val body = rememberSaveable{ mutableStateOf("????????????") }

    val religion = rememberSaveable{ mutableStateOf("????????????") }
    val bloodType = rememberSaveable{ mutableStateOf("????????????") }
    val mbtiType = rememberSaveable{ mutableStateOf("XXXX") } //MBTI ???????????? XXXX??? ?????? X??? ????????? ?????????

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

    ///???????????? ????????????
    val dialogBackPageEnable = rememberSaveable{ mutableStateOf(false) }
    if(dialogBackPageEnable.value)
    {
        BasicConfirmDialog(
            title = "???????????? ???????????????",
            textSize = 22.sp,
            leftText =  "??????",
            rightText = "??????",
            rightClick = { response("front") },
            leftClick = {  dialogBackPageEnable.value = false },
            onDismissClicked = { dialogBackPageEnable.value = false }
        )
    }

    ///?????? ??????
    val dialogSignupEnable = rememberSaveable{ mutableStateOf(false) }
    if(dialogSignupEnable.value)
    {
        ConfirmDialogWithContext(
            title = "???????????? ????????? ???????????????",
            context = "${getPoints.value} ???????????? ????????? ?????????",
            contextArrangement = Arrangement.Center,
            leftText = "??????",
            rightText = "??????",
            rightClick = {

                /**basic Info**/
                if(userViewModel.userBasicData.value == null){
                    userViewModel.userBasicData.value = UserPublicParkGolf(fid = userViewModel.userFid)
                }


                userViewModel.userBasicData.value?.fid = userViewModel.userFid
                userViewModel.userBasicData.value?.eid = userViewModel.userEid

                userViewModel.userBasicData.value?.nickname = nickName.value
                userViewModel.userBasicData.value?.birthday = if(birthday.value == "") 20001212 else birthday.value.toInt()
                userViewModel.userBasicData.value?.gender   = if(gender.value == "??????") 1 else 2
                userViewModel.userBasicData.value?.smoking  = if(smoke.value == "??????") 1 else 2
                userViewModel.userBasicData.value?.drinking = when(drinking.value){
                    "????????????" -> 1
                    "?????? ?????????" -> 2
                    "???????????? ?????????" -> 3
                    "?????? ????????????" -> 4
                    else -> 1
                }
                userViewModel.userBasicData.value?.body = when(body.value){
                    "????????????" -> 1
                    "????????????" -> 2
                    "????????????" -> 3
                    "????????????" -> 4
                    "?????????" -> 5
                    "?????????" -> 6
                    "?????????" -> 7
                    else -> 1
                }
                userViewModel.userBasicData.value?.religion = when(religion.value){
                    "????????????" -> 1
                    "??????" -> 2
                    "?????????" -> 3
                    "?????????" -> 4
                    "?????????" -> 5
                    "??????" -> 6
                    "?????????" -> 7
                    "??????" ->  8
                    else -> 1
                }
                userViewModel.userBasicData.value?.bloodType = bloodType.value
                userViewModel.userBasicData.value?.mbti = mbtiType.value
                userViewModel.userBasicData.value?.tall = if(height.value == "0") 0 else height.value.toInt()
                userViewModel.userBasicData.value?.job = job.value
                userViewModel.userBasicData.value?.lastSchool = school.value
                userViewModel.userBasicData.value?.major = major.value
                userViewModel.userBasicData.value?.hobby = hobby.value
                userViewModel.userBasicData.value?.personality = //?????? ??????
                    if(personalityCheckSet.value.toString() == "") ""
                    else personalityCheckSet.value.toString().replace("[","").replace("]","") // "[item1, item2]" ?????? [ ] ??????
                userViewModel.userBasicData.value?.selfIntroduce = selfIntroduceText.value
                userViewModel.userBasicData.value?.certificationPhone = 2 //????????? ??????, 2??? ????????????
                userViewModel.userBasicData.value?.basic_info_confirmed = true //????????? ??????????????? ???????????? ????????? ?????????

                /**private Info**/
                if(userViewModel.userPrivateData.value == null){
                    userViewModel.userPrivateData.value = UserPrivateParkGolf(fid = userViewModel.userFid)
                }
                userViewModel.userPrivateData.value?.fid = userViewModel.userFid
                userViewModel.userPrivateData.value?.eid = userViewModel.userEid
                userViewModel.userPrivateData.value?.storePoint = getPoints.value
                userViewModel.userPrivateData.value?.fid = userViewModel.userFid


                /** ??? ????????? ???????????? state??? 4??? ?????? ?????? ?????? ??????????????? ???????????? ??? ??????*/
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
//                        Toast.makeText(context,"?????? ????????? ?????? ????????? 250?????? \n ????????? ?????????", Toast.LENGTH_SHORT).show()
                    }

                    //????????????
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

    //??????????????? ??????
    LaunchedEffect(key1 = getPoints.value) {
        if(getPoints.value != 0)
        {
            bonusState.apply { targetState = true }
            delay(1500L)
            bonusState.apply { targetState = false }
        }
    }


    //????????? ??????
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
                        } //0?????? ????????? ?????? ??????????????????
                        else{ selectedPage.value = SignUpPage.Gender
                        } //1?????? ???????????? ????????? ?????? ???????????? ??????
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
                        /*** ?????? : [SignupPhoneNumber]?????? Firebase auth ????????? ?????? ????????? fid??? ????????? ????????? ????????? ??????
                         *  ????????? ???????????? viewmodel - user ????????? fid??? ???????????? ???????????? fid??? ????????? ?????? ?????? process??? ????????????
                         ***/
                        /*** ?????? : [SignupPhoneNumber]?????? Firebase auth ????????? ?????? ????????? fid??? ????????? ????????? ????????? ??????
                         *  ????????? ???????????? viewmodel - user ????????? fid??? ???????????? ???????????? fid??? ????????? ?????? ?????? process??? ????????????
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
                        } //0?????? ????????? ?????? ??????????????????
                        else{ selectedPage.value = SignUpPage.Birthday
                        } //1?????? ???????????? ????????? ?????? ???????????? ??????
                    },
                    nextPage = { selectedPage.value = SignUpPage.Height })
            }


            SignUpPage.Height -> {
                SignupOptionHeight(
                    selectedOption = height,
                    rememberTextLength = rememberHeightLength,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
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

            /**?????? ??????????????? ???????????? ??????**/

            SignUpPage.OptionIntro -> {
                SignupOptionIntro(
                    backPage = { selectedPage.value = SignUpPage.ImageUpload },
                    nextPage = { selectedPage.value = SignUpPage.Smoke })
            }


            SignUpPage.Smoke -> {
                SignupOptionSmoke(
                    selectedOption = smoke,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.ImageUpload },
                    nextPage = { selectedPage.value = SignUpPage.Drinking })
            }

            SignUpPage.Drinking -> {
                SignupOptionDrinking(
                    selectedOption = drinking,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Smoke },
                    nextPage = { selectedPage.value = SignUpPage.Body })
            }


            SignUpPage.Body -> {
                SignupOptionBody(
                    selectedOption = body,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Drinking },
                    nextPage = { selectedPage.value = SignUpPage.Religion })
            }

            SignUpPage.Religion -> {
                SignupOptionReligion(
                    selectedOption = religion,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Body },
                    nextPage = { selectedPage.value = SignUpPage.BloodType })
            }

            SignUpPage.BloodType -> {
                SignupOptionBloodType(
                    selectedOption = bloodType,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Religion },
                    nextPage = { selectedPage.value = SignUpPage.MBTI })
            }

            SignUpPage.MBTI -> {
                SignupOptionMBTItype(
                    selectedOption = mbtiType,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.BloodType },
                    nextPage = { selectedPage.value = SignUpPage.Job })
            }

            SignUpPage.Job -> {
                SignupOptionJob(
                    selectedOption = job,
                    rememberTextLength = rememberJobLength,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.MBTI },
                    nextPage = { selectedPage.value = SignUpPage.School })
            }

            SignUpPage.School -> {
                SignupOptionSchool(
                    selectedOption = school,
                    rememberTextLength = rememberSchoolLength,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Job },
                    nextPage = { selectedPage.value = SignUpPage.Major })
            }

            SignUpPage.Major -> {
                SignupOptionMajor(
                    selectedOption = major,
                    rememberTextLength = rememberMajorLength,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.School },
                    nextPage = { selectedPage.value = SignUpPage.Hobby })
            }

            SignUpPage.Hobby -> {
                SignupOptionHobby(
                    selectedOption = hobby,
                    rememberTextLength = rememberHobbyLength,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Major },
                    nextPage = { selectedPage.value = SignUpPage.Character })
            }

            SignUpPage.Character -> {
                SignupOptionCharacter(
                    personalityCheckSet = personalityCheckSet,
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    backPage = { selectedPage.value = SignUpPage.Hobby },
                    nextPage = { selectedPage.value = SignUpPage.SelfIntroduce })
            }

            SignUpPage.SelfIntroduce -> {
                SignupOptionSelfIntroduce(
                    selfIntroduceText = selfIntroduceText,
                    rememberTextLength = selfIntroduceTextLength,
                    backPage = { selectedPage.value = SignUpPage.Character },
                    exit = { /*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true },
                    nextPage = {/*?????? ?????? ?????? ??????*/dialogSignupEnable.value = true })
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
                        text = "${getPoints.value} point ??????",
                        fontSize = 15.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

    }


    //????????????
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
                } //0?????? ????????? ?????? ??????????????????
                else{ selectedPage.value = SignUpPage.Birthday
                } //1?????? ???????????? ????????? ?????? ???????????? ??????
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
