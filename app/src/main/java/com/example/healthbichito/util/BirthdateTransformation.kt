package com.example.healthbichito.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class BirthdateTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val input = text.text.filter { it.isDigit() }  // Solo números

        val formatted = formatInput(input)

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 4 -> offset
                    offset <= 6 -> offset + 1
                    offset <= 8 -> offset + 2
                    else -> formatted.length
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 4 -> offset
                    offset <= 7 -> offset - 1
                    offset <= 10 -> offset - 2
                    else -> input.length
                }
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    companion object {


        fun formatInput(input: String): String {
            return when {
                input.length <= 4 -> input
                input.length <= 6 -> "${input.substring(0, 4)}-${input.substring(4)}"
                input.length <= 8 -> "${input.substring(0, 4)}-${input.substring(4, 6)}-${input.substring(6)}"
                else -> "${input.substring(0, 4)}-${input.substring(4, 6)}-${input.substring(6, 8)}"
            }
        }
    }
}
