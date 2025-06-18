package com.example.travista.data.model





data class AutocompleteResponse(
    val status: String,
    val predictions: List<Prediction>
)

data class Prediction(
    val description: String,
    val place_id: String,
    val structured_formatting: StructuredFormatting
)

data class StructuredFormatting(
    val main_text: String,
    val secondary_text: String
)
