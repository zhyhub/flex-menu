package com.sushi.flex.menu.service.impl

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.sushi.flex.menu.service.IFlexMenuItem

/**
 *
 * @author: zhenghaoyu
 * @date: 2024/11/2
 */
class FlexMenuItem @JvmOverloads constructor(
    context: Context, override val selectEnable: Boolean = true, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs), IFlexMenuItem