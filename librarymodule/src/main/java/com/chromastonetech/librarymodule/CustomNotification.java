package com.chromastonetech.librarymodule;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling custom
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class CustomNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Custom";
    private static String channelId = "101";

    private Context mContext;
    private String mTitle;
    private String mText;
    private int mNumber;
    private Bitmap picture;
    private boolean autoCancel;
    private String mChannelId;
    private NotificationCompat.Builder builder;

    public CustomNotification(@NonNull Context mContext, String mTitle, String mText, @Nullable int mNumber, Bitmap picture, boolean autoCancel, @NonNull String mChannelId) {
        this.mContext = mContext;
        this.mTitle = mTitle;
        this.mText = mText;
        this.mNumber = mNumber;
        this.picture = picture;
        this.autoCancel = autoCancel;
        this.mChannelId = mChannelId;
        initBuilder();
    }

    private void initBuilder(){
        builder = new NotificationCompat.Builder(mContext,mChannelId);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.drawable.ic_stat_notification_icon);
        builder.setPriority(Notification.DEFAULT_ALL);
        builder.setNumber(mNumber);
        setmTitle(mTitle);
        setmText(mText);
        builder.setAutoCancel(autoCancel);
    }

    public void showProgress(int progress){
        builder.setOngoing(true);
        builder.setProgress(100,progress,false);
        show();
    }

    public void hideProgress(){
        builder.setOngoing(false);
        builder.setProgress(0,0,false);
        show();
    }

    public CustomNotification setmTitle(String mTitle) {
        this.mTitle = mTitle;
        builder.setContentTitle(this.mTitle);
        return this;
    }

    public CustomNotification setmText(String mText) {
        this.mText = mText;
        builder.setContentTitle(this.mText);
        return this;
    }
    public void show(){
        notify(mContext,builder.build());
    }

    public static void notify(final Context context,
                              final String exampleString, final int number) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);


        final String ticker = exampleString;
        final String title = res.getString(
                R.string.custom_notification_title_template, exampleString);
        final String text = res.getString(
                R.string.custom_notification_placeholder_text_template, exampleString);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_stat_notification_icon)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(ticker)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)

                // If this notification relates to a past or upcoming event, you
                // should set the relevant time information using the setWhen
                // method below. If this call is omitted, the notification's
                // timestamp will by set to the time at which it was shown.
                // upcoming event. The sole argument to this method should be
                // the notification timestamp in milliseconds.
                //.setWhen(...)

                // Set the pending intent to be initiated when the user touches
                // the notification.
/*                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
                                PendingIntent.FLAG_UPDATE_CURRENT))*/

                // Example additional actions for this notification. These will
                // only show on devices running Android 4.1 or later, so you
                // should ensure that the activity in this notification's
                // content intent provides access to the same actions in
                // another way.
/*                .addAction(
                        R.drawable.ic_action_stat_share,
                        res.getString(R.string.action_share),
                        PendingIntent.getActivity(
                                context,
                                0,
                                Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                        .setType("text/plain")
                                        .putExtra(Intent.EXTRA_TEXT, "Dummy text"), "Dummy title"),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        R.drawable.ic_action_stat_reply,
                        res.getString(R.string.action_reply),
                        null)*/

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
