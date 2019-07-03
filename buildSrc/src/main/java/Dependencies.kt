object Versions {
  val kotlin = "1.3.40"
  val min_sdk = 19
  val target_sdk = 28
  val compile_sdk = 28
  val version_code = 1
  val version_name = "0.5"
  val android_gradle_plugin = "3.4.1"
  val app_compat="1.0.2"
  val core_ktx="1.0.2"
  val constraint_layout="1.1.3"
  val material="1.0.0"
  val recyclerview="1.0.0"
  val viewPager2="1.0.0-alpha06"
}

object Deps {
  val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
  val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
  val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
  val app_compat = "androidx.appcompat:appcompat:${Versions.app_compat}"
  val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
  val constraint_layout = "androidx.constraintlayout:constraintlayout:${Versions.constraint_layout}"
  val material = "com.google.android.material:material:${Versions.material}"
  val recyclerview = "com.android.support:recyclerview-v7:${Versions.recyclerview}"
  val view_pager2 = "androidx.viewpager2:viewpager2:${Versions.viewPager2}"
}