package com.wenhui.circlepageindicator.example

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.wenhui.recyclerview.RecyclerViewPageIndicator
import kotlinx.android.synthetic.main.example_item.view.*
import kotlinx.android.synthetic.main.fragment_recyclerview_page_indicator.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class RecyclerViewPageIndicatorFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_recyclerview_page_indicator, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val recyclerView = view.exampleRecyclerView
    recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    recyclerView.adapter = ExampleAdapter(requireContext())
    recyclerView.setHasFixedSize(true)
    recyclerView.isNestedScrollingEnabled = false

  }
}

private class ExampleAdapter(context: Context) : RecyclerView.Adapter<ExampleViewHolder>() {

  private val layoutInflater = LayoutInflater.from(context)

  override fun getItemCount(): Int = 50

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
    return ExampleViewHolder(layoutInflater, parent)
  }

  override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
    holder.bindData(position)
  }
}

private class ExampleViewHolder(
  layoutInflater: LayoutInflater,
  parent: ViewGroup?
) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.example_item, parent, false)) {

  private val recyclerView = itemView.colorRecyclerview
  private val titleView = itemView.titleView
  private val countView = itemView.countView

  private val adapter = ColorAdapter()

  init {
    recyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
    recyclerView.isNestedScrollingEnabled = false
    recyclerView.setHasFixedSize(true)

    PagerSnapHelper().attachToRecyclerView(recyclerView)
    RecyclerViewPageIndicator().attachToRecyclerView(recyclerView)

    recyclerView.adapter = adapter
  }

  @SuppressLint("SetTextI18n")
  fun bindData(position: Int) {
    titleView.text = "Item $position"
    adapter.updateData(position + 1)
    countView.text = "Showing ${position + 1} pages"
  }

  private class ColorAdapter : RecyclerView.Adapter<ColorViewHolder>() {

    private var itemCount: Int = 0

    fun updateData(itemCount: Int) {
      this.itemCount = itemCount
      notifyDataSetChanged()
    }

    override fun getItemCount(): Int = itemCount

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
      return ColorViewHolder(parent.context)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
      holder.bindHolder()
    }
  }
}

private class ColorViewHolder(context: Context) : RecyclerView.ViewHolder(View(context)) {

  init {
    itemView.layoutParams = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )

  }

  fun bindHolder() {
    itemView.setBackgroundColor(randomColor())
  }
}
