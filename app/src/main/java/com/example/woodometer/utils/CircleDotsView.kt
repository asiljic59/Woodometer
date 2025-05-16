package com.example.woodometer.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.example.woodometer.R
import com.example.woodometer.model.Stablo
import com.google.android.material.card.MaterialCardView
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CircleDotsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    var dots: List<Stablo> = emptyList()
        set(value) {
            field = value
            post {
                addDotViews()
            }
        }

    private val paintCircle = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val paintCenterDot = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private fun addDotViews() {
        removeAllViews()

        val realRadiusMeters = 12.62f // Radius, not diameter
        val canvasRadius = min(width, height) / 2f - 30f
        val scale = canvasRadius / realRadiusMeters

        val cx = width / 2f
        val cy = height / 2f
        val colors = listOf(
            10 to R.color.stablo_buducnosti,
            21 to R.color.konkurent,
            30 to R.color.indiferentno,
            41 to R.color.loseg_kvaliteta,
            51 to R.color.zrelo_stablo
        )
        val density = context.resources.displayMetrics.density
        val maxPrecnik = 100.0f

        for (stablo in dots) {
            // For landscape mode where "top" is actually East (90°)
            // We need to subtract 90° to compensate for landscape rotation
            val adjustedAzimuth = (stablo.azimut - 90) % 360
            val angleRad = Math.toRadians(adjustedAzimuth.toDouble())
            val r = stablo.razdaljina * scale

            // Calculate position with adjusted azimuth
            val x = cx + (r * cos(angleRad)).toFloat()
            val y = cy + (r * sin(angleRad)).toFloat()

            val dotView = LayoutInflater.from(context).inflate(R.layout.circle_item, this, false)
            val card = dotView.findViewById<MaterialCardView>(R.id.treeMaterialCardView)
            val text = dotView.findViewById<TextView>(R.id.stabloRbrTextView)
            text.text = stablo.rbr.toString()
            text.textSize = 14f
            val colorResId = colors.find { stablo.probDoznaka == it.first }?.second ?: R.color.white
            val color = context.getColor(colorResId)
            card.background.setTint(color)
            val baseSizeDp = 20f
            val maxSizeDp = 60f

            val minPrecnik = 10f

            val normalized = ((stablo.precnik - minPrecnik) / (maxPrecnik - minPrecnik)).coerceIn(0f, 1f)
            val scaledDp = baseSizeDp + normalized * (maxSizeDp - baseSizeDp)
            val sizePx = (scaledDp * density).toInt()

            val lp = LayoutParams(sizePx, sizePx)
            dotView.layoutParams = lp

            dotView.x = x - sizePx / 2f
            dotView.y = y - sizePx / 2f

            dotView.setOnClickListener {
                Toast.makeText(context, "Clicked ${stablo.rbr}", Toast.LENGTH_SHORT).show()
            }

            addView(dotView)
        }
        invalidate()
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 55f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val canvasRadius = min(width, height) / 2f - 30f

        // Draw outer circle
        canvas.drawCircle(cx, cy, canvasRadius, paintCircle)

        // Draw center dot
        canvas.drawCircle(cx, cy, 8f, paintCenterDot)

        // Draw compass directions adjusted for landscape mode
        // In landscape, left is North (0°), top is East (90°), right is South (180°), bottom is West (270°)
        canvas.drawText("N", cx, cy - canvasRadius + 40f, textPaint)
        canvas.drawText("E", cx + canvasRadius - 40f, cy, textPaint)
        canvas.drawText("S", cx, cy + canvasRadius - 40f, textPaint)
        canvas.drawText("W", cx - canvasRadius + 40f, cy, textPaint)
    }
}