package com.coquankedian.bigtime.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import com.coquankedian.bigtime.data.model.Event
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageGenerator(private val context: Context) {

    companion object {
        private const val IMAGE_WIDTH = 1080
        private const val IMAGE_HEIGHT = 1920
        private const val CARD_WIDTH = 900
        private const val CARD_HEIGHT = 600
        private const val CARD_RADIUS = 32f
        private const val PADDING = 90
    }

    fun generateEventImage(event: Event): File? {
        return try {
            val bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            // Draw background
            drawBackground(canvas)
            
            // Draw event card
            drawEventCard(canvas, event)
            
            // Draw branding
            drawBranding(canvas)
            
            // Save to file
            saveBitmapToFile(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawBackground(canvas: Canvas) {
        // Create gradient background
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#667eea"),
                Color.parseColor("#764ba2")
            )
        )
        gradient.setBounds(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
        gradient.draw(canvas)
    }

    private fun drawEventCard(canvas: Canvas, event: Event) {
        val cardLeft = (IMAGE_WIDTH - CARD_WIDTH) / 2
        val cardTop = (IMAGE_HEIGHT - CARD_HEIGHT) / 2 - 100
        val cardRight = cardLeft + CARD_WIDTH
        val cardBottom = cardTop + CARD_HEIGHT

        // Draw card shadow
        val shadowPaint = Paint().apply {
            color = Color.parseColor("#40000000")
            maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
        }
        
        val shadowRect = RectF(
            cardLeft + 10f, cardTop + 10f,
            cardRight + 10f, cardBottom + 10f
        )
        canvas.drawRoundRect(shadowRect, CARD_RADIUS, CARD_RADIUS, shadowPaint)

        // Draw card background
        val cardPaint = Paint().apply {
            color = event.cardBackgroundColor
            isAntiAlias = true
        }
        
        val cardRect = RectF(cardLeft.toFloat(), cardTop.toFloat(), cardRight.toFloat(), cardBottom.toFloat())
        canvas.drawRoundRect(cardRect, CARD_RADIUS, CARD_RADIUS, cardPaint)

        // Draw card content
        drawCardContent(canvas, event, cardLeft, cardTop, CARD_WIDTH, CARD_HEIGHT)
    }

    private fun drawCardContent(canvas: Canvas, event: Event, left: Int, top: Int, width: Int, height: Int) {
        val contentPadding = 60
        val contentLeft = left + contentPadding
        val contentTop = top + contentPadding
        val contentWidth = width - (contentPadding * 2)

        // Title
        val titlePaint = Paint().apply {
            color = event.textColor
            textSize = 72f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        
        drawTextMultiline(
            canvas, event.title, titlePaint,
            contentLeft.toFloat(), (contentTop + 80).toFloat(),
            contentWidth.toFloat()
        )

        // Date
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        val datePaint = Paint().apply {
            color = adjustColorAlpha(event.textColor, 0.8f)
            textSize = 48f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        
        canvas.drawText(
            dateFormat.format(event.date),
            contentLeft.toFloat(),
            (contentTop + 200).toFloat(),
            datePaint
        )

        // Days count - large and prominent
        val daysCountPaint = Paint().apply {
            color = event.textColor
            textSize = 120f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        
        val daysText = "${Math.abs(event.daysUntil)}"
        val daysTextWidth = daysCountPaint.measureText(daysText)
        canvas.drawText(
            daysText,
            contentLeft.toFloat() + (contentWidth - daysTextWidth) / 2,
            (contentTop + 360).toFloat(),
            daysCountPaint
        )

        // Days label
        val daysLabel = when {
            event.daysUntil > 0 -> "天后"
            event.daysUntil < 0 -> "天前"
            else -> "今天"
        }
        
        val daysLabelPaint = Paint().apply {
            color = adjustColorAlpha(event.textColor, 0.9f)
            textSize = 56f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        
        val daysLabelWidth = daysLabelPaint.measureText(daysLabel)
        canvas.drawText(
            daysLabel,
            contentLeft.toFloat() + (contentWidth - daysLabelWidth) / 2,
            (contentTop + 440).toFloat(),
            daysLabelPaint
        )

        // Description (if available)
        if (event.description.isNotEmpty()) {
            val descriptionPaint = Paint().apply {
                color = adjustColorAlpha(event.textColor, 0.7f)
                textSize = 36f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }
            
            drawTextMultiline(
                canvas, event.description, descriptionPaint,
                contentLeft.toFloat(), (contentTop + 500).toFloat(),
                contentWidth.toFloat(), maxLines = 2
            )
        }
    }

    private fun drawBranding(canvas: Canvas) {
        val brandingPaint = Paint().apply {
            color = Color.WHITE
            textSize = 36f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
            alpha = 200
        }
        
        val brandingText = "Days Matter - 重要日子"
        val textWidth = brandingPaint.measureText(brandingText)
        
        canvas.drawText(
            brandingText,
            (IMAGE_WIDTH - textWidth) / 2,
            IMAGE_HEIGHT - 120f,
            brandingPaint
        )
    }

    private fun drawTextMultiline(
        canvas: Canvas, 
        text: String, 
        paint: Paint, 
        x: Float, 
        y: Float, 
        maxWidth: Float,
        maxLines: Int = Int.MAX_VALUE
    ) {
        val words = text.split(" ")
        var currentLine = StringBuilder()
        var currentY = y
        var lineCount = 0

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)

            if (testWidth > maxWidth && currentLine.isNotEmpty()) {
                // Draw current line and start new line
                canvas.drawText(currentLine.toString(), x, currentY, paint)
                currentLine = StringBuilder(word)
                currentY += paint.textSize + 10
                lineCount++
                
                if (lineCount >= maxLines) break
            } else {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            }
        }

        // Draw last line
        if (currentLine.isNotEmpty() && lineCount < maxLines) {
            canvas.drawText(currentLine.toString(), x, currentY, paint)
        }
    }

    private fun adjustColorAlpha(color: Int, alpha: Float): Int {
        val adjustedAlpha = (255 * alpha).toInt()
        return Color.argb(
            adjustedAlpha,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File? {
        return try {
            val fileName = "event_share_${System.currentTimeMillis()}.png"
            val file = File(context.cacheDir, fileName)
            
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}