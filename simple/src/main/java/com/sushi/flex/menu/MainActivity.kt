package com.sushi.flex.menu

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sushi.flex.menu.config.FlexMenuConfig
import com.sushi.flex.menu.service.impl.FlexMenuInterpolatorImpl
import com.sushi.flex.menu.service.impl.FlexMenuItem
import com.sushi.flex.menu.simple.R

class MainActivity : AppCompatActivity() {

    private lateinit var flexMenu: FlexMenuLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        flexMenu = findViewById(R.id.flex_menu)

        FlexMenuConfig.build {
            setSelectedIndex(0)
            setDuration(1000L)
            setSelectDrawable(this@MainActivity, R.drawable.shape_flex_menu_item)
            setInterpolator(FlexMenuInterpolatorImpl())
            interceptEvent(true)
            build()
        }.also {
            with(flexMenu) {
                initFlexMenuConfig(it)
                registerItemClickListener {
                    onItemSelectListener { old, new ->
                        Log.v("itemClick", "old:${old?.tag}")
                        Log.v("itemClick", "new:${new?.tag}")
                    }
                }
                setFlexMenuItem(createFlexMenuItems())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createFlexMenuItems(): MutableList<FlexMenuItem> {
        val lp = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT, 150
        )
        val items = mutableListOf<FlexMenuItem>()
        var item: FlexMenuItem
        for (i in 0 until 10) {
            item = FlexMenuItem(this)
            with(item) {
                layoutParams = lp
                setTextColor(Color.WHITE)
                textSize = 16f
                gravity = Gravity.CENTER
                text = "第${i + 1}个item"
                items.add(item)
            }
        }
        return items
    }
}