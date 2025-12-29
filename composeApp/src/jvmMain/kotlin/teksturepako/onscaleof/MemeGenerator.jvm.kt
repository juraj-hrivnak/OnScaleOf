package teksturepako.onscaleof

import java.awt.*
import java.awt.image.BufferedImage
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap

actual fun createMemeImage(
    images: List<ImageBitmap?>,
    text: String,
    cellSize: Int
): ImageBitmap {
    val gridSize = cellSize * 3
    val headerHeight = 200
    val totalHeight = headerHeight + gridSize

    val bufferedImage = BufferedImage(gridSize, totalHeight, BufferedImage.TYPE_INT_RGB)
    val g2d = bufferedImage.createGraphics()
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    g2d.color = Color.WHITE
    g2d.fillRect(0, 0, gridSize, totalHeight)

    g2d.color = Color.BLACK
    g2d.font = Font("Arial", Font.PLAIN, 48)
    val fm = g2d.fontMetrics

    val line1 = "On a scale of random"
    val line2 = text
    val line3 = "how are you feeling today?"

    g2d.drawString(line1, (gridSize - fm.stringWidth(line1)) / 2, 60)
    g2d.drawString(line2, (gridSize - fm.stringWidth(line2)) / 2, 120)
    g2d.drawString(line3, (gridSize - fm.stringWidth(line3)) / 2, 180)

    for (row in 0..2) {
        for (col in 0..2) {
            val index = row * 3 + col
            val x = col * cellSize
            val y = headerHeight + row * cellSize
            val image = images.getOrNull(index)

            if (image != null) {
                val awtImage = image.toAwtImage()
                // Crop to largest centered square
                val srcW = awtImage.width
                val srcH = awtImage.height
                val side = minOf(srcW, srcH)
                val sx0 = (srcW - side) / 2
                val sy0 = (srcH - side) / 2
                val sx1 = sx0 + side
                val sy1 = sy0 + side
                // Draw the cropped square scaled to cell
                g2d.drawImage(
                    awtImage,
                    x, y, x + cellSize, y + cellSize, // dest
                    sx0, sy0, sx1, sy1,               // src
                    null
                )
            } else {
                g2d.color = Color.LIGHT_GRAY
                g2d.fillRect(x, y, cellSize, cellSize)
            }

            // Draw grid lines
            g2d.color = Color.GRAY
            g2d.stroke = BasicStroke(4f)
            g2d.drawLine(x, y, x + cellSize, y)    // top
            g2d.drawLine(x, y, x, y + cellSize)    // left
        }
    }

    g2d.color = Color.GRAY
    g2d.stroke = BasicStroke(4f)
    g2d.drawRect(0, headerHeight, gridSize, gridSize)

    g2d.dispose()
    return bufferedImage.toComposeImageBitmap()
}
