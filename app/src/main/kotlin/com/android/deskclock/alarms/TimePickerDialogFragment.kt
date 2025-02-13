/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.deskclock.alarms

import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

/**
 * DialogFragment used to show TimePicker.
 */
object TimePickerDialogFragment {
    /**
     * The callback interface used to indicate the user is done filling in the time (e.g. they
     * clicked on the 'OK' button).
     */
    interface OnTimeSetListener {
        /**
         * Called when the user is done setting a new time and the dialog has closed.
         *
         * @param hourOfDay the hour that was set
         * @param minute the minute that was set
         */
        fun onTimeSet(hourOfDay: Int, minute: Int)
    }

    /**
     * Tag for timer picker fragment in FragmentManager.
     */
    const val TAG = "TimePickerDialogFragment"

    @JvmStatic
    fun show(fragment: Fragment) {
        show(fragment, -1 /* hour */, -1 /* minute */)
    }

    fun show(parentFragment: Fragment, hourOfDay: Int, minute: Int) {
        val manager: FragmentManager = parentFragment.parentFragmentManager
        if (manager.isDestroyed) {
            return
        }

        val picker = MaterialTimePicker.Builder()
            .setHour(hourOfDay)
            .setMinute(minute)
            .setTimeFormat(
                if (DateFormat.is24HourFormat(parentFragment.context)) {
                    TimeFormat.CLOCK_24H
                } else {
                    TimeFormat.CLOCK_12H
                }
            )
            .build()

        picker.addOnPositiveButtonClickListener {
            (parentFragment as OnTimeSetListener).onTimeSet(picker.hour, picker.minute)
        }

        picker.show(manager, TAG)
    }

    @JvmStatic
    fun removeTimeEditDialog(manager: FragmentManager?) {
        manager?.let { manager ->
            val prev: Fragment? = manager.findFragmentByTag(TAG)
            prev?.let {
                manager.beginTransaction().remove(it).commit()
            }
        }
    }
}
