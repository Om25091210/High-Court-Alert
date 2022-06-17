package `in`.aryomtech.cgalert.fcm


import `in`.aryomtech.cgalert.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


private const val CHANNEL_ID="my_channel"

class FirebaseService : FirebaseMessagingService(){

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("newToken", p0)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent= Intent(this, temp_notification::class.java)
        intent.putExtra("sending_msg_data",""+message.data["key"])
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            createNotifionChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, notificationID, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, notificationID, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val contentView = RemoteViews(this.packageName, R.layout.notification_layout)
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
        contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent)
        contentView.setTextViewText(R.id.message, message.data["title"])
        contentView.setTextViewText(R.id.date, message.data["message"])

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
            .setSmallIcon(R.drawable.ic_cap)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_cap))
            .setAutoCancel(true)
            .setContent(contentView)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)

        //Initialize Database
        val reference = FirebaseDatabase.getInstance().reference.child("data")
        reference.child(""+message.data["key"]).child("sent").setValue("1")

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifionChannel(notificationManager: NotificationManager){
        val channelName="ChannelName"
        val channel=
            NotificationChannel(CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH).apply {

                description="My channel description"
                enableLights(true)
                lightColor= Color.GREEN
            }
        notificationManager.createNotificationChannel(channel)
    }
}