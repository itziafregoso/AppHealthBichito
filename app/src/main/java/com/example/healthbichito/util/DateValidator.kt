package com.example.healthbichito.util

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Valida una fecha de nacimiento en formato AAAA-MM-DD y calcula edad.
 */
object DateValidator {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    data class BirthdateResult(
        val valid: Boolean,
        val age: Int = -1,
        val error: String = ""
    )

    fun validateBirthdate(input: String): BirthdateResult {
        // No completos los 10 caracteres → todavía no validar
        if (input.length < 10) {
            return BirthdateResult(valid = false, error = "Formato incompleto")
        }

        return try {
            val date = LocalDate.parse(input, formatter)
            val today = LocalDate.now()

            if (date.isAfter(today)) {
                return BirthdateResult(
                    valid = false,
                    error = "La fecha no puede ser futura"
                )
            }

            val age = Period.between(date, today).years

            if (age < 0) {
                return BirthdateResult(
                    valid = false,
                    error = "Fecha inválida"
                )
            }

            BirthdateResult(valid = true, age = age)

        } catch (e: DateTimeParseException) {
            BirthdateResult(
                valid = false,
                error = "Fecha inválida (revísala)"
            )
        }
    }
}
