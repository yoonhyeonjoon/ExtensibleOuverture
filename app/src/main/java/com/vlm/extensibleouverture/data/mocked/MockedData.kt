package com.vlm.extensibleouverture.data.mocked

import com.vlm.extensibleouverture.data.GatheringData
import java.util.*


object GatheringFakeData{

    val currentFakeData = MutableList(10) { GatheringData() }

    val fixedFakeData = MutableList(3) { GatheringData() }
    fun fixedDataSet(){
        fixedFakeData[0] = GatheringData(
            affiliate = "강철군단",
            currentParticipantNum = 1,
            gatheringEndTime = "PM/12/8",
            gatheringStartTime = "PM/23/9",
            golflink = "아이파크골프장",
            location = "진주",
            maxParticipantNum = 4,
            title = "즐거운 파크골프 함께 해요",
            vistorNum = 0,
            writer = "내아이디2",
            writerFid = "2eu5Bw6p0KeH4reyT1dshbrvtGd2")

        fixedFakeData[1] = GatheringData(
            affiliate = "아이리스",
            currentParticipantNum = 1,
            gatheringEndTime = "PM/40/9",
            gatheringStartTime = "PM/20/9",
            golflink = "와룡2파크골프장",
            location = "진주",
            maxParticipantNum = 4,
            title = "즐거운 파크골프 함께 해요",
            vistorNum = 1,
            writer = "vvlam",
            writerFid = "2eu5Bw6p0KeH4reyT1dshbrvtGd4")

        fixedFakeData[2] = GatheringData(
            affiliate = "미래차",
            currentParticipantNum = 1,
            gatheringEndTime = "PM/12/9",
            gatheringStartTime = "PM/15/9",
            golflink = "신탄진파크골프장",
            location = "진주",
            maxParticipantNum = 4,
            title = "즐거운 파크골프 함께 해요",
            vistorNum = 2,
            writer = "우짜께",
            writerFid = "2eu5Bw6p0KeH4reyT1dshbrvtGd3")
    }


    fun generating() {
//        val rannum = (1..100).random()
        for (i in 0 until 10){
            //Date(2000000000000)
            //val time = (2000000000000..2000000050000).random()
            currentFakeData[i] = GatheringData(
                documentFid = UUID.randomUUID().toString(), //      : String = "",
                title = UUID.randomUUID().toString(),
                maxParticipantNum = (1..200).random(),
                currentParticipantNum = (1..200).random(),
                gatheringStartTime = "AM/12/0",
                gatheringEndTime = "AM/12/5",
                writer = UUID.randomUUID().toString(),
                writerFid = UUID.randomUUID().toString(),
                vistorNum = (1..200).random(),
                participantFidList = listOf(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString())
            )
        }
    }

}
