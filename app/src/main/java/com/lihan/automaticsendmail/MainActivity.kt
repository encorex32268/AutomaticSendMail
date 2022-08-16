package com.lihan.automaticsendmail

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.UiThread
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.lihan.automaticsendmail.presention.AppBar
import com.lihan.automaticsendmail.presention.SendMailEvent
import com.lihan.automaticsendmail.presention.SendMailViewModel
import com.lihan.automaticsendmail.ui.theme.AutomaticSendMailTheme
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executor
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutomaticSendMailTheme {
                val context = LocalContext.current
                val viewModel = viewModel<SendMailViewModel>()
                val state = viewModel.state

                val defaultPreferences = DefaultPreferences(
                    context.getSharedPreferences(Preference.SHAREDPREFERENCES,
                        MODE_PRIVATE)
                )
                LaunchedEffect(key1 = Unit){

                    viewModel.onEvent(SendMailEvent.SendTimes(defaultPreferences.loadSenderTimes()))
                    viewModel.onEvent(SendMailEvent.SenderMailChanged(defaultPreferences.loadSenderEmail()))
                    viewModel.onEvent(SendMailEvent.SendPasswordChanged(defaultPreferences.loadSenderPassword()))
                    viewModel.onEvent(SendMailEvent.ReceiverMailChanged(defaultPreferences.loadReceiverMail()))
                    viewModel.onEvent(SendMailEvent.SubjectChanged(defaultPreferences.loadSubject()))
                    viewModel.onEvent(SendMailEvent.BodyChanged(defaultPreferences.loadBody()))

                }

                LaunchedEffect(key1 = context){
                    viewModel.validationEvents.collect{ event->
                        when(event){
                            is SendMailViewModel.ValidationEvent.Success->{
                                val state = viewModel.state
                                val constraints = Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .setRequiresBatteryNotLow(true)
                                val data = Data.Builder().apply {
                                    putString(Preference.KEY_SENDER_EMAIL,state.senderMail)
                                    putString(Preference.KEY_SENDER_PASSWORD,state.senderPassword)
                                    putString(Preference.KEY_RECEIVER_EMAIL,state.receiverMail)
                                    putString(Preference.KEY_BODY,state.body)
                                    putString(Preference.KEY_SUBJECT,state.subject)
                                    putInt(Preference.KEY_SENDER_TIMES,state.sendTimes)
                                }
                                val sendWorker = OneTimeWorkRequest.Builder(SendWorker::class.java)
                                    .setConstraints(constraints.build())
                                    .setInputData(data.build())
                                    .build()

                                val workManager = WorkManager.getInstance(context)
                                workManager.enqueue(sendWorker)
                                workManager.getWorkInfoByIdLiveData(sendWorker.id).observe(this@MainActivity
                                ) {
                                    if (it.outputData.keyValueMap.isNotEmpty()){
                                        val data = it.outputData.keyValueMap
                                        val result = data.getValue("SEND_RESULT")
                                        val reason = data.getValue("MESSAGE")?:""
                                        if (result == true){
                                            runOnUiThread{
                                                Toast.makeText(context,"Success.",Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            runOnUiThread{
                                                Toast.makeText(context,"Fail. $reason",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }


                }
                Scaffold(
                    topBar = {
                        AppBar {
                            viewModel.onEvent(SendMailEvent.Send)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                            TextField(
                                value = state.senderMail,
                                onValueChange = {
                                    defaultPreferences.saveSenderEmail(it)
                                    viewModel.onEvent(SendMailEvent.SenderMailChanged(it))
                                },
                                isError = state.senderMailErrorMessage != null,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(text = "Sender Email")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email
                                )
                            )
                            if (state.senderMailErrorMessage != null){
                                Text(
                                    text = state.senderMailErrorMessage,
                                    color = MaterialTheme.colors.error
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = state.senderPassword,
                                onValueChange = {
                                    defaultPreferences.saveSenderPassword(it)
                                    viewModel.onEvent(SendMailEvent.SendPasswordChanged(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(text = "SenderPassword")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text
                                )
                            )

                        Spacer(modifier = Modifier.height(32.dp))
                        Divider(Modifier.height(1.dp), color = Color.Green , thickness = 5.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = state.sendTimes.toString(),
                            onValueChange = {
                                if (TextUtils.isDigitsOnly(it) && it.isNotBlank()){
                                    defaultPreferences.saveSenderTimes(it.toInt())
                                    viewModel.onEvent(SendMailEvent.SendTimes(it.toInt()))
                                }
                            } ,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(text = "Sender Times")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )



                        TextField(
                            value = state.receiverMail,
                            onValueChange = {
                                defaultPreferences.saveReceiverMail(it)
                                viewModel.onEvent(SendMailEvent.ReceiverMailChanged(it))
                            },
                            isError = state.receiverMailErrorMessage != null,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(text = "Receiver Email")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            )
                        )
                        if (state.receiverMailErrorMessage != null){
                            Text(
                                text = state.receiverMailErrorMessage,
                                color = MaterialTheme.colors.error
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))


                        TextField(
                            value = state.subject,
                            onValueChange = {
                                defaultPreferences.saveSubject(it)
                                viewModel.onEvent(SendMailEvent.SubjectChanged(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(text = "Subject")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            )
                        )
                        TextField(
                            value = state.body,
                            onValueChange = {
                                defaultPreferences.saveBody(it)
                                viewModel.onEvent(SendMailEvent.BodyChanged(it))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                            ,
                            placeholder = {
                                Text(text = "Body")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))





                    }


                }
            }
        }
    }
}
