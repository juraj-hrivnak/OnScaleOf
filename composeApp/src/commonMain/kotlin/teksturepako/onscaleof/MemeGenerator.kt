package teksturepako.onscaleof

import androidx.compose.ui.graphics.ImageBitmap

expect fun createMemeImage(
    images: List<ImageBitmap?>,
    text: String,
    cellSize: Int = 400
): ImageBitmap