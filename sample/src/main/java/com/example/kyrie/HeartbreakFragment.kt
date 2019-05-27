package com.example.kyrie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.Fragment

import com.github.alexjlockwood.kyrie.KyrieDrawable

class HeartbreakFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var seekBar: SeekBar

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_seekbar, container, false)
        imageView = view.findViewById(R.id.image_view)
        seekBar = view.findViewById(R.id.seekbar)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val drawable = KyrieDrawable.create(requireContext(), R.drawable.avd_heartbreak)!!
        drawable.addListener(SampleListenerAdapter(seekBar))
        imageView.setImageDrawable(drawable)
        imageView.setOnClickListener(SampleOnClickListener(drawable))
        seekBar.setOnSeekBarChangeListener(SampleOnSeekBarChangeListener(drawable))
    }
}
