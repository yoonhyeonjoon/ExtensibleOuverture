package com.vlm.extensibleouverture.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vlm.extensibleouverture.data.UserPrivateParkGolf
import com.vlm.extensibleouverture.data.UserPublicParkGolf
import com.vlm.vlmlogin.data.UserPrivate
import com.vlm.vlmlogin.data.UserPublic
import com.vlm.vlmlogin.modules.viewmodels.UserViewModel
import com.vlm.vlmlogin.objects.GlobalFirebase.fs_userbasic_info
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModelParkGolf
@Inject constructor() : UserViewModel<UserPublicParkGolf, UserPrivateParkGolf>() {

    override var userFid = ""
    override var userEid = ""
    override var passwordContainer = "" //이 변수가 카카오 구글 페이스북 중에 선택한 아이디를 기억한다

    var userLoginFirst = MutableLiveData<Boolean>(false)
    private fun setUserLoginFirst(value : Boolean) {
        userLoginFirst.value = value
    }

    var certification0Observe = MutableLiveData<Int>()
    private fun setCertification0(value: Int){
        certification0Observe.value = value
    }

    private fun listenToUserData() {
        listenerBasic = fireStore.collection(fs_userbasic_info)
            .document(userFid).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Firebase.crashlytics.recordException(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    try {

                        val userBasicDataSnap = snapshot.toObject(UserPublic::class.java)
                        userBasicDataSnap?.let { setUserBasicData(it as UserPublicParkGolf) }


                    }catch (e:Exception){
                        Firebase.crashlytics.recordException(e)
                    }
                }
            }

        listenerPrivate = fireStore.collection(fs_userbasic_info)
            .document(userFid).collection("user_private").document("user_private").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Firebase.crashlytics.recordException(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    try
                    {
                        val userPrivateData = snapshot.toObject(UserPrivate::class.java)
                        userPrivateData?.let { setUserPrivateData(it as UserPrivateParkGolf) }
                    }
                    catch (e:Exception){
                        Firebase.crashlytics.recordException(e)
                    }
                }
            }
    }

}