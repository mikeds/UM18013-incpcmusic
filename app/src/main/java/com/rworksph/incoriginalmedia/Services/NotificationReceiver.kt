package com.rworksph.incoriginalmedia.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.rworksph.incoriginalmedia.Home


class NotificationReceiver:BroadcastReceiver() {
    val home = Home()
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {
        context.sendBroadcast(Intent("ACTION")
            .putExtra("actionName", intent.action))


       // Log.e("na","call ba to?")
    }
}