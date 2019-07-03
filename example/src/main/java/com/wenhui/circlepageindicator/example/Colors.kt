package com.wenhui.circlepageindicator.example

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import java.util.*

private val random = Random()

@ColorInt fun randomColor(): Int {
  return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255))
}