@file:Suppress("ConvertTwoComparisonsToRangeCheck")

package com.wenhui.circlepagindicator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.View
import androidx.core.util.Pools
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import java.util.*

private val INDICATOR_POOL = Pools.SimplePool<PageIndicatorSpecs>(10)


/**
 * Calculating indicators position, and animate shift and scale if needed
 */
internal class CirclePageIndicatorHandler(
  private val parentView: View,
  private val configs: CirclePageIndicatorConfigs
) {

  private val itemSpace = configs.indicatorSpacing
  private val animationInterpolator = LinearOutSlowInInterpolator()
  private val largeIndicatorRadius = configs.largeIndicatorRadius
  private val mediumIndicatorRadius = configs.mediumIndicantRadius
  private val smallIndicatorRadius = configs.smallIndicatorRadius

  private val minNumOfIndicators = Math.min(configs.edgeVisibleIndicators, (configs.maxVisibleIndicators + 1) / 2)

  var bounds: Rect = ZERO_BOUNDS_RECT

  private var totalItemCount = 0
  private var runningAnimation: Animator? = null

  // At any given time, we will show at max visible indicators, but have 2 more offset invisible, so we can animate them
  val indicators = LinkedList<PageIndicatorSpecs>()

  val shouldShowIndicators: Boolean
    get() = totalItemCount >= configs.minVisibleIndicators

  fun onItemsUpdated(totalItemCount: Int) {
    clearIndicators()
    this.totalItemCount = totalItemCount
    bounds = ZERO_BOUNDS_RECT
  }

  private fun cancelAnimation() {
    runningAnimation?.cancel()
    runningAnimation = null
  }

  fun showActivePosition(position: Int) {
    if (shouldShowIndicators.not()) return

    var oldActiveIndicator: PageIndicatorSpecs? = null
    var newActiveIndicator: PageIndicatorSpecs? = null

    indicators.forEach {
      if (it.active) {
        oldActiveIndicator = it
      }

      if (it.position == position) {
        newActiveIndicator = it
      }
    }

    if (oldActiveIndicator != null && newActiveIndicator != null) {
      animateToPosition(oldActiveIndicator!!, newActiveIndicator!!)
    } else {
      // initial state, none of them are active
      showIndicatorsAtPosition(position)
    }
  }

  /**
   * Show indicator at position without animation
   */
  private fun showIndicatorsAtPosition(position: Int) {
    clearIndicators()

    val activeIndicator = createIndicator().also {
      it.active = true
      it.position = position
      it.x = centerX()
    }
    indicators.addFirst(activeIndicator)

    val numOfIndicatorsOnEachSide = numOfIndicatorsOnEachSideAtPosition(position)
    addNewIndicatorsToTheLeft(numOfIndicatorsOnEachSide)
    addNewIndicatorsToTheRight(numOfIndicatorsOnEachSide)

    // update indicator type and radius
    indicators.forEach {
      it.type = getIndicatorType(position, it.position)
      it.radius = getTargetRadius(it.type)
    }

    // This must be done after updating the indicator types
    val xShift = computeXShift()
    indicators.forEach {
      it.x += xShift
    }

    parentView.invalidate()
  }

  private fun centerY() = bounds.top + (bounds.bottom - bounds.top) / 2f
  private fun centerX() = bounds.left + (bounds.right - bounds.left) / 2f

  /**
   * The number of indicators on each side of active indicator
   */
  private fun numOfIndicatorsOnEachSideAtPosition(position: Int): Int {
    return if (position == 0 || position == totalItemCount - 1)
      minNumOfIndicators - 1 // show min visible indicator on both end
    else
      (configs.maxVisibleIndicators - 1) / 2
  }

  private fun getTargetRadius(type: PageIndicatorType): Float {
    return when (type) {
      PageIndicatorType.LARGE -> largeIndicatorRadius
      PageIndicatorType.MEDIUM -> mediumIndicatorRadius
      PageIndicatorType.SMALL -> smallIndicatorRadius
      PageIndicatorType.INVISIBLE -> 0f
    }
  }

  /**
   * Get the target corresponding indicator type base on the active position.
   * Return [PageIndicatorType.LARGE] if the position is active, return [PageIndicatorType.SMALL] if the position
   * is on the end of both side, return [PageIndicatorType.INVISIBLE] if the position is outside of number of display
   * indicator range, return [PageIndicatorType.MEDIUM] for the rest
   */
  private fun getIndicatorType(activePosition: Int, targetPosition: Int): PageIndicatorType {
    val numOfIndicatorsOnEachSide = numOfIndicatorsOnEachSideAtPosition(activePosition)
    if (targetPosition < activePosition - numOfIndicatorsOnEachSide
      || targetPosition > activePosition + numOfIndicatorsOnEachSide
    ) {
      // If the position is outside of range, always hide it
      return PageIndicatorType.INVISIBLE
    }

    // When there are less or equal to min visible total items, show medium do to the rest of the inactive item
    val showMediumAlways = totalItemCount <= minNumOfIndicators

    return if (targetPosition == activePosition) {
      PageIndicatorType.LARGE
    } else if (!showMediumAlways &&
      (targetPosition == activePosition - numOfIndicatorsOnEachSide || targetPosition == activePosition + numOfIndicatorsOnEachSide)
    ) {
      PageIndicatorType.SMALL
    } else {
      PageIndicatorType.MEDIUM
    }
  }

  private fun createIndicator(): PageIndicatorSpecs {
    val newSpecs = INDICATOR_POOL.acquire() ?: PageIndicatorSpecs()
    // Must reset the specs to avoid any side effect when using recycled item
    return newSpecs.also {
      it.active = false
      it.type = PageIndicatorType.INVISIBLE
      it.radius = 0f
      it.x = 0f
      it.y = centerY()
      it.translateX = 0f
    }
  }

  private fun clearIndicators() {
    cancelAnimation()
    indicators.forEach {
      releaseIndicatorToPool(it)
    }
    indicators.clear()
  }

  private fun releaseIndicatorToPool(indicator: PageIndicatorSpecs) {
    try {
      INDICATOR_POOL.release(indicator)
    } catch (ignore: IllegalStateException) {
      // If the indicator is already released, move on
    }
  }

  private fun addNewIndicatorsToTheLeft(count: Int) {
    val firstItem = indicators.first
    val position = firstItem.position
    var x = firstItem.x
    for (i in position - 1 downTo position - count) {
      if (i >= 0 && i < totalItemCount) {
        x -= itemSpace
        val spec = createIndicator().also {
          it.position = i
          it.x = x
        }
        indicators.addFirst(spec)
      } else {
        break
      }
    }
  }

  private fun addNewIndicatorsToTheRight(count: Int) {
    val lastItem = indicators.last
    val position = indicators.last.position
    var x = lastItem.x
    for (i in position + 1 until position + count + 1) {
      if (i >= 0 && i < totalItemCount) {
        x += itemSpace
        val spec = createIndicator().also {
          it.position = i
          it.x = x
        }
        indicators.addLast(spec)
      } else {
        break
      }
    }
  }

  /**
   * We always want to position the indicator on the center horizontal of the bounds, so calculate
   * the distance we need to shift to center them
   */
  private fun computeXShift(): Float {
    // first, compute current center

    // since we know the space between each item is the same, so we can simply calculate the average of all the visible x
    var currentTotalX = 0f
    var count = 0
    indicators.forEach {
      if (it.type != PageIndicatorType.INVISIBLE) {
        currentTotalX += it.x
        count++
      }
    }

    val boundCenterX = centerX()
    val currentCenterX = if (count > 0) currentTotalX / count else boundCenterX
    return boundCenterX - currentCenterX
  }

  private fun animateToPosition(oldActiveIndicator: PageIndicatorSpecs, newActiveIndicator: PageIndicatorSpecs) {
    val oldPosition = oldActiveIndicator.position
    val newPosition = newActiveIndicator.position
    if (oldPosition == newPosition) return

    // first, let's draw the new active indicator
    oldActiveIndicator.active = false
    newActiveIndicator.active = true
    parentView.invalidate()

    // Then animate the new active indicator to new position
    animateIndicatorsToNewPosition(oldPosition, newPosition)
  }

  private fun animateIndicatorsToNewPosition(oldPosition: Int, newPosition: Int) {
    cancelAnimation()

    val numOfIndicatorsOnEachSide = numOfIndicatorsOnEachSideAtPosition(newPosition)
    if (oldPosition > newPosition) {
      // page scroll to the left
      addNewIndicatorsToTheLeft(Math.abs(numOfIndicatorsOnEachSide))
    } else {
      // page scroll to the right
      addNewIndicatorsToTheRight(Math.abs(numOfIndicatorsOnEachSide))
    }

    val floatEvaluators = ArrayList<FloatEvaluator>(indicators.size + 1)
    indicators.forEach { spec ->
      val newType = getIndicatorType(newPosition, spec.position)
      if (spec.type != newType) {
        spec.type = newType
        floatEvaluators += FloatEvaluator(
          spec.radius,
          getTargetRadius(newType)
        ) { newRadius ->
          spec.radius = newRadius
        }
      }
    }

    // This must be called after updating indicator types
    val shiftDist: Float = computeXShift()
    val shouldAnimateShift = shiftDist != 0f
    if (shouldAnimateShift) {
      floatEvaluators += FloatEvaluator(0f, shiftDist) { shift ->
        indicators.forEach { spec ->
          spec.translateX = shift
        }
      }
    }

    if (floatEvaluators.isEmpty()) return

    runningAnimation = ValueAnimator.ofInt(0, 100).apply {

      addUpdateListener { anim ->
        floatEvaluators.forEach { evaluator ->
          evaluator.evaluate(anim.animatedFraction)
        }
        parentView.invalidate()
      }

      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
          indicators.removeAll {
            // Remove those invisible indicators, since they are no longer needed
            val toRemove = it.type == PageIndicatorType.INVISIBLE
            if (toRemove) {
              releaseIndicatorToPool(it)
            }
            return@removeAll toRemove
          }
          indicators.forEach {
            it.x += it.translateX
            it.translateX = 0f
          }
        }
      })

      interpolator = animationInterpolator
      startDelay =
        if (shouldAnimateShift) configs.animationStartDelayed else configs.animationStartDelayedWithoutShift
      duration = if (shouldAnimateShift) configs.animationDuration else configs.animationDurationWithoutShift
      start()
    }
  }

}

/**
 * @param onEval Callback after new fraction is evaluated, the parameter of the callback is the new value of fraction
 */
private class FloatEvaluator(
  private val startValue: Float,
  private val endValue: Float,
  private val onEval: (Float) -> Unit
) {

  fun evaluate(fraction: Float) {
    onEval(startValue + fraction * (endValue - startValue))
  }
}