package com.example.kyrie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.github.alexjlockwood.kyrie.KyrieDrawable
import com.github.alexjlockwood.kyrie.kyrieDrawable
import com.github.alexjlockwood.kyrie.path
import kotlinx.android.synthetic.main.fragment_two_pane.*

class ColorStateListFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_two_pane, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        imageViewPane1.setImageDrawable(createColorStateListXmlDrawable())
        imageViewPane1.setOnClickListener {
            Toast.makeText(requireContext(), "Click!", Toast.LENGTH_SHORT).show()
        }
        imageViewPane2.setImageDrawable(createColorStateListBuilderDrawable())
        imageViewPane2.setOnClickListener {
            Toast.makeText(requireContext(), "Click!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createColorStateListXmlDrawable(): KyrieDrawable {
        return KyrieDrawable.create(requireContext(), R.drawable.vd_colorstatelist_test)!!
    }

    private fun createColorStateListBuilderDrawable(): KyrieDrawable {
        return kyrieDrawable {
            viewport(200f, 200f)
            path {
                pathData("M 0 0 h 200 v 200 h -200v -200 z")
                fillColor(AppCompatResources.getColorStateList(requireContext(), R.color.colorstatelist))
                
            }
        }
    }
}
