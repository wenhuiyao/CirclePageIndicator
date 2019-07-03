package com.wenhui.circlepageindicator.example

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.wenhui.circlepageindicator.viewpager2.ViewPagerPageIndicator
import kotlinx.android.synthetic.main.fragment_view_pager_page_indicator.view.*

class ViewPagerPageIndicatorFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_view_pager_page_indicator, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val viewPager = view.viewPager

    viewPager.adapter = SamplePageAdapter()
    ViewPagerPageIndicator().attachTo(view.viewPager)
  }

  private class SamplePageAdapter : RecyclerView.Adapter<SampleViewHolder>() {

    private val dataset = intArrayOf(
      randomColor(),
      randomColor(),
      randomColor(),
      randomColor()
    )

    override fun getItemCount(): Int = dataset.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
      return SampleViewHolder(parent.context)
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
      holder.bindData(dataset[position])
    }

  }

  private class SampleViewHolder(context: Context) : RecyclerView.ViewHolder(View(context)) {

    init {
      itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun bindData(color: Int) {
      itemView.setBackgroundColor(color)
    }
  }
}
