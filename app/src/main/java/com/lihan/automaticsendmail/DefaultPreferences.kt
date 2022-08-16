package com.lihan.automaticsendmail

import android.content.SharedPreferences

class DefaultPreferences(
    private val sharePreferences : SharedPreferences
) : Preference{
    override fun loadSenderEmail(): String {
        return sharePreferences.getString(Preference.KEY_SENDER_EMAIL,"")?:""
    }

    override fun saveSenderEmail(senderMail: String) {
        sharePreferences.edit()
            .putString(Preference.KEY_SENDER_EMAIL,senderMail)
            .apply()
    }

    override fun loadSenderPassword(): String {
        return sharePreferences
            .getString(Preference.KEY_SENDER_PASSWORD,"")?:""
    }

    override fun saveSenderPassword(password: String) {
        sharePreferences.edit()
            .putString(Preference.KEY_SENDER_PASSWORD,password)
            .apply()
    }

    override fun loadSenderTimes(): Int {
       return sharePreferences.getInt(Preference.KEY_SENDER_TIMES,0)
    }

    override fun saveSenderTimes(times: Int) {
        sharePreferences.edit()
            .putInt(Preference.KEY_SENDER_TIMES,times)
            .apply()
    }

    override fun loadReceiverMail(): String {
        return sharePreferences.getString(Preference.KEY_RECEIVER_EMAIL,"")?:""
    }

    override fun saveReceiverMail(receiverMail: String) {
        sharePreferences.edit()
            .putString(Preference.KEY_RECEIVER_EMAIL,receiverMail)
            .apply()
    }

    override fun loadSubject(): String {
        return sharePreferences.getString(Preference.KEY_SUBJECT,"")?:""
    }

    override fun saveSubject(subject: String) {
        sharePreferences.edit()
            .putString(Preference.KEY_SUBJECT,subject)
            .apply()
    }

    override fun loadBody(): String {
       return sharePreferences.getString(Preference.KEY_BODY,"")?:""
    }

    override fun saveBody(body: String) {
       sharePreferences.edit()
           .putString(Preference.KEY_BODY,body)
           .apply()
    }
}