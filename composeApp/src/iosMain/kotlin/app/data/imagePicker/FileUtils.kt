package app.data.imagePicker

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import dev.gitlive.firebase.storage.File
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIImage

actual fun toFile(image: ByteArray): File? {
    val data = image.toImageBytes()
    if (data != null) {
        return UIImage(data = data).CIImage?.url?.let {
            File(url = it)
        }
    } else {
        return null
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toImageBytes(): NSData? = memScoped {
    val string = NSString.create(string = this@toImageBytes.decodeToString())
    return string.dataUsingEncoding(NSUTF8StringEncoding)
}
