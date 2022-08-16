package com.lihan.automaticsendmail

import android.content.Context
import android.util.Log
import androidx.work.*

class SendWorker(
    context : Context,
    workerParameters: WorkerParameters,
    )  : Worker(context,workerParameters){

    override fun doWork(): Result {
        val senderMail = inputData.getString(Preference.KEY_SENDER_EMAIL)!!
        val senderPassword = inputData.getString(Preference.KEY_SENDER_PASSWORD)!!
        val receiverMail = inputData.getString(Preference.KEY_RECEIVER_EMAIL)!!
        val subject = inputData.getString(Preference.KEY_SUBJECT)!!
        val body = inputData.getString(Preference.KEY_BODY)!!
        val sendTimes = inputData.getInt(Preference.KEY_SENDER_TIMES,0)!!
        var isFailed = false
        var failReason = ""
        repeat(sendTimes){
            GMailSender.Builder()
                .setSenderMail(senderMail)
                .setSenderPassword(senderPassword)
                .setReceiverMail(receiverMail)
                .setSubject(subject)
                .setBody(body)
                .setOnSuccessCallBack {}
                .setOnFailCallBack {
                    isFailed = true
                    failReason = it
                    return@setOnFailCallBack
                }
                .build()
                .send()
        }
        return if (isFailed){
            val outputData = workDataOf(
                "SEND_RESULT" to false,
                "MESSAGE" to failReason
            )
            Result.failure(outputData)
        }else{
            val outputData = workDataOf(
                "SEND_RESULT" to true,
                "MESSAGE" to "SUCCESS"
            )
            Result.success(outputData)
        }

    }
}