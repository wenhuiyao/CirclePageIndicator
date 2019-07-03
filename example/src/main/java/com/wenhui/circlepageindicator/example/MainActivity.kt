package com.wenhui.circlepageindicator.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    sampleRecyclerView.let {
      it.setHasFixedSize(true)
      it.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
      it.adapter = SampleAdapter(this)
    }
  }

}

private data class Data(val title: String,
                        val clazzName: String)

private val dataset = arrayOf(
  Data("RecyclerView Page Indicator", RecyclerViewPageIndicatorFragment::class.java.name),
  Data("ViewPager Page Indicator", ViewPagerPageIndicatorFragment::class.java.name)
)

private class SampleAdapter(private val context: AppCompatActivity): RecyclerView.Adapter<SampleViewHolder>() {

  override fun getItemCount(): Int = dataset.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
    return SampleViewHolder(context, parent)
  }

  override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
    holder.bindData(dataset[position])
  }

}


private class SampleViewHolder(private val context: AppCompatActivity,
                               parent: ViewGroup)
  : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_item_view, parent, false)) {

  private var data: Data? = null

  init {
    itemView.setOnClickListener {
      val d = data ?: return@setOnClickListener

      val fragment = Fragment.instantiate(context, d.clazzName)
      context.supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragmentContainer, fragment)
        .addToBackStack(null)
        .commit()
    }
  }


  fun bindData(data: Data) {
    this.data = data
    (itemView as TextView).text = data.title
  }
}