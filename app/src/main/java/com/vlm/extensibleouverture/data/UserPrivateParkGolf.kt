package com.vlm.extensibleouverture.data

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.vlm.vlmlogin.data.UserPrivate

@IgnoreExtraProperties
@Keep
data class UserPrivateParkGolf(
    override var fid                    : String = "",
    override var eid                    : String = "",
    var account_confirm                 : Boolean = false,
    var accuracy                        : Double  = 0.0,
    var altitude                        : Double  = 0.0,
    var latitude                        : Double  = 0.0,
    var longitude                       : Double  = 0.0,
    var name                            : String  = "",
    var notifyingAll                    : Boolean = true,
    var notifyingOptionMasterMsg        : Boolean = true,
    var notifyingTodayCard              : Boolean = true,
    var notifyingReceiveCard            : Boolean = true,
    var notifyingMessaging              : Boolean = true,
    var notifyingMapPostingParticipant  : Boolean = true,
    var storePoint                      : Int     = 0,
    var userBlocked                     : Boolean = false
) : UserPrivate {
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

        private const val TAG = "MoGagGongUser"
    }


}
