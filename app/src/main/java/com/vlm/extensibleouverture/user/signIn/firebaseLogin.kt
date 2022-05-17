package com.vlm.extensibleouverture.user.signIn

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlm.extensibleouverture.viewmodels.UserViewModelParkGolf
import com.vlm.vlmlogin.objects.GlobalFirebase.fireauth
import com.vlm.vlmlogin.signOut.userLoginTimeUpdate

/**firebaseEmailLogin은 파이어베이스 로그인을 해서 데이터를 가져오는 메소드임 메소드임**/
fun firebaseEmailLoginRenewal(
    context : Context, userViewModel : UserViewModelParkGolf,
    email : String, password : String,
    response : (SignInState) -> Unit)
{
    val sharedpreferences : SharedPreferences = context.getSharedPreferences("basicBox", Context.MODE_PRIVATE)
    val editor : SharedPreferences.Editor = sharedpreferences.edit()

    userViewModel.userFid = fireauth.currentUser?.uid?:"" // 이메일을 통한 로그인이 된다.
    userViewModel.userEid = email
    userViewModel.passwordContainer = password

    Firebase.crashlytics.setUserId(userViewModel.userFid)
    
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userViewModel.userFid = fireauth.currentUser?.uid?:""
                //userLoginTimeUpdate()
                if(userViewModel.userFid != "") {
                    val firestore = FirebaseFirestore.getInstance()
                    /**사적 개인 유져 정보를 불러오는 메소드.**/
                    firestore.collection("user_basic").document(userViewModel.userFid).get().addOnSuccessListener {
                        try {
                            if(it.data?.get("fid") ?: "" == "")
                            {
                                editor.putString("logPhone", fireauth.currentUser?.phoneNumber)
//                                println( "number : ${fireauth.currentUser?.phoneNumber}")
                                editor.apply()
                                response(SignInState.DataNull) // 가입절차가 완벽하지 않아서 다시 가입해야함
                            }else{
                                //로그인 시간을 업데이트한다
                                userLoginTimeUpdate(userViewModel.userFid)
                                response(SignInState.Login)// 로그인하면됨
                            }
                        }
                        catch (e:Exception)
                        {
                            Firebase.crashlytics.recordException(e)
                            Toast.makeText(context, "알 수 없는 이유로 로그인에 실패하였습니다(5715) \n 실패가 반복된다면 개발자에게 문의해주세요", Toast.LENGTH_SHORT).show()
                            response(SignInState.False) // 로그인 실패
                        }
                    }.addOnFailureListener {
                        Firebase.crashlytics.recordException(it)
                        Toast.makeText(context, "계정 세부내용이 존재하지 않습니다", Toast.LENGTH_LONG).show()
                        response(SignInState.False)
                    }
                }
                else
                {
                    Toast.makeText(context, "기기내 id 가져오기에 실패하였습니다(6199)", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Firebase.crashlytics.recordException(task.exception?: RuntimeException("null error"))

                when(task.exception)
                {
                    is FirebaseTooManyRequestsException -> {
                        Toast.makeText(context, "서버에서 해당 계정을 차단했습니다(1763).\n 개발자에게 문의해 주세요", Toast.LENGTH_LONG).show()
                        editor.putInt("userPassword", -1)
                        editor.apply()
                        response(SignInState.False)
                    }
                    is FirebaseAuthInvalidUserException -> {  //계정 가입이 안되있을 경우 예외 -> 계정이 없다.
                        response(SignInState.InvalidUser)
                    }
                    is FirebaseAuthInvalidCredentialsException -> { //계정 인증 정보가 틀렸을 경우의 예외 -> 즉 계정은 있다.
                        response(SignInState.InvalidCredential)
                    }
                    else -> {
                        Toast.makeText(context, "로그인에 실패하였습니다 \n 이메일이 같은 다른 로그인 경로가 존재합니다.", Toast.LENGTH_LONG).show()
                        editor.putInt("userPassword", -1)
                        editor.apply()
                        response(SignInState.Else)
                    }
                }
            }
        }
}
