package com.lihan.automaticsendmail.presention

sealed class SendMailEvent{
    data class SenderMailChanged(val senderEmail : String) : SendMailEvent()
    data class SendPasswordChanged(val password : String) : SendMailEvent()
    data class ReceiverMailChanged(val receiverMail: String) : SendMailEvent()
    data class SubjectChanged(val subject : String ) : SendMailEvent()
    data class BodyChanged(val body : String) : SendMailEvent()
    data class SendTimes(val times : Int ) : SendMailEvent()
    object Send : SendMailEvent()
}
