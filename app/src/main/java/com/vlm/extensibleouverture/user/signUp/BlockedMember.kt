package com.vlm.extensibleouverture.user.signUp

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.util.*


/**
 *    추후에 차단한 대상을 가지고 올때 그 사람의 정보가없으면 지워지는 함수도 만들어야 겠다. 차단된 사람의 아이디 삭제로 인한 함수가 필요할듯
 * */
@Parcelize
data class BlockedMember(
    var blockedFid : String = "",
    var blockedNickname : String = "",
    var blockedDate : Date = Date(1),
    var blockedPhoneNumber : String = "",
    var blockedEmail : String = ""
): Parcelable
{
    companion object {
        const val TAG = "blockedMember"

        /**파이어 베이스에서 들고 들고올때*/
        fun DocumentSnapshot.toBlockedMember() : BlockedMember {
            val blockedFid = getString("blockedFid") ?: ""
            val blockedNickname = getString("blockedNickname") ?: ""
            val blockedDate = getDate("blockedDate") ?: Date(1)
            val blockedPhoneNumber = getString("blockedPhoneNumber") ?: ""
            val blockedEmail = getString("blockedEmail") ?: ""
            return BlockedMember(
                blockedFid = blockedFid ,
                blockedNickname = blockedNickname ,
                blockedDate = blockedDate ,
                blockedPhoneNumber = blockedPhoneNumber ,
                blockedEmail = blockedEmail
            )
        }


    }

}
