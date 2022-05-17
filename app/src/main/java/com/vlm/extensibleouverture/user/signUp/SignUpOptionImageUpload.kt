package com.vlm.extensibleouverture.user.signUp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.annotation.ExperimentalCoilApi
import com.vlm.extensibleouverture.viewmodels.SignUpLibraryViewModel
import com.vlm.vlmfirebase.firebase.imageFileProcessor
import kotlinx.coroutines.launch
import java.io.File


import com.vlm.extensibleouverture.R
import com.vlm.vlmlibrary.compose.VlmImageMaker
import com.vlm.vlmlibrary.compose.button.DefaultInputButton
import com.vlm.vlmlibrary.compose.design.VlmRoundedCornerBox
import com.vlm.vlmlibrary.compose.dialog.CircularProcessingDialog
import com.vlm.vlmlibrary.compose.dialog.PictureSelectDialog
import com.vlm.vlmlibrary.compose.notosanFontFamily
import com.vlm.vlmlibrary.compose.theme.ColorSomeGray
import com.vlm.vlmlibrary.compose.theme.ColorWhite
import com.vlm.vlmlibrary.date.generateRandAdderRenewal
import com.vlm.vlmlibrary.mediaController.functions.startCrop

@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Preview(showBackground = true, name = "Signup-page-Imageupload")
@Composable
fun SignupImageUpload_(){
    val fid = "testfid"
    SignupOptionImageUpload(fid = fid, signupLibraryViewModel = SignUpLibraryViewModel(), initAnimation = true)
}


@ExperimentalCoilApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun SignupOptionImageUpload(
//    authCodeNumber : MutableState<String>,
//    rememberTextLength : MutableState<Int>,
    fid: String,
    signupLibraryViewModel: SignUpLibraryViewModel,
    initAnimation: Boolean = false,
    exit: (() -> Unit)? = null,
    backPage: (() -> Unit)? = null,
    nextPage: (() -> Unit)? = null,
) {



    //텍스트 길이를 기억
    val context = LocalContext.current

    val imgUriList by signupLibraryViewModel.imageUriList

    val pictureDialogVisible = remember{ mutableStateOf(false) }
    val result = remember { mutableStateOf<File?>(null) }
    val fileList: MutableList<File> = mutableListOf<File>()
    val temporFile = remember { mutableStateOf<Uri?>(null) }
    val currentCounter by signupLibraryViewModel.currentImageCounter
    val progressDialogShow = remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        temporFile.value?.let {
            val path = "file:" + result.value?.absolutePath

            startCrop(
                activity = context as Activity,
                fragment = null,
                imageDataIntent = Uri.parse(path),
                imageDataDestination = Uri.parse(path),
                isThisforAdd = true
            )

        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()){
        if(it.data?.data != null)
        {
            it.data?.let{ intent ->
                imageFileProcessor(
                    intent = intent ,
                    context = context,
                    fileOrder = currentCounter,
                    userFid = fid
                ){ start , dest ->

                    startCrop(
                        activity = context as Activity,
                        fragment = null,
                        imageDataIntent = start,
                        imageDataDestination = dest,
                        isThisforAdd = true
                    )

                    temporFile.value = dest
                }
            }
        }
    }


    val galleryPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if(granted)
            {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                galleryLauncher.launch(intent)
            }
            else
            {
                //Toast.makeText(context," 갤러리 허락 안해줘따",Toast.LENGTH_SHORT).show()
            }
        }
    )

    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(initAnimation).apply { targetState = true } },
        enter = fadeIn(animationSpec = tween(1000, 0)),
        exit = fadeOut()
    ) {

        Box {
            Row(
                Modifier
                    .padding(bottom = 50.dp)
                    .fillMaxSize()
                    .background(ColorWhite))
            {
                Column(Modifier.align(Alignment.CenterVertically)) {

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "셀소개팅용 사진등록",
                        fontSize = 25.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "2장 이상 자신의 사진을 등록해주세요",
                        fontSize = 13.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.height(25.dp))

//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.Center,
//
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                        VlmRoundedCornerBox(
                            onClick = { pictureDialogVisible.value = true },
                            width = 70,
                            height = 70,
                            innerColor = ColorWhite,
                            badgeMode = true,
                            context = {
                                Image(
                                    imageVector = ImageVector.vectorResource(
                                        id = R.drawable.outline_camera_alt_24_black),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(10.dp)
                                )
                            })
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = LocalConfiguration.current.screenWidthDp.dp * 0.28f),
                        modifier = Modifier.padding(horizontal = 10.dp),
                    ) { //                        items(numbers) { photo ->
//                            PhotoItem(photo)
//                        }
                        //                        items(numbers) { photo ->
//                            PhotoItem(photo)
//                        }
                        this.items(6) { index ->

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(modifier = Modifier.height(15.dp))

                                VlmRoundedCornerBox(
                                    onClick = {
                                        pictureDialogVisible.value = true
                                    },
                                    innerColor = ColorWhite,
                                    badgeMode = imgUriList[index] != null,
                                    badgeIcon = {
                                        Icon(
                                            modifier = Modifier.clickable {
                                                signupLibraryViewModel.deleteImageUri(index = index)
                                            },
                                            painter = painterResource(id = R.drawable.baseline_cancel_black_24),
                                            contentDescription = "",
                                            tint = ColorSomeGray,
                                        )
                                    },
                                    context = {
                                        VlmImageMaker(
                                            onClick = { },
                                            uri = imgUriList[index],
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f),
                                        )
                                    })
                            }
                        }//                        items(numbers) { photo ->
                        //                            PhotoItem(photo)
                        //                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End){

                        Column {
//                            if (rememberTextLength.value == 3) {
                                DefaultInputButton(
                                    backgroundColor = Color.Transparent,
                                    textColor = Color.Black,
                                    modifier = Modifier
                                        .padding(top = 50.dp, end = 35.dp)
                                        .align(Alignment.End),
                                    title = "계속하기"
                                ) {
                                    var itemCounting = 0
                                    imgUriList.forEach { if(it != null) itemCounting++ }
                                    if(itemCounting >= 2)
                                    {
                                        progressDialogShow.value = true
                                        coroutineScope.launch {
                                            signupLibraryViewModel.uploadToFirebase(fid = fid, fileList = fileList, nextPage = nextPage)
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(context,"2장 이상 자신의 사진을 등록해주세요", Toast.LENGTH_SHORT).show()
                                    }
                                }
//                            }
                        }
                    }

                }
            }

//            Row(
//                modifier = Modifier.fillMaxSize(),
//                horizontalArrangement = Arrangement.End,
//                verticalAlignment = Alignment.Bottom
//            ) {
//                Column(verticalArrangement = Arrangement.Bottom) {
//                    DefaultInputButton(
//                        backgroundColor = Color.Transparent,
//                        textColor = Color.Black,
//                        modifier = Modifier
//                            .padding(bottom = 100.dp, end = 35.dp)
//                            .align(Alignment.End),
//                        title = "보상 없이 로그인"
//                    ) {
//                        exit?.let { it() }
//                    }
//                }
//            }
        }

    }


    val cameraPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if(granted){
                val storage = context.cacheDir
                val fileNameGenerating = generateRandAdderRenewal(userFid = fid, filename = "${currentCounter}_1", extension = "png")
                result.value = File("$storage/${fileNameGenerating}")
                result.value?.let{
                    temporFile.value = FileProvider.getUriForFile(context, context.packageName!!, result.value!!)
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, temporFile.value)
                    cameraLauncher.launch(intent)
                }
            }
            else
            {
                //Toast.makeText(context," 카메라 허락 안해줘따",Toast.LENGTH_SHORT).show()
            }
        })

    if(pictureDialogVisible.value) {
        PictureSelectDialog(
            onDismissClicked = {
                pictureDialogVisible.value = false
            },
            cameraClicked = {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val storage = context.cacheDir
                    val fileNameGenerating = generateRandAdderRenewal(
                        userFid = fid,
                        filename = "${currentCounter}_1",
                        extension = "png"
                    )
                    result.value = File("${storage}/${fileNameGenerating}")
                    result.value?.let {
                        temporFile.value = FileProvider.getUriForFile(
                            context,
                            context.packageName!!,
                            result.value!!
                        )
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, temporFile.value)
                        cameraLauncher.launch(intent)
                    }
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                pictureDialogVisible.value = false
            },
            galleryClicked = {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = MediaStore.Images.Media.CONTENT_TYPE
                    galleryLauncher.launch(intent)
                } else {
                    galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                pictureDialogVisible.value = false
            }
        )
    }

    if(progressDialogShow.value){
        CircularProcessingDialog {}
    }



}

