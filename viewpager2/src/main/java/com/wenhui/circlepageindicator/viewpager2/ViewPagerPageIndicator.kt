package com.wenhui.circlepageindicator.viewpager2

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.wenhui.circlepagindicator.CirclePageIndicator
import com.wenhui.circlepagindicator.CirclePageIndicatorConfigs

class ViewPagerPageIndicator {

  private var adapter: RecyclerView.Adapter<*>? = null
  private var dataObserver: RecyclerView.AdapterDataObserver? = null

  fun attachTo(
    viewPager: ViewPager2,
    configs: CirclePageIndicatorConfigs = defaultCirclePageIndicatorConfigs(
      viewPager.context
    )
  ) {
    val pageIndicator = CirclePageIndicator(viewPager, configs)

    viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        if (tryRegisterAdapter(viewPager, pageIndicator))
        pageIndicator.onPageSelected(position)
      }
    })

    viewPager.addItemDecoration(object: RecyclerView.ItemDecoration() {
      override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        pageIndicator.draw(c)
      }
    })
  }

  private fun tryRegisterAdapter(
    viewPager: ViewPager2,
    pageIndicator: CirclePageIndicator
  ): Boolean {
    val adapter = viewPager.adapter
    if (adapter == null) {
      // cleanup current adapter
      unregisterDataObserver()
      this.adapter = null
      return false
    }


    if (adapter !== this.adapter) {
      unregisterDataObserver()
      pageIndicator.onDataUpdated(adapter.itemCount, viewPager.currentItem)
      val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
          pageIndicator.onDataUpdated(adapter.itemCount, viewPager.currentItem)
        }
      }
      adapter.registerAdapterDataObserver(dataObserver)
      this.dataObserver = dataObserver
      this.adapter = adapter
    }

    return true
  }

  private fun unregisterDataObserver() {
    val dob = this.dataObserver
    if (dob != null) {
      this.adapter?.unregisterAdapterDataObserver(dob)
    }
    this.dataObserver = null
  }

}

private fun defaultCirclePageIndicatorConfigs(context: Context)
    : CirclePageIndicatorConfigs {
  return CirclePageIndicatorConfigs.Builder(context)
    .maxVisibleIndicators(7)
    .minVisibleIndicators(2)
    .edgeVisibleIndicators(4)
    .build()
}