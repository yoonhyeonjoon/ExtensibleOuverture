package com.vlm.extensibleouverture

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.vlm.extensibleouverture.viewmodels.FrontViewModel
import com.vlm.extensibleouverture.viewmodels.SignUpLibraryViewModel
import com.vlm.extensibleouverture.viewmodels.UserViewModelParkGolf
import com.yalantis.ucrop.UCrop


class MainActivity : ComponentActivity() {

    private val signinLibraryViewModel : SignUpLibraryViewModel by viewModels()
    private val userViewModel : UserViewModelParkGolf by viewModels()
    private val frontViewModel : FrontViewModel by viewModels()

    //이 과정은 부득이하게 Ucrop Result를 [onActivityResult]로 받기때문에 사용함
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 70) {
                data?.let {
                    UCrop.getOutput(it)?.let { aUri ->
                        signinLibraryViewModel.addImageUri(uri = aUri) { response ->
                            if(!response)
                                Toast.makeText(this, "이미지를 최대 6개까지 업로드할 수 있어요", Toast.LENGTH_LONG).show()
                        }
                        signinLibraryViewModel.increaseCounter()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }

    }
}
