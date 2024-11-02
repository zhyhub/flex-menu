package com.sushi.flex.menu.interpolator

import android.view.animation.BaseInterpolator
import com.sushi.flex.menu.service.IFlexMenuInterpolator

/**
 *
 * @author: zhenghaoyu
 * @date: 2024/11/2
 *
 * 变形拉伸效果，由一端拉扯另一端往目标方向移动，例如从上往下移动时
 * fraction=((2.0).pow((-10 * input).toDouble()) * sin((input - 1.5 / 4) * (2 * Math.PI) / 1.5) + 1).toFloat(),factor=1.5
 */
class FlexMenuInterpolator(private val interpolator: IFlexMenuInterpolator?) : BaseInterpolator() {
    override fun getInterpolation(input: Float): Float {
        return interpolator?.getInterpolation(input) ?: 0f
    }
}