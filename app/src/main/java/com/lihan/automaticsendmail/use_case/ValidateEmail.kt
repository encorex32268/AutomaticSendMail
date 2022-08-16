package com.lihan.automaticsendmail.use_case

import android.util.Patterns

class ValidateEmail {

    fun execute(mail : String) : ValidationResult {
        if (mail.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = "Please Input Email"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            return ValidationResult(
                successful = false,
                errorMessage = "Format error"
            )
        }
        return ValidationResult(
            successful = true
        )



    }
}