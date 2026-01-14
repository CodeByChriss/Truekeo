package com.chaima.truekeo.utils

import java.time.Duration
import java.time.Instant
import kotlin.math.round

private fun roundToNearest(value: Double, step: Int = 1): Int {
    return (round(value / step) * step).toInt()
}

private fun pluralize(value: Int, singular: String, plural: String): String {
    return if (value == 1) singular else plural
}

fun timeAgo(from: Instant, to: Instant = Instant.now()): String {
    val minutes = Duration.between(from, to).toMinutes()

    return when {
        minutes < 1 ->
            "Publicado ahora"

        minutes < 60 -> {
            val roundedMinutes = roundToNearest(minutes.toDouble(), 5)
            val label = pluralize(roundedMinutes, "minuto", "minutos")
            "Publicado hace $roundedMinutes $label"
        }

        minutes < 60 * 24 -> {
            val hours = round(minutes / 60.0).toInt()
            val label = pluralize(hours, "hora", "horas")
            "Publicado hace $hours $label"
        }

        minutes < 60 * 24 * 30 -> {
            val days = round(minutes / (60.0 * 24)).toInt()
            val label = pluralize(days, "día", "días")
            "Publicado hace $days $label"
        }

        else -> {
            val months = round(minutes / (60.0 * 24 * 30)).toInt()
            val label = pluralize(months, "mes", "meses")
            "Publicado hace $months $label"
        }
    }
}