package com.lihan.automaticsendmail.presention

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lihan.automaticsendmail.R

@Composable
fun AppBar(
    onSendButtonClick : () -> Unit
) {

    TopAppBar(
      title = {
          Text(
              text = stringResource(id = R.string.app_name)
          )
      },
      actions = {
          IconButton(onClick = { onSendButtonClick() }) {
               Icon(imageVector = Icons.Default.Send, contentDescription = "send")
          }
      }
    )

}