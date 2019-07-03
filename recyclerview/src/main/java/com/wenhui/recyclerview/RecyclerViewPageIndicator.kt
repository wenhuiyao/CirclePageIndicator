package com.wenhui.recyclerview

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import com.wenhui.circlepagindicator.CirclePageIndicator
import com.wenhui.circlepagindicator.CirclePageIndicatorConfigs

/**
 * Showing horizontal circle page indicator for RecyclerView. Internally, it is relied on [CirclePageIndicator] for showing
 * indicators
 */
class RecyclerViewPageIndicator {

  private var adapter: RecyclerView.Adapter<*>? = null
  private var dataObserver: RecyclerView.AdapterDataObserver? = null

  @JvmOverloads
  fun attachToRecyclerView(
    recyclerView: RecyclerView,
    configs: CirclePageIndicatorConfigs = CirclePageIndicatorConfigs(recyclerView.context)
  ) {
    val pageIndicator = CirclePageIndicator(recyclerView, configs)
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!tryRegisterAdapter(recyclerView, pageIndicator)) {
          return
        }

        val selectedPosition = findSelectedPagePosition(recyclerView)
        pageIndicator.onPageSelected(selectedPosition)
      }
    })

    recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
      override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        pageIndicator.draw(c)
      }
    })
  }

  private fun tryRegisterAdapter(
    recyclerView: RecyclerView,
    pageIndicator: CirclePageIndicator<RecyclerView>
  ): Boolean {
    val adapter = recyclerView.adapter
    if (adapter == null) {
      // cleanup current adapter
      unregisterDataObserver()
      this.adapter = null
      return false
    }

    fun onDataUpdated() {
      val position = findSelectedPagePosition(recyclerView)
      pageIndicator.onDataUpdated(adapter.itemCount, position)
    }

    if (adapter !== this.adapter) {
      unregisterDataObserver()
      onDataUpdated()
      val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
          onDataUpdated()
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

  private fun findSelectedPagePosition(recyclerView: RecyclerView): Int {
    val centerX = recyclerView.width / 2f
    val centerY = recyclerView.height / 2f
    val centerChild = recyclerView.findChildViewUnder(centerX, centerY)
    return if (centerChild == null) -1 else recyclerView.getChildAdapterPosition(centerChild)
  }

}