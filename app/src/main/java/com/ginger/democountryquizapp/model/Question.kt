package com.ginger.democountryquizapp.model

data class Question(
        val answerId: Int,
        val countryCode: String,
        val options: List<Option>
)
