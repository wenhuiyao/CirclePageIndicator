package com.wenhui.circlepagindicator

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View


/**
 * A horizontal pager indicators can be drew on top of RecyclerView.
 */
class CirclePageIndicator<V : View> @JvmOverloads constructor(
    private val parentView: V,
    private val pager: Pager<V>,
    private val configs: CirclePageIndicatorConfigs = CirclePageIndicatorConfigs(parentView.context)
) {

    interface Pager<V : View> {
        /**
         * @return The position of the selected page, otherwise return negative number if no page found
         */
        fun findSelectedPagePosition(parentView: V): Int
    }

    private val indicatorHandler = CirclePageIndicatorHandler(parentView, configs)
    private val indicatorRenderer = CirclePagerIndicatorRenderer(configs.activeColor, configs.inactiveColor)

    /**
     * Call when adapter data updated
     */
    fun onDataUpdated(totalItemCount: Int) {
        indicatorHandler.onItemsUpdated(totalItemCount)
        onPageSelected()
    }

    /**
     * Call when page is selected, or scrolling, so the new active position will be highlighted appropriately
     */
    fun onPageSelected() {
        if (indicatorHandler.shouldShowIndicators.not()) return
        updateBounds()

        val newPosition = pager.findSelectedPagePosition(parentView)
        if (newPosition >= 0) {
            indicatorHandler.showActivePosition(newPosition)
        }
    }

    /**
     * Update indicator background and drawing bounds
     *
     * @return True if bounds updated, false if not
     */
    private fun updateBounds(): Boolean {
        if (indicatorHandler.bounds !== ZERO_BOUNDS_RECT) return false

        if (parentView.width > 0 && parentView.height > 0) {
            val left = 0
            val top = parentView.height - configs.indicatorBackgroundHeight
            val right = parentView.width
            val bottom = parentView.height
            configs.indicatorBackground?.setBounds(left, top, right, bottom)

            val indicatorBottom = parentView.height - configs.indicatorBottomMargin
            val indicatorDrawingBoundHeight = (configs.largeIndicatorRadius + 4f).toInt()
            val indicatorTop = indicatorBottom - indicatorDrawingBoundHeight
            indicatorHandler.bounds = Rect().also {
                it.left = left
                it.top = indicatorTop
                it.right = right
                it.bottom = indicatorBottom
            }

            return true
        }

        return false
    }

    fun draw(canvas: Canvas) {
        if (indicatorHandler.shouldShowIndicators.not()) return

        val indicators = indicatorHandler.indicators
        if (indicators.isEmpty()) return
        configs.indicatorBackground?.draw(canvas)
        indicatorRenderer.renderPagerIndicator(canvas, indicators)
    }
}
