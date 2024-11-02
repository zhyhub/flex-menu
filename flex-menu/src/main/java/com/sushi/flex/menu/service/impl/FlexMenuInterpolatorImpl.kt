package com.sushi.flex.menu.service.impl

import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.LinearLayoutCompat
import com.sushi.flex.menu.service.IFlexMenuInterpolator
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 *
 * @author: zhenghaoyu
 * @date: 2024/11/2
 */
class FlexMenuInterpolatorImpl : IFlexMenuInterpolator {

    override fun getInterpolation(input: Float): Float {
        return ((2.0).pow((-10 * input).toDouble()) * sin((input - 1.5 / 4) * (2 * Math.PI) / 1.5) + 1).toFloat()
    }

    override fun decInterpolator(fraction: Float): Float {
        return ((1.0 - cos((fraction * Math.PI) / 2.0)).toFloat())
    }

    override fun accInterpolator(fraction: Float): Float {
        return sin(fraction * Math.PI / 2.0).toFloat()
    }

    override fun setsSelectedBoundsForItem(view: FlexMenuItem, drawable: Drawable) {
        drawable.setBounds(view.left, view.top, view.right, view.bottom)
    }

    override fun setSelectBoundsForOffset(
        orientation: Int,
        startView: FlexMenuItem,
        endView: FlexMenuItem,
        offset: Float,
        drawable: Drawable
    ) {
        val startRect = with(startView) {
            RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        }
        val endRect = with(endView) {
            RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        }
        if (orientation == LinearLayoutCompat.HORIZONTAL) {
            horizontalMove(startRect, endRect, offset, drawable)
        } else {
            verticalMove(startRect, endRect, offset, drawable)
        }
    }

    private fun horizontalMove(
        startRect: RectF, endRect: RectF, offset: Float, drawable: Drawable
    ) {
        val movingRight = startRect.left < endRect.left
        val leftFraction = if (movingRight) decInterpolator(offset) else accInterpolator(offset)
        val rightFraction = if (movingRight) accInterpolator(offset) else decInterpolator(offset)
        drawable.setBounds(
            interpolator(startRect.left.toInt(), endRect.left.toInt(), leftFraction),
            drawable.bounds.top,
            interpolator(startRect.right.toInt(), endRect.right.toInt(), rightFraction),
            drawable.bounds.bottom
        )
    }

    private fun verticalMove(startRect: RectF, endRect: RectF, offset: Float, drawable: Drawable) {
        val movingBottom = startRect.top < endRect.top
        val topFraction = if (movingBottom) decInterpolator(offset) else accInterpolator(offset)
        val bottomFraction = if (movingBottom) accInterpolator(offset) else decInterpolator(offset)
        drawable.setBounds(
            drawable.bounds.left,
            interpolator(startRect.top.toInt(), endRect.top.toInt(), topFraction),
            drawable.bounds.right,
            interpolator(startRect.bottom.toInt(), endRect.bottom.toInt(), bottomFraction),
        )
    }

    private fun interpolator(startValue: Int, endValue: Int, fraction: Float): Int {
        return startValue + Math.round(fraction * (endValue - startValue))
    }

}