package com.example.vectonews.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.vectonews.MainActivity
import com.example.vectonews.R
import com.example.vectonews.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class NotificationService : Service() {

    private var isRunning = false
    private val thirtyMinutesInMillis = 5 * 60 * 1000 // to fetch news after 30 minutes from now
    private val handleTimeIn25Minutes = 2.5 * 60 * 1000 // to check for news in 25 minutes time from starts
    private val deleteNotification = 4.8 * 60 * 1000 // to fetch news after 30 minutes from now

    private val myHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    val connectivityManager: ConnectivityManager by lazy {
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    val networkInfo: NetworkInfo? by lazy {
        connectivityManager.activeNetworkInfo
    }


    private val shared_time_count: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(
            Constants.SHARED_TIME,
            Context.MODE_PRIVATE
        )
    }


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startMyOwnForeground()

        } else {
            startForeground(1, Notification())

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        //   stopService(Intent(this, NotificationService::class.java))


        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {

            isRunning = true

            try {
                val editor = shared_time_count.edit()
                editor.putLong("saved_time", System.currentTimeMillis().toLong())
                editor.apply()

                if (networkInfo != null && networkInfo!!.isConnected) {
                    makeAPIRequest()
                }


                myHandler.postDelayed(runnable, handleTimeIn25Minutes.toLong())
            } catch (_: Exception) {
            }


        }, 5000)



        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        isRunning = false

        val getTrueStae = shared_time_count.getString(Constants.isActive, "")
        if (isRunning == false) {
            if (getTrueStae.equals("Button_Active")) {
                myHandler.removeCallbacksAndMessages(runnable)
                val broadcastIntent = Intent()
                broadcastIntent.action = "RestarterBootReceiver"
                broadcastIntent.setClass(this, RestarterBootReceiver::class.java)
                this.sendBroadcast(broadcastIntent)
            }
        }
    }


    private val runnable: Runnable = object : Runnable {
        override fun run() {

            val getTime = shared_time_count.getLong("saved_time", 1L)
            val currentTimeMillis = System.currentTimeMillis()

            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (currentTimeMillis >= getTime + deleteNotification) {
                val getNotifyId = shared_time_count.getInt("NotifyID", 1)
                notificationManager.cancel(getNotifyId) // Cancel the notification
            }

            if (currentTimeMillis >= getTime + thirtyMinutesInMillis) {
                val editor = shared_time_count.edit()
                editor.putLong("saved_time", System.currentTimeMillis().toLong())
                editor.apply()

                if (networkInfo != null && networkInfo!!.isConnected) {
                    makeAPIRequest()
                }

            } else {
                //  Toast.makeText(applicationContext, "handling", Toast.LENGTH_SHORT).show()
            }

            myHandler.postDelayed(this, handleTimeIn25Minutes.toLong())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {


        val intent = Intent(applicationContext, MainActivity::class.java)
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("James", "Powell")
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val getTitle = shared_time_count.getString("NewsTitle", "")
        val NewsTitle: String
        if (getTitle!!.isNotEmpty()) {
            NewsTitle = getTitle
        } else {
            NewsTitle = "Checking for News Headlines"
        }

        val builder = NotificationCompat.Builder(applicationContext, "ChannelId")
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setContentText(NewsTitle + "")
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ChannelId", "News", importance)

            notificationManager.createNotificationChannel(channel)

            startForeground(2, builder.build())

        }

    }


    private fun makeAPIRequest() {

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("James", "Powell")
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val randomID = Random.nextInt()

        val ChannelId = "Powell$randomID.toString()"


        val builder = NotificationCompat.Builder(applicationContext, ChannelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        val coroutineScope = CoroutineScope(Dispatchers.IO)

        coroutineScope.launch {
            try {
                // val editor22 = shared_time_count.edit()
                val editor = shared_time_count.edit()

                val response = RetrofitInstanceWorker.api.getNews()

                if (response.news.isNotEmpty()) {

                    val random = Random.nextInt(25)
                    val firstArticle = response.news[random]
                    val title = firstArticle.title + ""
                    val description = firstArticle.description + ""
                    val getUrl = firstArticle.url + ""
                    val urlToImage = firstArticle.url + ""
                    val author = firstArticle.author + ""
                    val publishedAt = firstArticle.published + ""

                    intent.putExtra("title", title)

                    withContext(Dispatchers.Main) {
                        builder.setContentTitle(title)
                        builder.setContentText(description)
                        editor.putString("NewsTitle", title + "")
                        editor.putString("getUrl", getUrl + "")
                        editor.putString("urlToImage", urlToImage + "")
                        editor.putString("author", author + "")
                        editor.putString("publishedAt", publishedAt + "")
                        editor.putString("onLoad_System", "onLoad_System")


                        //Toast.makeText(applicationContext, "Courtliness called", Toast.LENGTH_SHORT).show()
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val notificationManager: NotificationManager =
                            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                        val notificationID = Random.nextInt()

                        val getStae_App = shared_time_count.getString("STATE_APP", "")

                        val importance: Int

                        if (getStae_App.equals("BACKGROUND")) {
                            importance = NotificationManager.IMPORTANCE_HIGH
                        } else {
                            importance = NotificationManager.IMPORTANCE_DEFAULT
                        }


                        val channel = NotificationChannel(
                            ChannelId,
                            "News$notificationID",
                            importance
                        ).apply {
                            enableLights(true)
                            lightColor = Color.GREEN
                        }

                        notificationManager.createNotificationChannel(channel)

                        notificationManager.notify(notificationID, builder.build())

                        //   val editor = shared_time_count.edit()
                        editor.putInt("NotifyID", notificationID)
                        editor.apply()


                    }


                }


            } catch (e: Exception) {
                Log.e("NotificationWorker", "API Request Failed: ${e.message}")
            }
        }
    }


}