package com.vlm.extensibleouverture.data

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.vlm.vlmlogin.data.UserPublic


@IgnoreExtraProperties
@Keep
data class UserPublicParkGolf(

    override var fid : String = "",
    override var eid: String = "",

    var nickname: String = "",
    var userState: Int = 0,
    var userWarning: Int = 0,

    var certificationPhone : Int = 2,

    /**preference 정의를 업데이트**/
    var personality: String = "", //밝음,당당함,엉뚱,호탕하다,자유로운 영혼,나긋나긋
    var basic_info_confirmed: Boolean = false,
    var birthday: Int = 0,
    var region: Int = 0,
    var achievement: Int = 0,
    var body: Int = 0,
    var drinking: Int = 0,
    override var gender: Int = 0,
    var hobby: String = "",
    var job: String = "",
    var lastSchool: String = "",
    var religion: Int = 0,
    var selfIntroduce: String = "",
    var smoking: Int = 0,
    var tall: Int = 0,
    var bloodType: String = "",
    var regionDetail: String = "",
    var major: String = "",
    var mbti: String = "",

    var affiliate : String = "" //소속

) : UserPublic
