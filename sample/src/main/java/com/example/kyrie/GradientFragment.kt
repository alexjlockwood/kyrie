package com.example.kyrie

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.alexjlockwood.kyrie.KyrieDrawable
import com.github.alexjlockwood.kyrie.kyrieDrawable
import com.github.alexjlockwood.kyrie.path
import kotlinx.android.synthetic.main.fragment_two_pane.*

class GradientFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_two_pane, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        imageViewPane1.setImageDrawable(createLinearGradientDrawable())
        imageViewPane2.setImageDrawable(createRadialGradientDrawable())
    }

    private fun createLinearGradientDrawable(): KyrieDrawable {
        return kyrieDrawable {
            viewport(200f, 200f)
            path {
                pathData("M 0 0 h 200 v 200 h -200v -200 z")
                fillColor(LinearGradient(0f, 0f, 200f, 200f, Color.RED, Color.BLUE, Shader.TileMode.CLAMP))
            }
        }
    }

    private fun createRadialGradientDrawable(): KyrieDrawable {
        return kyrieDrawable {
            viewport(200f, 200f)
            path {
                pathData("M 0 0 h 200 v 200 h -200v -200 z")
                fillColor(RadialGradient(100f, 100f, 100f, Color.GREEN, Color.YELLOW, Shader.TileMode.CLAMP))
            }
        }
    }
}
