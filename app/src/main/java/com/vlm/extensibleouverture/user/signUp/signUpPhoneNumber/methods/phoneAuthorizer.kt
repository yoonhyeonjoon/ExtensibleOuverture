package com.vlm.extensibleouverture.user.signUp.signUpPhoneNumber.methods

import android.text.TextUtils
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vlm.extensibleouverture.user.signUp.PhoneNumberComposeInfo


//import vlm.utils.a0Objects.GlobalAppInfo


class PhoneAuthorizer
{
    //https://firebase.google.com/docs/auth/android/phone-auth?hl=ko 번호인증 링크 페이지
    // SHA-1 (릴리즈, 디버그 해쉬 키를 등록해야한다. 이를 주의하라)

    var glboalPhoneAuthCredential : PhoneAuthCredential? = null

    fun verifyPhoneNumberWithCode(verificationId: String?, code: String, responseA:((Boolean)->Unit)? = null) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        glboalPhoneAuthCredential = credential
        //Credential이 제대로 된 인증 번호인지를 signUpWithCredential로 증명하고 fireauth에 등록한다
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener { //signUpWithCredential로 계정을 생성한다, 이부분에서 최초생성
            if (responseA != null) { //phoneAuthCheck()
                responseA(true)
            }
        }.addOnFailureListener {
            Firebase.crashlytics.recordException(it)
            if (responseA != null) {
                responseA(false)
            }
        }
    }

    /**Credential을 가지고서 폰번호를 증명하는 코드**/
    fun verifyPhoneNumberWithCredential(credential: PhoneAuthCredential, responseA:((Boolean)->Unit)? = null) {
        glboalPhoneAuthCredential = credential
        //Credential이 제대로 된 인증 번호인지를 signUpWithCredential로 증명하고 fireauth에 등록한다
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener {
            if (responseA != null) { //phoneAuthCheck()
                responseA(true)
            }
        }.addOnFailureListener {
            Firebase.crashlytics.recordException(it)
            if (responseA != null) {
                responseA(false)
            }
        }
    }

//    fun validatePhoneNumber(etPhoneNumber: EditText): Boolean {
//        if (TextUtils.isEmpty(phonenumberChanger(etPhoneNumber))) {
//            etPhoneNumber.error = "적절하지않은 번호 입니다."
//            return false
//        }
//        return true
//    }



//    // [START resend_verification]
//    private fun resendVerificationCode( //resendVerificationCode(fieldPhoneNumber.text.toString(), phoneToken) 사용예제
//        phoneNumber: String,
//        token: PhoneAuthProvider.ForceResendingToken?
//    ) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//            phoneNumber,      // Phone number to verify
//            60,               // Timeout duration
//            TimeUnit.SECONDS, // Unit of timeout
//            activity,             // Activity (for callback binding)
//            callbacks,        // OnVerificationStateChangedCallbacks
//            token)            // ForceResendingToken from callbacks
//    }


    /**이메일은 업체를 통해 자동으로 받아오므로 현재 사용하지않는다.*/
    fun emailvalidateFormChecker(email : String, tvEmail : TextView): Boolean {
        var valid = true
        //val email = m001EtNewMemberJoinEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            tvEmail.error = "이메일을 입력해주세요"
            valid = false
        } else {
            tvEmail.error = null
        }

        return valid
    }

}



/** 폰 번호 인증 과정*/
//전화번호 인증과 동시에 phone등록 -> 이메일 등록
fun verifyProcess(phonenumberInfo : PhoneNumberComposeInfo, getResponse: Boolean, result : (PhoneResponseCode) -> Unit)
{
    if (getResponse)
    {
        val crashlytics = Firebase.crashlytics
        val fireauth = FirebaseAuth.getInstance()
        if (fireauth.currentUser?.email.isNullOrEmpty() && phonenumberInfo.eid != "") {
            fireauth.currentUser?.updateEmail(phonenumberInfo.eid)?.addOnSuccessListener {
                fireauth.currentUser?.updatePassword(phonenumberInfo.password)
                    ?.addOnSuccessListener {
                        result(PhoneResponseCode.Success)
                    }?.addOnFailureListener {//이메일 등록 실패
                        crashlytics.recordException(it)
                        result(PhoneResponseCode.EMAIL_UPDATE_FAILURE)
                    }
            }?.addOnFailureListener {//이메일 실패
                crashlytics.recordException(it)
                result(PhoneResponseCode.EMAIL_UPDATE_FAILURE)
            }
        } else {//이메일 이미 존재
            result(PhoneResponseCode.EMAIL_ALREADY_EXIST)
            crashlytics.recordException(RuntimeException("mail already exist"))
        }
    }
    else
    {
        result(PhoneResponseCode.FIREBASEAUTH_CREDENTIAL_ERROR)
    }
}