package com.glong.demo.kotlinview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.glong.demo.R
import kotlinx.android.synthetic.main.layout_sun_view.view.*

/**
 * @author guolong
 * @since 2019/8/19
 */
class SunView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_sun_view,this)
        iv_sun_light.startAnimation(AnimationUtils.loadAnimation(context, R.anim.sun_anim))
    }
}
