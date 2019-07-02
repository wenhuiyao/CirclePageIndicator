package com.wenhui.circlepagindicator

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt

internal class CirclePagerIndicatorRenderer(
    @ColorInt private val activeColor: Int,
    @ColorInt private val inactiveColor: Int
) {

    private val inactivePaint = Paint()
    private val activePaint = Paint()

    init {
        inactivePaint.isAntiAlias = true
        inactivePaint.color = inactiveColor

        activePaint.isAntiAlias = true
        activePaint.color = activeColor
    }

    fun renderPagerIndicator(canvas: Canvas, specs: List<PageIndicatorSpecs>) {
        specs.forEach {
            if (it.radius > 0.5f) { // It doesn't make sense to draw anything smaller than a pixel
                // render indicator
                val paint = if (it.active) this.activePaint else this.inactivePaint
                canvas.drawCircle(it.x + it.translateX, it.y, it.radius, paint)
            }
        }
    }

}