package com.iamarnob.austnotice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Arnob on 1/26/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String noticeUrl,noticeTitle;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        noticeTitle = remoteMessage.getData().get("message");
        noticeUrl = "http://aust.edu/" + remoteMessage.getData().get("notice_url");
        showNotification(remoteMessage.getData().get("message"),noticeTitle,noticeUrl);

    }

    private void showNotification(String message, String title, String url) {
        Intent intent = new Intent(this,NoticeDetailsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link",url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT,url);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,title);

        Intent externalIntent = new Intent();
        externalIntent.setAction(Intent.ACTION_VIEW);
        externalIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        externalIntent.setData(Uri.parse(url));

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent sharingPendingIntent = PendingIntent.getActivity(this,1,sharingIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent externalPendingIntent = PendingIntent.getActivity(this,2,externalIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action shareAction = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_share, "SHARE", sharingPendingIntent).build();
        NotificationCompat.Action externalAction = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_upload, "OPEN IN BROWSER", externalPendingIntent).build();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("You Have a New Notice!")
                .addAction(externalAction)
                .addAction(shareAction)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setVibrate(new long[] { 1000, 500, 300, 500, 300 })
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }
}
