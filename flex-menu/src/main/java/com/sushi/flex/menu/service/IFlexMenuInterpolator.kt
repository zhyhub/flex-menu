package com.sushi.flex.menu.service

import android.graphics.drawable.Drawable
import androidx.annotation.FloatRange
import com.sushi.flex.menu.service.impl.FlexMenuItem

/**
 *
 * @author: zhenghaoyu
 * @date: 2024/11/2
 */
interface IFlexMenuInterpolator {

    /**
     * 变形拉伸效果
     */
    fun getInterpolation(input: Float): Float

    /**
     * 先移动的一端插值计算
     */
    fun decInterpolator(@FloatRange(0.0, 1.0) fraction: Float): Float

    /**
     * 被拉扯一端插值计算
     */
    fun accInterpolator(@FloatRange(0.0, 1.0) fraction: Float): Float

    /**
     * 设置选中的背景坐标
     */
    fun setsSelectedBoundsForItem(view: FlexMenuItem, drawable: Drawable)

    /**
     * 设置drawable背景
     */
    fun setSelectBoundsForOffset(
        orientation: Int,
        startView: FlexMenuItem,
        endView: FlexMenuItem,
        offset: Float,
        drawable: Drawable
    )

}