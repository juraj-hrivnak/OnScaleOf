package teksturepako.onscaleof

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun createMemeImage(
    images: List<ImageBitmap?>,
    text: String,
    cellSize: Int
): ImageBitmap {
    val gridSize = cellSize * 3
    val headerHeight = 200
    val totalHeight = headerHeight + gridSize

    // Create bitmap for the meme
    val resultBitmap = Bitmap.createBitmap(gridSize, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(resultBitmap)

    // Draw white background
    val paint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawRect(0f, 0f, gridSize.toFloat(), totalHeight.toFloat(), paint)

    // Draw text header
    val textPaint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    canvas.drawText("On a scale of random", gridSize / 2f, 60f, textPaint)
    canvas.drawText(text, gridSize / 2f, 120f, textPaint)
    canvas.drawText("how do you feel today?", gridSize / 2f, 180f, textPaint)

    // Draw grid
    for (row in 0..2) {
        for (col in 0..2) {
            val index = row * 3 + col
            val image = images.getOrNull(index)

            val x = col * cellSize
            val y = headerHeight + row * cellSize

            if (image != null) {
                val srcRect = Rect(0, 0, image.width, image.height)
                val dstRect = Rect(x, y, x + cellSize, y + cellSize)
                canvas.drawBitmap(image.asAndroidBitmap(), srcRect, dstRect, null)
            } else {
                // Draw placeholder
                paint.color = android.graphics.Color.LTGRAY
                canvas.drawRect(
                    x.toFloat(),
                    y.toFloat(),
                    (x + cellSize).toFloat(),
                    (y + cellSize).toFloat(),
                    paint
                )
            }

            // Draw grid lines
            paint.color = android.graphics.Color.GRAY
            paint.strokeWidth = 4f
            canvas.drawLine(x.toFloat(), y.toFloat(), (x + cellSize).toFloat(), y.toFloat(), paint)
            canvas.drawLine(x.toFloat(), y.toFloat(), x.toFloat(), (y + cellSize).toFloat(), paint)
        }
    }

    return resultBitmap.asImageBitmap()
}