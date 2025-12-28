package teksturepako.onscaleof

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.Image

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}

// TODO: make suspend
actual fun ImageBitmap.toByteArray(): ByteArray {
    return runBlocking {
        this@toByteArray.encodeToByteArray(ImageFormat.PNG)
    }
}