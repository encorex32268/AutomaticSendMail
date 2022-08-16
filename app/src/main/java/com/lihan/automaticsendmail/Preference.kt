package com.lihan.automaticsendmail

interface Preference {
    companion object{
        const val SHAREDPREFERENCES="user_info"
        const val KEY_SENDER_EMAIL = "sender_mail"
        const val KEY_SENDER_PASSWORD = "sender_password"
        const val KEY_SENDER_TIMES = "sender_times"
        const val KEY_RECEIVER_EMAIL = "receiver_mail"
        const val KEY_SUBJECT = "subject"
        const val KEY_BODY = "body"
    }

    fun loadSenderEmail() : String
    fun saveSenderEmail(senderMail : String)

    fun loadSenderPassword() : String
    fun saveSenderPassword(password : String)

    fun loadSenderTimes() : Int
    fun saveSenderTimes(times : Int)

    fun loadReceiverMail() : String
    fun saveReceiverMail(receiverMail : String)

    fun loadSubject() : String
    fun saveSubject(subject  :  String)

    fun loadBody() : String
    fun saveBody(body : String)

}