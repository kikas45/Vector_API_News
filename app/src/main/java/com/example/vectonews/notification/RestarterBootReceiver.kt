package com.example.vectonews.notification

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity



class RestarterBootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {

        if (intent!!.action.equals("RestarterBootReceiver")) {
            if (!foregroundServiceRunning(context)) {
                context.applicationContext.startService(
                    Intent(
                        context.applicationContext, NotificationService::class.java
                    )
                )
            } // else do nothing

        }

    }


    fun foregroundServiceRunning(context: Context): Boolean {
        val activityManager =
            context.applicationContext.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (NotificationService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }


}