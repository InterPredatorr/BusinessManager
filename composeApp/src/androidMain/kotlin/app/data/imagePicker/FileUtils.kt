package app.data.imagePicker

import android.graphics.BitmapFactory
import android.net.Uri
import dev.gitlive.firebase.storage.File
import java.io.ByteArrayOutputStream


actual fun toFile(image: ByteArray): File? {
    val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        ?: return null

    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

    val uri: Uri? = BitmapUtils.getUriFromBitmap(bitmap)
    return uri?.let { File(it) }
}