package com.wenhui.circlepagindicator

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View


/**
 * A horizontal pager indicators can be drew on top of RecyclerView.
 */
class CirclePageIndicator @JvmOverloads constructor(
  private val parentView: View,
  private val configs: CirclePageIndicatorConfigs = CirclePageIndicatorConfigs(parentView.context)
) {

  private val indicatorHandler = CirclePageIndicatorHandler(parentView, configs)
  private val indicatorRenderer = CirclePagerIndicatorRenderer(configs.activeColor, configs.inactiveColor)

  /**
   * Call when adapter data updated
   */
  fun onDataUpdated(totalItemCount: Int, selectedPosition: Int) {
    indicatorHandler.onItemsUpdated(totalItemCount)
    onPageSelected(selectedPosition)
  }

  /**
   * Call when page is selected, or scrolling, so the new active position will be highlighted appropriately
   */
  fun onPageSelected(selectedPosition: Int) {
    if (indicatorHandler.shouldShowIndicators.not()) return
    updateBounds()

    val position = Math.max(selectedPosition, 0)
    indicatorHandler.showActivePosition(position)
  }

  /**
   * Update indicator background and drawing bounds
   */
  private fun updateBounds() {
    if (indicatorHandler.isBoundsSet) return

    if (parentView.width > 0 && parentView.height > 0) {
      val left = 0
      val top = parentView.height - configs.indicatorBackgroundHeight
      val right = parentView.width
      val bottom = parentView.height
      configs.indicatorBackground?.setBounds(left, top, right, bottom)

      val indicatorBottom = parentView.height - configs.indicatorBottomMargin
      val indicatorDrawingBoundHeight = (configs.largeIndicatorRadius + 4f).toInt()
      val indicatorTop = indicatorBottom - indicatorDrawingBoundHeight
      indicatorHandler.bounds = Rect(left, indicatorTop, right, indicatorBottom)
    }
  }

  fun draw(canvas: Canvas) {
    if (indicatorHandler.shouldShowIndicators.not()) return

    val indicators = indicatorHandler.indicators
    if (indicators.isEmpty()) return
    configs.indicatorBackground?.draw(canvas)
    indicatorRenderer.renderPagerIndicator(canvas, indicators)
  }
}
