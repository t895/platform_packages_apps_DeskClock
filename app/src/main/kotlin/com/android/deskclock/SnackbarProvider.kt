package com.android.deskclock

import com.google.android.material.snackbar.Snackbar

interface SnackbarProvider {
    /**
     * @return a Snackbar that displays the message with the given text for [duration] milliseconds
     */
    fun createSnackbar(text: String, duration: Int): Snackbar
}
