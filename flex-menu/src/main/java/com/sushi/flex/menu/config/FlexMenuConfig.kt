package com.sushi.flex.menu.config

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.sushi.flex.menu.service.IFlexMenuInterpolator

/**
 *
 * @author: zhenghaoyu
 * @date: 2024/11/2
 */
class FlexMenuConfig private constructor(
    val selectItemIndex: Int,
    val animDuration: Long,
    var selectedDrawable: Drawable?,
    val flexMenuInterpolator: IFlexMenuInterpolator?,
    val interceptorEvent: Boolean,
) {

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().also(block).build()
    }

    class Builder {

        /**
         * 创建时起始item下标
         */
        private var selectedItemIndex = 0

        /**
         * 动效时间
         */
        private var duration: Long = 500L

        /**
         * 选中背景
         */
        private var selectedDrawable: Drawable = GradientDrawable()

        /**
         * 动画插值器
         */
        private var interpolator: IFlexMenuInterpolator? = null

        /**
         * 动画执行过程中，不允许点击
         */
        private var interceptClickEvent: Boolean = true

        /**
         * 设置创建起始item
         */
        fun setSelectedIndex(index: Int) {
            selectedItemIndex = index
        }

        /**
         * 设置动画时长
         */
        fun setDuration(duration: Long) {
            if (duration < 1) return
            this.duration = duration
        }

        /**
         * 设置item选中背景
         */
        fun setSelectDrawable(context: Context, @DrawableRes drawableRes: Int) {
            AppCompatResources.getDrawable(context, drawableRes)?.let { selectedDrawable = it }
        }

        /**
         * 设置插值器
         */
        fun setInterpolator(interpolator: IFlexMenuInterpolator) {
            this.interpolator = interpolator
        }

        /**
         * 是否需要等动画结素后再响应点击时间，默认true，防止快速点击
         */
        fun interceptEvent(interceptor: Boolean) {
            interceptClickEvent = interceptor
        }

        /**
         * 关闭执行动画
         */
        fun closeAnim() {
            duration = 1L
        }

        fun build(): FlexMenuConfig {
            return FlexMenuConfig(
                selectedItemIndex, duration, selectedDrawable, interpolator, interceptClickEvent
            )
        }
    }

    private fun getDrawableHeight(): Int {
        if (this.selectedDrawable == null) return 0
        val height = this.selectedDrawable!!.bounds.height()
        return if (height < 0) {
            this.selectedDrawable!!.intrinsicHeight
        } else {
            height
        }
    }

    private fun getDrawableWidth(): Int {
        if (this.selectedDrawable == null) return 0
        val width = this.selectedDrawable!!.bounds.width()
        return if (width < 0) {
            this.selectedDrawable!!.intrinsicWidth
        } else {
            width
        }
    }

    fun drawableDraw(canvas: Canvas) {
        if (getDrawableHeight() > 0 && getDrawableWidth() > 0) {
            this.selectedDrawable?.draw(canvas)
        }
    }
}