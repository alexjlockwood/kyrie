package com.example.kyrie

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

private val DEMOS = arrayOf(
        DemoListFragment.Demo("Polygons", PolygonsFragment::class.java.name),
        DemoListFragment.Demo("Progress bars", ProgressFragment::class.java.name),
        DemoListFragment.Demo("Path morphing", PathMorphFragment::class.java.name),
        DemoListFragment.Demo("Heartbreak", HeartbreakFragment::class.java.name)
)

class DemoListFragment : Fragment() {

    private lateinit var callbacks: Callbacks

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is Callbacks) {
            throw IllegalArgumentException("Host must implement Callbacks interface")
        }
        callbacks = context
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_demo_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.demo_list)
        recyclerView.adapter = Adapter()
        return view
    }

    private inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ViewHolder(inflater.inflate(R.layout.fragment_demo_item, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(DEMOS[position])
        }

        override fun getItemCount(): Int {
            return DEMOS.size
        }
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val textView: TextView
        private var demo: Demo? = null

        init {
            itemView.setOnClickListener(this)
            textView = itemView.findViewById(R.id.demo_text)
        }

        fun bind(d: Demo) {
            demo = d
            textView.text = d.title
        }

        override fun onClick(v: View) {
            callbacks.onListItemClick(demo!!)
        }
    }

    data class Demo(val title: String, val fragmentClassName: String)

    interface Callbacks {
        fun onListItemClick(demo: Demo)
    }
}
