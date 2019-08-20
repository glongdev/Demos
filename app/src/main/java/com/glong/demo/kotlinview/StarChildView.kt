package com.glong.demo.kotlinview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.glong.demo.R
import com.glong.demo.entry.HomePlanetBean
import kotlinx.android.synthetic.main.layout_star_child.view.*
import org.jetbrains.anko.toast

/**
 * @author guolong
 * @since 2019/8/18
 */
@SuppressLint("ViewConstructor")
class StarChildView(planetBean: HomePlanetBean, context: Context) : ConstraintLayout(context) {

    init {
        setWillNotDraw(false)
        LayoutInflater.from(context).inflate(R.layout.layout_star_child, this)
        button.setOnClickListener {
            context.toast("Button clicked")
        }

        planetBean.let {
            button.text = it.planetName
            if (it.isActivated) {
                appCompatImageView.setImageResource(it.planetActivateImage)
                textView.text = String.format("%d/%d",planetBean.needStars,planetBean.needStars)
                progressBar.progress = 100
            } else {
                appCompatImageView.setImageResource(it.planetNormalImage)
                progressBar.progress = 0
                textView.text = String.format("%d/%d",0,planetBean.needStars)
            }

            if (it.isActivated || it.canActivate) {
                button.setBackgroundResource(R.drawable.bg_award_exchange_enable)
                button.isEnabled = true
            } else {
                button.setBackgroundResource(R.drawable.bg_award_exchange_disenable)
                button.isEnabled = false
            }

            button.setOnClickListener {
                context.toast(planetBean.planetName)
            }
        }
    }
}
