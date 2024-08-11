package extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.EmptyCoroutineContext

class Timer {
    private var timer: Job? = null
    var updateInterface: ((String) -> Unit)? = null
    var startTime: Instant? = null

    fun start(c: (String) -> Unit) {
        start(Clock.System.now(), c)
    }

    fun start(instant: Instant, c: (String) -> Unit) {
        stop()
        startTime = instant
        updateInterface = c
        val callback = updateInterface
        timer = CoroutineScope(EmptyCoroutineContext).launch {
            calculateTime()?.let { callback?.invoke(secondToTimeFormat(it)) }
            while (true) {
                calculateTime()?.let { callback?.invoke(secondToTimeFormat(it)) }
                delay(1000)
            }
        }
    }

    fun stop() {
        startTime = null
        timer?.cancel()
    }

    private fun calculateTime(): Int? {
        return startTime?.let {
            Clock.System.now().minus(it).inWholeSeconds.toInt()
        }
    }

    fun calculateTime(timeAsString: String): String {
        val second = Clock.System.now().minus(timeAsString.toInstant()).inWholeSeconds.toInt()
        return secondToTimeFormat(second)
    }

    fun calculateDeltaTime(time1: String, time2: String): String {
        val second = calculateDeltaTimeWithSeconds(time1, time2)
        return secondToTimeFormat(second)
    }

    private fun calculateDeltaTimeWithSeconds(time1: String, time2: String): Int {
        return time2.toInstant().minus(time1.toInstant()).inWholeSeconds.toInt()
    }

    fun calculateDeltaTimeWithMinutes(time1: String, time2: String): Int {
        return calculateDeltaTimeWithSeconds(time2, time1) / 60
    }

    private fun secondToTimeFormat(seconds: Int): String {
        val minutes = (seconds.toFloat() / 60f).toInt()
        val second = seconds - minutes * 60

        return "$minutes:$second"
    }

    companion object {
        val Now: Instant
            get() {
                return Clock.System.now()
            }
    }
}

fun Instant.toYearAndMonth(): String {
    val localDateTime = iso8601TimestampToLocalDateTime(this.toString())
    val date = localDateTime.date
    val month = date.monthNumber
    val year = date.year
    return "${month.zeroPrefixed(2)}-${year}"
}

class Month(
    val month: String,
    var errorMessage: String = ""
)

fun generateMonthList(startDate: String): List<Month> {
    val startParts = startDate.split("-")
    val startMonth = startParts[0].toInt()
    val startYear = startParts[1].toInt()

    val currentLocalDate = Timer.Now.toLocalDateTime(TimeZone.currentSystemDefault())
    val currentMonth = currentLocalDate.monthNumber
    val currentYear = currentLocalDate.year

    val monthList = mutableListOf<String>()
    var year = startYear
    var month = startMonth

    while (year < currentYear || (year == currentYear && month <= currentMonth)) {
        monthList.add("${month.zeroPrefixed(2)}-$year")
        month++
        if (month > 12) {
            month = 1
            year++
        }
    }

    return monthList.map { Month(it) }
}


fun String.dateWithWord(): String {
    val months = mapOf(
        "01" to "January",
        "02" to "February",
        "03" to "March",
        "04" to "April",
        "05" to "May",
        "06" to "June",
        "07" to "July",
        "08" to "August",
        "09" to "September",
        "10" to "October",
        "11" to "November",
        "12" to "December"
    )
    return try {
        val (month, year) = this.split("-")
        return months[month] + " " + year
    } catch (e: Exception) {
        "-- ----"
    }
}

private fun iso8601TimestampToLocalDateTime(timestamp: String): LocalDateTime {
    return Instant.parse(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
}