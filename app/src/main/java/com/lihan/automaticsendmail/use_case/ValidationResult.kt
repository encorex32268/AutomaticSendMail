package com.lihan.automaticsendmail.use_case

data class ValidationResult(
    val successful : Boolean,
    val errorMessage : String? = null,

)
