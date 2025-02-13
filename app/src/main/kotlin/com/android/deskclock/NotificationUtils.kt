/*
 * Copyright (C) 2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.deskclock

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.content.Context
import android.util.ArraySet
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH

object NotificationUtils {
    private val TAG = NotificationUtils::class.java.simpleName

    /**
     * Notification channel containing all missed alarm notifications.
     */
    const val ALARM_MISSED_NOTIFICATION_CHANNEL_ID = "alarmMissedNotification"

    /**
     * Notification channel containing all upcoming alarm notifications.
     */
    const val ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID = "alarmUpcomingNotification"

    /**
     * Notification channel containing all snooze notifications.
     */
    const val ALARM_SNOOZE_NOTIFICATION_CHANNEL_ID = "alarmSnoozingNotification"

    /**
     * Notification channel containing all firing alarm and timer notifications.
     */
    const val FIRING_NOTIFICATION_CHANNEL_ID = "firingAlarmsAndTimersNotification"

    /**
     * Notification channel containing all TimerModel notifications.
     */
    const val TIMER_MODEL_NOTIFICATION_CHANNEL_ID = "timerNotification"

    /**
     * Notification channel containing all stopwatch notifications.
     */
    const val STOPWATCH_NOTIFICATION_CHANNEL_ID = "stopwatchNotification"

    private data class ChannelProperties(
        @StringRes val nameId: Int,
        val importance: Int = IMPORTANCE_DEFAULT,
        val vibration: Boolean = false,
        val sound: Boolean = false,
        val lights: Boolean = false
    )

    private val CHANNEL_PROPS: MutableMap<String, ChannelProperties> = HashMap()

    init {
        CHANNEL_PROPS[ALARM_MISSED_NOTIFICATION_CHANNEL_ID] = ChannelProperties(
            nameId = R.string.alarm_missed_channel,
            importance = IMPORTANCE_HIGH,
            vibration = true,
            sound = true
        )
        CHANNEL_PROPS[ALARM_SNOOZE_NOTIFICATION_CHANNEL_ID] = ChannelProperties(
            nameId = R.string.alarm_snooze_channel
        )
        CHANNEL_PROPS[ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID] = ChannelProperties(
            nameId = R.string.alarm_upcoming_channel
        )
        CHANNEL_PROPS[FIRING_NOTIFICATION_CHANNEL_ID] = ChannelProperties(
            nameId = R.string.firing_alarms_timers_channel,
            importance = IMPORTANCE_HIGH,
            lights = true
        )
        CHANNEL_PROPS[STOPWATCH_NOTIFICATION_CHANNEL_ID] = ChannelProperties(
            nameId = R.string.stopwatch_channel
        )
        CHANNEL_PROPS[TIMER_MODEL_NOTIFICATION_CHANNEL_ID] = ChannelProperties(
            nameId = R.string.timer_channel
        )
    }

    @SuppressLint("WrongConstant")
    @JvmStatic
    fun createChannel(context: Context, id: String) {
        if (!Utils.isOOrLater) {
            return
        }

        if (!CHANNEL_PROPS.containsKey(id)) {
            Log.e(TAG, "Invalid channel requested: $id")
            return
        }

        val properties = CHANNEL_PROPS[id]!!
        val nameId = properties.nameId
        val channel = NotificationChannel(id, context.getString(nameId), properties.importance)
        channel.enableVibration(properties.vibration)
        if (!properties.sound) {
            channel.setSound(null, null)
        }
        val nm: NotificationManagerCompat = NotificationManagerCompat.from(context)
        nm.createNotificationChannel(channel)
    }

    private fun deleteChannel(nm: NotificationManagerCompat, channelId: String) {
        val channel: NotificationChannel? = nm.getNotificationChannel(channelId)
        if (channel != null) {
            nm.deleteNotificationChannel(channelId)
        }
    }

    private fun getAllExistingChannelIds(nm: NotificationManagerCompat): Set<String> {
        val result: MutableSet<String> = ArraySet()
        for (channel in nm.getNotificationChannels()) {
            result.add(channel.id)
        }
        return result
    }

    @JvmStatic
    fun updateNotificationChannels(context: Context) {
        if (!Utils.isOOrLater) {
            return
        }

        val nm: NotificationManagerCompat = NotificationManagerCompat.from(context)

        // These channels got a new behavior so we need to recreate them with new ids
        deleteChannel(nm, "alarmLowPriorityNotification")
        deleteChannel(nm, "alarmHighPriorityNotification")
        deleteChannel(nm, "StopwatchNotification")
        deleteChannel(nm, "alarmNotification")
        deleteChannel(nm, "TimerModelNotification")
        deleteChannel(nm, "alarmSnoozeNotification")

        // We recreate all existing channels so any language change or our name changes propagate
        // to the actual channels
        val existingChannelIds = getAllExistingChannelIds(nm)
        for (id in existingChannelIds) {
            createChannel(context, id)
        }
    }
}
