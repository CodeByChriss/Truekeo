package com.chaima.truekeo.utils

import android.content.Context
import com.chaima.truekeo.R
import java.time.Duration
import java.time.Instant
import kotlin.math.round

private fun roundToNearest(value: Double, step: Int = 1): Int {
    return (round(value / step) * step).toInt()
}

fun timeAgo(
    context: Context,
    from: Instant,
    to: Instant = Instant.now()
): String {

    val minutes = Duration.between(from, to).toMinutes()

    return when {
        minutes < 1 -> {
            context.getString(R.string.time_now)
        }

        minutes < 60 -> {
            val value = roundToNearest(minutes.toDouble(), 5).coerceAtLeast(1)
            context.resources.getQuantityString(
                R.plurals.time_minutes_ago,
                value,
                value
            )
        }

        minutes < 60 * 24 -> {
            val value = round(minutes / 60.0).toInt().coerceAtLeast(1)
            context.resources.getQuantityString(
                R.plurals.time_hours_ago,
                value,
                value
            )
        }

        minutes < 60 * 24 * 30 -> {
            val value = round(minutes / (60.0 * 24)).toInt().coerceAtLeast(1)
            context.resources.getQuantityString(
                R.plurals.time_days_ago,
                value,
                value
            )
        }

        else -> {
            val value = round(minutes / (60.0 * 24 * 30)).toInt().coerceAtLeast(1)
            context.resources.getQuantityString(
                R.plurals.time_months_ago,
                value,
                value
            )
        }
    }
}

enum class TimePrefix {
    PUBLISHED,
    UPDATED
}

fun prefixedTimeAgo(
    context: Context,
    from: Instant,
    prefix: TimePrefix
): String {
    val prefixRes = when (prefix) {
        TimePrefix.PUBLISHED -> R.string.time_prefix_published
        TimePrefix.UPDATED -> R.string.time_prefix_updated
    }

    val prefixText = context.getString(prefixRes)
    val timeText = timeAgo(context, from)

    return "$prefixText $timeText"
}