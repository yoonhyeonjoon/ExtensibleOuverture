package com.vlm.extensibleouverture.viewmodels

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vlm.vlmfirebase.firebase.imageFileUpload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignUpLibraryViewModel @Inject constructor() : ViewModel() {

    var currentImageCounter = mutableStateOf(0)

    fun increaseCounter(){ currentImageCounter.value += 1 }
    fun decreaseCounter(){ currentImageCounter.value -= 1 }

    /**imageUriList는 6개가 최대임*/
    var imageUriList = mutableStateOf<List<Uri?>>(List(6){null})

    fun addImageUri(uri : Uri, response : ((Boolean) -> Unit)? = null)  //file:///data/user/0/vlm.naiman/cache/2LcdW8v2ecN7J0AYWAfQTvjhYvr2_20211214173804_0_1.png
    {
        var addChecker = false

        val temporArr = arrayListOf<Uri?>(null,null,null,null,null,null)
        imageUriList.value.forEachIndexed { index, aUri ->
            if(!addChecker)
            {
                if(aUri == null) //null 이면 여기 등록함
                {
                    imageUriList.value.filterNotNull().mapIndexed { i, it ->
                        temporArr[i] = it
                    }

                    //새로운 아이템 등록
                    temporArr[index] = uri
                    imageUriList.value = temporArr

                    //리스폰스
                    response?.let { it(true) }

                    //다른 부분에 이미지 등록 안되도록 동작
                    addChecker = true
               }
            }
        }

        if(!addChecker)
        {
            response?.let { it(false) }
        }

    }

    fun deleteImageUri(index : Int)  //file:///data/user/0/vlm.naiman/cache/2LcdW8v2ecN7J0AYWAfQTvjhYvr2_20211214173804_0_1.png
    {
        fun arranger(arr : ArrayList<Uri?>):ArrayList<Uri?>{

            val checklist = mutableListOf<Int>()
            val newArr = arrayListOf<Uri?>(null,null,null,null,null,null)

            arr.forEachIndexed { index, aUri ->
                if(aUri != null){
                    checklist.add(index)
                }
            }

            /**Uri 변경 알고리즘, 이것을 쓰기위해서는 파일이름도 바꾸어주어야함*/
//            checklist.forEachIndexed { reindex, getItem ->
//                var reCombination = ""
//                arr[getItem].toString().split("_").forEachIndexed { index, sentence ->
//                    //배열 예제
////                    0 = "file:///data/user/0/vlm.naiman/cache/2LcdW8v2ecN7J0AYWAfQTvjhYvr2"
////                    1 = "20211214201824"
////                    2 = "0"  이게 이미지 이름이 된다
////                    3 = "1.png"
//                    reCombination +=
//                    when (index) {
//                        0 -> { sentence }
//                        2 -> { "_$reindex" }
//                        else -> { "_$sentence" }
//                    }
//                }
//                newArr[reindex] = reCombination.toUri()
//            }

            /**Uri 변경 없이 재정렬*/
            checklist.forEachIndexed { reindex, getItem ->
                newArr[reindex] = arr[getItem]
            }

            return newArr
        }

        val arr = arrayListOf<Uri?>(null,null,null,null,null,null)
        imageUriList.value.filterNotNull().mapIndexed { i, it ->
            arr[i] = it
        }

        if(imageUriList.value.size > index)
        {
            //제거
            arr.removeAt(index)
            //재정렬
            imageUriList.value = arranger(arr)
            //카운팅
            decreaseCounter()
        }
    }

    suspend fun uploadToFirebase(fid : String, fileList: MutableList<File>, nextPage : (() -> Unit)? = null) = viewModelScope.launch{

        //fileList로 만들기
        val makeFileList: MutableList<File> = mutableListOf()
        imageUriList.value.forEach{ aUri ->
            if(aUri != null) {
                makeFileList.add(Uri.parse(aUri.toString()).toFile())
            }
        }
        //파일추가
        fileList.clear()
        fileList.addAll(makeFileList)

        //파일 경로 설정 //("userprofile/${userBasicdata.fid}/thumbnail/$imageName")
        val storageRef = FirebaseStorage.getInstance().reference.child("userprofile/$fid")

        val firestorageThumbnailImgRef: StorageReference = storageRef.child("original")
        val firestorageOriginalImgRef: StorageReference = storageRef.child("thumbnail")

        val thumbnailList = firestorageThumbnailImgRef.listAll().await()
        val originalList = firestorageOriginalImgRef.listAll().await()

        /**기존 이미지 삭제**/
        val tasksThumbnail = thumbnailList.items.map {
           launch { it.delete().await() }
        }

        val tasksOriginal = originalList.items.map {
            launch { it.delete().await() }
        }

        tasksThumbnail.joinAll()
        tasksOriginal.joinAll()

        //파일 업로드
        imageFileUpload(
            uniqueId = fid,
            imageFileList = fileList,
            targetStorageReference = storageRef,
            metadata = null
        ){  resultInt, oriL, thumbL->
            if(resultInt*2 == oriL.size + thumbL.size)
            { // <- 사진 등록 성공
//                Toast.makeText(context,"성공.",Toast.LENGTH_SHORT).show()
                nextPage?.let { it() }
            }
            else
            {
//                Toast.makeText(context,"실패.",Toast.LENGTH_SHORT).show()
            }
        }



    }

}