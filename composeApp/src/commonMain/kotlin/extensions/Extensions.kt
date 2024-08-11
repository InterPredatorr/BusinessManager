package extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant

fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    block: (T1, T2, T3, T4) -> R?
): R? {
    return if (p1!=null && p2!=null && p3!=null && p4!=null) block(p1, p2, p3, p4) else null
}

fun Instant.toDate(): String {
    val day = toString().substringBefore('T')
    val hour = toString().substringAfter('T').substringBefore('.')

    return "$day $hour"
}

fun Modifier.ifTrue(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
fun Float.scaledSp(): TextUnit {
    val value: Float = this
    return with(LocalDensity.current) {
        val fontScale = this.fontScale
        val textSize = value / fontScale
        textSize.sp
    }
}

fun Int.zeroPrefixed(
    maxLength: Int,
): String {
    if (this < 0 || maxLength < 1) return ""

    val string = this.toString()
    val currentStringLength = string.length
    return if (maxLength <= currentStringLength) {
        string
    } else {
        val diff = maxLength - currentStringLength
        var prefixedZeros = ""
        repeat(diff) {
            prefixedZeros += "0"
        }
        "$prefixedZeros$string"
    }
}