package teksturepako.onscaleof

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}

actual fun ImageBitmap.toByteArray(): ByteArray {
    val skiaBitmap = Bitmap()
    skiaBitmap.allocPixels(ImageInfo.makeN32(this.width, this.height, ColorAlphaType.PREMUL))
    val skiaImage = Image.makeFromBitmap(skiaBitmap)
    return skiaImage.encodeToData(EncodedImageFormat.PNG)?.bytes ?: byteArrayOf()
}