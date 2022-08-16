package com.lihan.automaticsendmail.presention

data class SendMailState(
    val senderMail : String = "",
    val senderMailErrorMessage : String? = "",
    val senderPassword : String = "",
    val receiverMail : String = "",
    val receiverMailErrorMessage : String? = "",
    val subject : String = "",
    val body : String="" ,
    val sendTimes : Int = 0,
)
