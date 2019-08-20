package com.glong.demo.entry

import androidx.annotation.DrawableRes

/**
 * @author guolong
 * @since 2019/8/19
 */
data class HomePlanetBean(
        val planetName: String,//行星名字
        @DrawableRes val planetNormalImage: Int,//行星未激活图片
        @DrawableRes val planetActivateImage: Int,// 行星已激活图片
        val needStars: Int, // 需要多少星星才能激活
        val isActivated: Boolean, // 是否已激活
        val canActivate: Boolean // 是否可以激活
)