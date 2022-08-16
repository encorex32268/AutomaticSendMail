package com.lihan.automaticsendmail.presention


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.automaticsendmail.use_case.ValidateEmail
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SendMailViewModel(
    private val validateEmail: ValidateEmail = ValidateEmail()
) : ViewModel(){

    var state by mutableStateOf(SendMailState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: SendMailEvent){
        when(event){
            is SendMailEvent.SenderMailChanged->{
                state = state.copy(
                    senderMail = event.senderEmail
                )
            }
            is SendMailEvent.SendPasswordChanged->{
                state = state.copy(
                    senderPassword = event.password
                )
            }
            is SendMailEvent.ReceiverMailChanged->{
                state = state.copy(
                    receiverMail = event.receiverMail
                )
            }
            is SendMailEvent.SubjectChanged->{
                state = state.copy(
                    subject = event.subject
                )
            }
            is SendMailEvent.BodyChanged->{
                state = state.copy(
                    body = event.body
                )
            }
            is SendMailEvent.SendTimes->{
                state = state.copy(
                    sendTimes = event.times
                )

            }
            is SendMailEvent.Send->{
                sendMail()
            }



        }

    }

    private fun sendMail() {
        val senderMailResult = validateEmail.execute(state.senderMail)
        val receiverMailResult = validateEmail.execute(state.receiverMail)

        val hasError = listOf(
            senderMailResult,
            receiverMailResult
        ).any{ !it.successful}

        if (hasError){
            state = state.copy(
                senderMailErrorMessage = senderMailResult.errorMessage,
                receiverMailErrorMessage = receiverMailResult.errorMessage
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(
                receiverMailErrorMessage = null,
                senderMailErrorMessage = null
            )
            validationEventChannel.send(ValidationEvent.Success)
        }
    }

    sealed class ValidationEvent{
       object Success : ValidationEvent()
    }

}