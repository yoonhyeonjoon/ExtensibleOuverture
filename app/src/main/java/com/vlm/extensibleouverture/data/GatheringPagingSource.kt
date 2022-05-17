package com.vlm.extensibleouverture.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.vlm.extensibleouverture.data.mocked.GatheringFakeData.currentFakeData
import kotlinx.coroutines.tasks.await


class GatheringPagingSource(private val db: FirebaseFirestore) : PagingSource<QuerySnapshot, GatheringData>() {

    var initialPage : QuerySnapshot? = null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, GatheringData> {
        return try {

            if(initialPage == null)
            {
                initialPage = params.key ?: FirebaseFirestore.getInstance()
                    .collection("myTest").orderBy("gatheringStartTime", Query.Direction.DESCENDING).limit(10).get().await()
            }

            //step1.
            val currentPage = params.key ?: db.collection("myTest").orderBy("gatheringStartTime", Query.Direction.DESCENDING).limit(10).get().await()

            //step2.
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            //step3.
            val nextPage = db.collection("myTest").orderBy("gatheringStartTime", Query.Direction.DESCENDING).limit(10).startAfter(lastDocumentSnapshot).get().await()

            LoadResult.Page(
                data = currentPage.toObjects(GatheringData::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, GatheringData>): QuerySnapshot? {
        return initialPage
    }
}


class FakeGatheringPagingSource : PagingSource<List<GatheringData>, GatheringData>() {
    override suspend fun load(params: LoadParams<List<GatheringData>>): LoadResult<List<GatheringData>, GatheringData> {
        return try {
            LoadResult.Page(
                data = currentFakeData,
                prevKey = null,
                nextKey = null //nextkey를 null로 설정
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<List<GatheringData>, GatheringData>): List<GatheringData>? {
        TODO("Not yet implemented")
    }

}