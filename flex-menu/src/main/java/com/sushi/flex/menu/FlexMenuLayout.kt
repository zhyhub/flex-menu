package com.sushi.flex.menu

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.sushi.flex.menu.config.FlexMenuConfig
import com.sushi.flex.menu.interpolator.FlexMenuInterpolator
import com.sushi.flex.menu.service.impl.FlexMenuItem
import java.util.concurrent.atomic.AtomicBoolean

class FlexMenuLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs), View.OnClickListener {

    init {
        setWillNotDraw(false)
    }

    private var flexConfig: FlexMenuConfig? = null
    private var currentView: FlexMenuItem? = null
    private var selectedAnimator: ValueAnimator? = null
    private val isRunning = AtomicBoolean(false)
    private var lastSelectedDrawable: Drawable? = null

    private val animatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            isRunning.set(true)
        }

        override fun onAnimationEnd(animation: Animator) {
            isRunning.set(false)
            selectedAnimator?.removeListener(this)
        }

        override fun onAnimationCancel(animation: Animator) {
        }

        override fun onAnimationRepeat(animation: Animator) {
        }
    }

    private var itemClickListenerBuilder: ItemClickListenerBuilder? = null

    inner class ItemClickListenerBuilder {
        internal var itemSelectListener: ((FlexMenuItem?, FlexMenuItem?) -> Unit)? = null

        fun onItemSelectListener(listener: (FlexMenuItem?, FlexMenuItem?) -> Unit) {
            itemSelectListener = listener
        }
    }

    fun registerItemClickListener(listenerBuilder: ItemClickListenerBuilder.() -> Unit) {
        itemClickListenerBuilder = ItemClickListenerBuilder().also(listenerBuilder)
    }

    /**
     * 配置动画时长，插值器，drawable样式
     */
    fun initFlexMenuConfig(config: FlexMenuConfig) {
        flexConfig = config
        lastSelectedDrawable = config.selectedDrawable
    }

    /**
     * [FlexMenuItem]设置监听
     */
    fun setFlexMenuItem(items: MutableList<FlexMenuItem>) {
        removeAllViews()
        items.forEachIndexed { index, flexMenuItem ->
            flexMenuItem.tag = index
            if (flexMenuItem.selectEnable) {
                flexMenuItem.setOnClickListener(this)
            }
            addView(flexMenuItem)
            if (index == (flexConfig?.selectItemIndex ?: 0)) {
                currentView = flexMenuItem
            }
        }
    }

    /**
     * 动态添加item到队列最后一个，自动选中新增item
     */
    fun addItem(item: FlexMenuItem) {
        item.tag = childCount
        if (item.selectEnable) {
            item.setOnClickListener(this)
        }
        currentView = item
        addView(item)
        postInvalidateOnAnimation()
        if (item.tag == 0) {
            if (flexConfig?.selectedDrawable == null) {
                flexConfig?.selectedDrawable = lastSelectedDrawable
            }
            setSelectedView(item)
        }
    }

    /**
     * 动态删除item
     */
    fun removeItem(index: Int) {
        if (childCount > 0) {
            if (childCount > index) {
                removeViewAt(index)
                Log.e("onLayout", "${this.childCount} , currentView:${currentView?.tag}")
                checkItems()
            }
        }
    }

    /**
     * 超出下标后重新赋值
     */
    private fun checkItems() {
        if (childCount > 0 && childCount == currentView?.tag) {
            currentView = getChildAt(childCount - 1) as? FlexMenuItem
            postInvalidateOnAnimation()
        }
    }

    override fun onClick(v: View?) {
        if (v != null && v is FlexMenuItem) {
            setSelectedView(v)
        }
    }

    /**
     * 设置选中的item
     */
    fun setSelectedView(item: FlexMenuItem) {
        if (currentView == null) currentView = item
        if (selectedAnimator != null && selectedAnimator!!.isRunning) selectedAnimator!!.cancel()
        if (currentView != item) {
            selectItem(currentView, item)
            currentView = item
            return
        }
        selectItem(null, item)
        currentView = item
    }

    /**
     * 执行切换
     * @param oldItem 原来的item
     * @param newItem 新的item
     */
    private fun selectItem(oldItem: FlexMenuItem?, newItem: FlexMenuItem?) {
        if (flexConfig?.selectedDrawable == null) return

        if (oldItem == null && newItem == null) return

        if (newItem == null && oldItem != null) {
            flexConfig?.flexMenuInterpolator?.setsSelectedBoundsForItem(
                oldItem, flexConfig?.selectedDrawable!!
            )
            return
        }

        if (newItem != null && oldItem == null) {
            flexConfig?.flexMenuInterpolator?.setsSelectedBoundsForItem(
                newItem, flexConfig?.selectedDrawable!!
            )
            return
        }

        selectedAnimator?.removeAllUpdateListeners()

        this.itemClickListenerBuilder?.itemSelectListener?.invoke(oldItem, newItem)

        selectedAnimator = ValueAnimator().apply {
            val animator = this
            animator.interpolator = FlexMenuInterpolator(flexConfig?.flexMenuInterpolator)
            animator.duration = flexConfig?.animDuration ?: 1L
            animator.addListener(animatorListener)
            animator.setFloatValues(0f, 1f)
            animator.addUpdateListener {
                if (oldItem != null && newItem != null) {
                    startMoveSelectedDrawable(oldItem, newItem, it.animatedFraction)
                }
            }
        }
        selectedAnimator?.start()
    }

    /**
     * 更新动画移动drawable
     */
    private fun startMoveSelectedDrawable(
        oldItem: FlexMenuItem, newItem: FlexMenuItem, animatedFraction: Float
    ) {
        if (flexConfig?.selectedDrawable == null) return
        flexConfig?.flexMenuInterpolator?.setSelectBoundsForOffset(
            orientation, oldItem, newItem, animatedFraction, flexConfig?.selectedDrawable!!
        )
        postInvalidateOnAnimation()
    }

    /**
     * 根据配置判断是否需要拦截点击事件
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (flexConfig == null) return super.onInterceptTouchEvent(ev)
        if (flexConfig!!.interceptorEvent && isRunning.get()) return true
        return super.onInterceptTouchEvent(ev)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (childCount == 0) flexConfig?.selectedDrawable = null
        if (selectedAnimator != null && selectedAnimator!!.isRunning) return
        if (currentView != null && flexConfig?.selectedDrawable != null) {
            flexConfig?.flexMenuInterpolator?.setsSelectedBoundsForItem(
                currentView!!, flexConfig?.selectedDrawable!!
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (flexConfig == null) return
        flexConfig!!.drawableDraw(canvas)
        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }

    private fun release() {
        currentView = null
        itemClickListenerBuilder = null
        flexConfig = null
        selectedAnimator?.let {
            it.removeListener(animatorListener)
            it.removeAllUpdateListeners()
            it.removeAllListeners()
            it.cancel()
        }
    }
}