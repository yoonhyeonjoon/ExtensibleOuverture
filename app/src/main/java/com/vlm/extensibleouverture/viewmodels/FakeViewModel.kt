package com.vlm.extensibleouverture.viewmodels

import androidx.lifecycle.MutableLiveData
import com.vlm.extensibleouverture.data.UserPrivateParkGolf
import com.vlm.extensibleouverture.data.UserPublicParkGolf
import com.vlm.vlmlogin.modules.viewmodels.FakeUserViewModel

object FakeParkGolfUserViewModel : FakeUserViewModel<UserPublicParkGolf, UserPrivateParkGolf> {
    override var userFid: String = "fakeFid"
    override var userBasicData: MutableLiveData<UserPublicParkGolf> = MutableLiveData<UserPublicParkGolf>()
}