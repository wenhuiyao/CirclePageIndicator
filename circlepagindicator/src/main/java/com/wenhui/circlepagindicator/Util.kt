package com.wenhui.circlepagindicator

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue


internal fun Float.toDp(context: Context): Float {
  return TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
  )
}

internal fun Int.toDp(context: Context): Int {
  return TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
  ).toInt()
}