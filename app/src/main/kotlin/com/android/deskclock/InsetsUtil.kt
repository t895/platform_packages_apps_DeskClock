package com.android.deskclock

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max

object InsetsUtil {
    inline fun <reified T : View> T.setInsetsListener(
        crossinline listener: T.(left: Int, top: Int, right: Int, bottom: Int) -> Unit
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val leftInsets = max(systemBarInsets.left, displayCutoutInsets.left)
            val topInsets = max(systemBarInsets.top, displayCutoutInsets.top)
            val rightInsets = max(systemBarInsets.right, displayCutoutInsets.right)
            val bottomInsets = max(systemBarInsets.bottom, displayCutoutInsets.bottom)
            this.listener(
                leftInsets,
                topInsets,
                rightInsets,
                bottomInsets
            )
            insets
        }
    }
}
