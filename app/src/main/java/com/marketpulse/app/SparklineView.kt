package com.marketpulse.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class SparklineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var points: List<Float> = emptyList()
    private var isGain: Boolean = true
    private var showFill: Boolean = true

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val path = Path()
    private val fillPath = Path()

    fun setData(newPoints: List<Float>, positive: Boolean, enableFill: Boolean = true) {
        this.points = newPoints
        this.isGain = positive
        this.showFill = enableFill
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (points.size < 2 || width <= 0 || height <= 0) return

        val strokeColor = if (isGain) Color.parseColor("#10B981") else Color.parseColor("#EF4444")
        linePaint.color = strokeColor

        val paddingH = 6f
        val paddingV = 8f
        val availableW = width - (paddingH * 2)
        val availableH = height - (paddingV * 2)

        var minVal = points.minOrNull() ?: 0f
        var maxVal = points.maxOrNull() ?: 1f
        if (minVal == maxVal) {
            maxVal += 1f
            minVal -= 1f
        }
        val range = maxVal - minVal

        path.reset()
        fillPath.reset()

        val stepX = availableW / (points.size - 1)

        for (i in points.indices) {
            val x = paddingH + (i * stepX)
            val normalizedY = (points[i] - minVal) / range
            val y = paddingV + availableH - (normalizedY * availableH)

            if (i == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, y)
            } else {
                val prevX = paddingH + ((i - 1) * stepX)
                val prevNormalizedY = (points[i - 1] - minVal) / range
                val prevY = paddingV + availableH - (prevNormalizedY * availableH)
                val midX = (prevX + x) / 2
                path.cubicTo(midX, prevY, midX, y, x, y)
                fillPath.cubicTo(midX, prevY, midX, y, x, y)
            }
        }

        // Draw gradient fill if enabled
        if (showFill) {
            val lastX = paddingH + ((points.size - 1) * stepX)
            fillPath.lineTo(lastX, height.toFloat())
            fillPath.lineTo(paddingH, height.toFloat())
            fillPath.close()

            val startAlphaColor = if (isGain) Color.parseColor("#4410B981") else Color.parseColor("#44EF4444")
            val endAlphaColor = Color.TRANSPARENT
            fillPaint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                startAlphaColor, endAlphaColor,
                Shader.TileMode.CLAMP
            )
            canvas.drawPath(fillPath, fillPaint)
        }

        // Draw line curve
        canvas.drawPath(path, linePaint)
    }
}
