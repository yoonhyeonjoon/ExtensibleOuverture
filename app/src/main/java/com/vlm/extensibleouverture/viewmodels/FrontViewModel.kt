package com.vlm.extensibleouverture.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.vlm.extensibleouverture.data.FakeGatheringPagingSource
import com.vlm.extensibleouverture.data.GatheringData
import com.vlm.extensibleouverture.data.GatheringPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


interface FrontViewModelInterface {
    fun setGatheringDataRefresh(input : Boolean)
    val gatheringDataRefreshing: MutableStateFlow<Boolean>
    val gatheringDataFlow: kotlinx.coroutines.flow.Flow<PagingData<GatheringData>>
}

object FakeFrontViewModel : ViewModel(), FrontViewModelInterface {
    override fun setGatheringDataRefresh(input: Boolean) { }
    override val gatheringDataRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val gatheringDataFlow = Pager(PagingConfig(10)) {
        FakeGatheringPagingSource()
    }.flow.cachedIn(viewModelScope)
}

@HiltViewModel
class FrontViewModel @Inject constructor() : ViewModel(), FrontViewModelInterface {

    override val gatheringDataFlow = Pager(PagingConfig(10)) {
        GatheringPagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)

    private val _isGatheringDataRefreshing = MutableStateFlow<Boolean>(false)
    override val gatheringDataRefreshing: MutableStateFlow<Boolean> get() =_isGatheringDataRefreshing


    override fun setGatheringDataRefresh(input : Boolean){
        _isGatheringDataRefreshing.value = input
    }

    enum class UploadResponse {
        UploadSuccess, UploadFailure, UploadDataEmpty
    }

    @ExperimentalCoroutinesApi
    fun uploadToFirebase(
        dataList: List<Pair<DocumentReference, Any>>,
        response: (UploadResponse, Exception?) -> Unit) = if(dataList.isNotEmpty()){
            viewModelScope.launch {
                FirebaseFirestore.getInstance().runBatch { batch ->
                    dataList.forEach { data ->
                        batch.set(data.first, data.second, SetOptions.merge())
                    }
                }
                    .addOnSuccessListener {
                        response(UploadResponse.UploadSuccess, null)
                    }
                    .addOnFailureListener { exception ->
                        response(UploadResponse.UploadFailure, exception)
                    }
            }
        }
        else{
            viewModelScope.launch {
                response(UploadResponse.UploadDataEmpty, null)
            }
        }

}



