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

    // Create BufferedImage
    val bufferedImage = BufferedImage(gridSize, totalHeight, BufferedImage.TYPE_INT_RGB)
    val g2d = bufferedImage.createGraphics()

    // Enable anti-aliasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    // Draw white background
    g2d.color = Color.WHITE
    g2d.fillRect(0, 0, gridSize, totalHeight)

    // Draw text header
    g2d.color = Color.BLACK
    g2d.font = Font("Arial", Font.PLAIN, 48)
    val fm = g2d.fontMetrics

    val line1 = "On a scale of random"
    val line2 = text
    val line3 = "how are you feeling today?"

    g2d.drawString(line1, (gridSize - fm.stringWidth(line1)) / 2, 60)
    g2d.drawString(line2, (gridSize - fm.stringWidth(line2)) / 2, 120)
    g2d.drawString(line3, (gridSize - fm.stringWidth(line3)) / 2, 180)

    // Draw grid
    for (row in 0..2) {
        for (col in 0..2) {
            val index = row * 3 + col
            val x = col * cellSize
            val y = headerHeight + row * cellSize

            val image = images.getOrNull(index)

            if (image != null) {
                // Draw the image
                val awtImage = image.toAwtImage()
                g2d.drawImage(
                    awtImage,
                    x, y,                           // dest x, y
                    x + cellSize, y + cellSize,     // dest x2, y2
                    0, 0,                            // src x, y
                    awtImage.width, awtImage.height, // src x2, y2
                    null
                )
            } else {
                // Draw placeholder
                g2d.color = Color.LIGHT_GRAY
                g2d.fillRect(x, y, cellSize, cellSize)
            }

            // Draw grid lines
            g2d.color = Color.GRAY
            g2d.stroke = BasicStroke(4f)
            g2d.drawLine(x, y, x + cellSize, y)  // top
            g2d.drawLine(x, y, x, y + cellSize)  // left
        }
    }

    // Draw outer border
    g2d.color = Color.GRAY
    g2d.stroke = BasicStroke(4f)
    g2d.drawRect(0, headerHeight, gridSize, gridSize)

    g2d.dispose()

    return bufferedImage.toComposeImageBitmap()
}