@file:JvmName("CirclePageIndicatorUtil")
package com.wenhui.circlepagindicator

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat

private const val ACTIVE_COLOR = Color.WHITE
private const val INACTIVE_COLOR = 0xBFE8E9EA.toInt() //silver with 75% transparency

private const val INDICATOR_LARGE_RADIUS_IN_DP = 3.6f
private const val INDICATOR_MEDIUM_RADIUS_IN_DP = 2.8f
private const val INDICATOR_SMALL_RADIUS_IN_DP = 1.6f

private const val INDICATOR_BACKGROUND_HEIGHT_IN_DP = 24

/**
 * The height in dp of indicator drawing bounds, add 2dp for buffering
 */
private const val INDICATOR_BOTTOM_MARGIN_IN_DP = 8

private const val MAX_VISIBLE_INDICATORS = 5
private const val MIN_VISIBLE_INDICATORS = 2

/**
 * The number of indicators to show when active position is 0 or last
 */
private const val EDGE_VISIBLE_INDICATORS = (MAX_VISIBLE_INDICATORS + 1) / 2

/**
 * This is the space between indicators
 */
private const val INDICATOR_SPACE_IN_DP = 10

/**
 * The amount of time delayed before start the shifting and scaling animation on indicator.
 */
private const val ANIMATION_DELAYED_WITH_SHIFT = 150L

/**
 * The amount of time delayed before start scale animation.
 *
 * NOTE: there is no shift animation, so we want to shorten the time the scale animation start
 */
private const val ANIMATION_DELAYED_WITHOUT_SHIFT = 32L

/**
 * Animation duration to shift indicators to center of the bound and scale each indicator to new radius
 */
private const val ANIMATION_DURATION_WITH_SHIFT = 300L

/**
 * Animation duration to scale each indicator to the new radius
 */
private const val ANIMATION_DURATION_WITHOUT_SHIFT = 100L


@Suppress("FunctionName")
@JvmName("getConfigs")
fun CirclePageIndicatorConfigs(context: Context): CirclePageIndicatorConfigs {
    return CirclePageIndicatorConfigs.Builder(context).build()
}

class CirclePageIndicatorConfigs(
    @ColorInt val activeColor: Int,
    @ColorInt val inactiveColor: Int,
    val largeIndicatorRadius: Float,
    val mediumIndicantRadius: Float,
    val smallIndicatorRadius: Float,
    @Px val indicatorBackgroundHeight: Int,
    @Px val indicatorBottomMargin: Int,
    val indicatorBackground: Drawable?,
    val maxVisibleIndicators: Int,
    val minVisibleIndicators: Int,
    val edgeVisibleIndicators: Int,
    @Px val indicatorSpacing: Int,
    val animationStartDelayed: Long,
    val animationDuration: Long,
    val animationStartDelayedWithoutShift: Long,
    val animationDurationWithoutShift: Long
) {

    class Builder(private val context: Context) {

        @ColorInt var activeColor: Int = ACTIVE_COLOR
        @ColorInt var inactiveColor: Int = INACTIVE_COLOR

        var largeIndicatorRadius: Float = INDICATOR_LARGE_RADIUS_IN_DP.toDp(context)
        var mediumIndicantRadius: Float = INDICATOR_MEDIUM_RADIUS_IN_DP.toDp(context)
        var smallIndicatorRadius: Float = INDICATOR_SMALL_RADIUS_IN_DP.toDp(context)

        @Px var indicatorBackgroundHeight: Int = INDICATOR_BACKGROUND_HEIGHT_IN_DP.toDp(context)
        @Px var indicatorBottomMargin: Int = INDICATOR_BOTTOM_MARGIN_IN_DP.toDp(context)
        var indicatorBackground: Drawable? = ContextCompat.getDrawable(context, R.drawable.circle_page_indicator_background)

        var maxVisibleIndicators: Int = MAX_VISIBLE_INDICATORS
        var minVisibleIndicators: Int = MIN_VISIBLE_INDICATORS
        var edgeVisibleIndicators: Int = EDGE_VISIBLE_INDICATORS

        @Px var indicatorSpacing: Int = INDICATOR_SPACE_IN_DP.toDp(context)

        var animationStartDelayed: Long = ANIMATION_DELAYED_WITH_SHIFT
        var animationDuration: Long = ANIMATION_DURATION_WITH_SHIFT
        var animationStartDelayedWithoutShift: Long = ANIMATION_DELAYED_WITHOUT_SHIFT
        var animationDurationWithoutShift: Long = ANIMATION_DURATION_WITHOUT_SHIFT

        fun activeColor(@ColorInt activeColor: Int) = apply {
            this.activeColor = activeColor
        }

        fun activeColorRes(@ColorRes activeColorRes: Int) = apply {
            this.activeColor = ContextCompat.getColor(context, activeColorRes)
        }

        fun inactiveColor(@ColorInt inactiveColor: Int) = apply {
            this.inactiveColor = inactiveColor
        }

        fun inactiveColorRes(@ColorRes inactiveColorRes: Int) = apply {
            this.inactiveColor = ContextCompat.getColor(context, inactiveColorRes)
        }

        fun largeIndicatorRadius(radius: Float) = apply {
            this.largeIndicatorRadius = radius
        }

        fun mediumIndicatorRadius(radius: Float) = apply {
            this.mediumIndicantRadius = radius
        }

        fun smallIndicatorRadius(radius: Float) = apply {
            this.smallIndicatorRadius = radius
        }

        fun backgroundHeight(@Px height: Int) = apply {
            this.indicatorBackgroundHeight = height
        }

        fun backgroundHeightRes(@DimenRes heightRes: Int) = apply {
            this.indicatorBackgroundHeight = context.resources.getDimensionPixelSize(heightRes)
        }

        fun background(background: Drawable?) = apply {
            this.indicatorBackground = background
        }

        fun backgroundRes(@DrawableRes backgroundRes: Int) = apply {
            this.indicatorBackground = ContextCompat.getDrawable(context, backgroundRes)
        }

        fun indicatorMarginBottom(@Px marginBottom: Int) = apply {
            this.indicatorBottomMargin = marginBottom
        }

        fun indicatorMarginBottomRes(@DimenRes marginBottomRes: Int) = apply {
            this.indicatorBottomMargin = context.resources.getDimensionPixelSize(marginBottomRes)
        }

        fun maxVisibleIndicators(size: Int) = apply {
            this.maxVisibleIndicators = size
        }

        fun minVisibleIndicators(size: Int) = apply {
            this.minVisibleIndicators = size
        }

        fun edgeVisibleIndicators(size: Int) = apply {
            this.edgeVisibleIndicators = size
        }

        fun indicatorSpacing(@Px space: Int) = apply {
            this.indicatorSpacing = space
        }

        fun indicatorSpacingRes(@DimenRes spaceRes: Int) = apply {
            this.indicatorSpacing = context.resources.getDimensionPixelSize(spaceRes)
        }

        fun animationStartDelayed(delayed: Long) = apply {
            this.animationStartDelayed = delayed
        }

        fun animationStartDelayedWithoutShift(delayed: Long) = apply {
            this.animationStartDelayedWithoutShift = delayed
        }

        fun animationDuration(duration: Long) = apply {
            this.animationDuration = duration
        }

        fun animationDurationWithoutShift(duration: Long) = apply {
            this.animationDurationWithoutShift = duration
        }

        fun build(): CirclePageIndicatorConfigs {
            return CirclePageIndicatorConfigs(
                activeColor = activeColor,
                inactiveColor = inactiveColor,

                largeIndicatorRadius = largeIndicatorRadius,
                mediumIndicantRadius = mediumIndicantRadius,
                smallIndicatorRadius = smallIndicatorRadius,

                indicatorBackground = indicatorBackground,
                indicatorBackgroundHeight = indicatorBackgroundHeight,
                indicatorBottomMargin = indicatorBottomMargin,

                maxVisibleIndicators = maxVisibleIndicators,
                minVisibleIndicators = minVisibleIndicators,
                edgeVisibleIndicators = edgeVisibleIndicators,

                indicatorSpacing = indicatorSpacing,

                animationStartDelayed = animationStartDelayed,
                animationDuration = animationDuration,
                animationStartDelayedWithoutShift = animationStartDelayedWithoutShift,
                animationDurationWithoutShift = animationDurationWithoutShift
            )
        }
    }

}