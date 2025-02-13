package com.android.deskclock

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object InsetsUtil {
    fun View.setInsetsListener(
        listener: (left: Int, top: Int, right: Int, bottom: Int) -> Unit
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val leftInsets = systemBarInsets.left + displayCutoutInsets.left
            val rightInsets = systemBarInsets.right + displayCutoutInsets.right
            listener(
                leftInsets,
                systemBarInsets.top,
                rightInsets,
                systemBarInsets.bottom
            )
            insets
        }
    }
}
