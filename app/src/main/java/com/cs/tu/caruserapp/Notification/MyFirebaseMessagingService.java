package com.cs.tu.caruserapp.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.cs.tu.caruserapp.MainActivity;
import com.cs.tu.caruserapp.MessageActivity;
import com.cs.tu.caruserapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //--- Dont send noti while chatting --- get current chatting receiver id
            SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
            String currentUser = preferences.getString("currentuser", "none");

            //get sender ID from payload
            String sender_car_id = remoteMessage.getData().get("sender_car_id");
            String sender_car_province = remoteMessage.getData().get("sender_car_province");
            //get receiver ID from payload
            String receiver = remoteMessage.getData().get("receiver");

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if(firebaseUser != null && receiver.equals(firebaseUser.getUid())){
                //--- Dont send noti while chatting if sender of notification is who are you chatting with ---
                if(!currentUser.equals(sender_car_id + "_" + sender_car_province)){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        sendOreoNotification(remoteMessage);
                    }else {
                        sendNotification(remoteMessage);
                    }
                }
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotificationFirebase(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            storeNotiToDatabase(remoteMessage.getNotification().getBody());

        }

    }

    private void sendNotificationFirebase(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dont_show_dialog", true);
        intent.putExtra("open_noti", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_car_launcher)
                        .setContentTitle(messageTitle)
                        .setContentText(filterBadWords(messageBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        String sender_id = remoteMessage.getData().get("sender_id");

        //contrast data from remote Message
        String receiver_id = remoteMessage.getData().get("sender_id");
        String receiver_car_id = remoteMessage.getData().get("sender_car_id");
        String sender_car_id = remoteMessage.getData().get("receiver_car_id");
        String sender_car_province = remoteMessage.getData().get("receiver_car_province");
        String receiver_car_province = remoteMessage.getData().get("sender_car_province");

        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = filterBadWords(remoteMessage.getData().get("body"));

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        // "\\D" mean All character that is not numbers         replace all character with empty string
        int j = Integer.parseInt(sender_id.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("receiver_id", receiver_id);
        intent.putExtra("receiver_car_id", receiver_car_id);
        intent.putExtra("sender_car_id", sender_car_id);
        intent.putExtra("sender_car_province", sender_car_province);
        intent.putExtra("receiver_car_province", receiver_car_province);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NewNotification newNotification = new NewNotification(this);
        Notification.Builder builder = newNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;
        if(j > 0){
            i = j;
        }

        newNotification.getManager().notify(i, builder.build());

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String sender_id = remoteMessage.getData().get("sender_id");

        //contrast data from remote Message
        String receiver_id = remoteMessage.getData().get("sender_id");
        String receiver_car_id = remoteMessage.getData().get("sender_car_id");
        String sender_car_id = remoteMessage.getData().get("receiver_car_id");
        String sender_car_province = remoteMessage.getData().get("receiver_car_province");
        String receiver_car_province = remoteMessage.getData().get("sender_car_province");

        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(sender_id.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("receiver_id", receiver_id);
        intent.putExtra("receiver_car_id", receiver_car_id);
        intent.putExtra("sender_car_id", sender_car_id);
        intent.putExtra("sender_car_province", sender_car_province);
        intent.putExtra("receiver_car_province", receiver_car_province);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if(j > 0){
            i = j;
        }

        notificationManager.notify(i, builder.build());
    }

    private void storeNotiToDatabase(String messageBody){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //get current date
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        String formattedDate = df.format(date);
        //get current time
        String pattern = "HH:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        String currentTimeString = simpleDateFormat.format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imageURL", "default");
        hashMap.put("message", messageBody);
        hashMap.put("isseen", false);
        hashMap.put("date", formattedDate);
        hashMap.put("time", currentTimeString);

        reference.child("Notification").child(firebaseUser.getUid()).push().setValue(hashMap);

    }

    public String filterBadWords(String message) {
        String[] bad_words = getResources().getStringArray(R.array.bad_words);;
        final List<String> words = Arrays.asList(bad_words);;

        for(String word : words){
            Pattern rx = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
            message = rx.matcher(message).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
        }

//        String censor_word = message;
//        for (String word : words) {
//            Pattern rx = Pattern.compile(word, Pattern.CASE_INSENSITIVE);
//            message = rx.matcher(message).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
//
//            for(int i=0;i<censor_word.length();i++){
//                if(censor_word.charAt(i) != message.charAt(i)){
//                    censor_word = censor_word.replace(censor_word.charAt(i),'*');
//                }
//            }
//        }

        return message;
    }

}
