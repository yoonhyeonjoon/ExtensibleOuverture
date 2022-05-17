package com.vlm.extensibleouverture.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@IgnoreExtraProperties
@Keep
@Parcelize
data class GatheringData(
    var documentFid                     : String = "",
    var title                           : String = "",
    var maxParticipantNum               : Int = -1,
    var currentParticipantNum           : Int = -1,
    @ServerTimestamp
    val writingDate                     : Date = Date(),
    var gatheringDate                   : Date = Date(),
    var gatheringStartTime              : String = "AM/12/0",
    var gatheringEndTime                : String = "AM/12/0",
    var writer                          : String = "unknown",
    var writerFid                       : String = "unknown",
    var vistorNum                       : Int = -1,
    var location                        : String = "unknown",
    var participantFidList              : List<String> = listOf<String>(),
    var affiliate                       : String = "unknown",
    var golflink                        : String = "unknown",
//  var participantList                 : List<String> = listOf<String>(),
) : Parcelable {

    fun toHashMap() : HashMap<String, Any?>{
        val messages = hashMapOf<String, Any?>()
        messages["writingFid"]             = documentFid
        messages["title"]                  = title
        messages["maxParticipantNum"]      = maxParticipantNum
        messages["currentParticipantNum"]  = currentParticipantNum
        messages["writingDate"]            = FieldValue.serverTimestamp() //글 쓴 시간 등록
        messages["gatheringDate"]          = gatheringDate
        messages["gatheringStartTime"]     = gatheringStartTime
        messages["gatheringEndTime"]       = gatheringEndTime
        messages["writer"]                 = writer
        messages["writerFid"]              = writerFid
        messages["vistorNum"]              = vistorNum
        messages["location"]               = location
        messages["participantFidList"]     = participantFidList
        messages["affiliate"]              = affiliate
        messages["golflink"]               = golflink
        return messages
    }


    companion object{

        fun getData(){
            // val city = documentSnapshot.toObject<City>()
        }

//        fun DocumentSnapshot.toUserPrivate(): UserPrivate?
//        {
//            try{
//                val accountConfirm                   = getBoolean("account_confirm")                 ?: false
//                val accuracy                         = getDouble("accuracy")                         ?: 0.0
//                val altitude                         = getDouble("altitude")                         ?: 0.0
//                val eid                              = getString("eid")                              ?: ""
//                val fid                              = getString("fcmtoken")                         ?: ""
//                val latitude                         = getDouble("latitude")                         ?: 0.0
//                val longitude                        = getDouble("longitude")                        ?: 0.0
//                val name                             = getString("name")                             ?: ""
//                val notifyingAll                     = getBoolean("notifyingAll")                    ?: true
//                val notifyingOptionMasterMsg         = getBoolean("notifyingOptionMasterMsg")        ?: true
//                val notifyingMessaging               = getBoolean("notifyingMessaging")              ?: true
//                val notifyingMapPostingParticipant   = getBoolean("notifyingMapPostingParticipant")  ?: true
//                val notifyingReceiveCard             = getBoolean("notifyingReceiveCard")            ?: true
//                val notifyingTodayCard               = getBoolean("notifyingTodayCard")              ?: true
//                val storePoint                       = getLong("storePoint")?.toInt()                ?: 0
//                val userBlocked                      = getBoolean("userBlocked")                     ?: false
//
//                return UserPrivate(
//                    account_confirm          =  accountConfirm              ,
//                    accuracy                 =  accuracy                    ,
//                    altitude                 =  altitude                    ,
//                    eid                      =  eid                         ,
//                    fid                      =  fid                         ,
//                    latitude                 =  latitude                    ,
//                    longitude                =  longitude                   ,
//                    name                     =  name                        ,
//                    notifyingAll             =  notifyingAll                ,
//                    notifyingOptionMasterMsg =  notifyingOptionMasterMsg    ,
//                    notifyingMapPostingParticipant = notifyingMapPostingParticipant,
//                    notifyingMessaging       =  notifyingMessaging          ,
//                    notifyingReceiveCard     =  notifyingReceiveCard        ,
//                    notifyingTodayCard       =  notifyingTodayCard          ,
//                    storePoint               =  storePoint                  ,
//                    userBlocked            =  userBlocked
//                )
//            }catch (e:Exception){
//                Log.e(TAG," Error converting MoGagGongUser",e)
//                return null
//            }
//        }

    }


}
