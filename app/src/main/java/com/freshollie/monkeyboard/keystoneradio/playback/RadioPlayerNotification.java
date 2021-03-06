/*
 * Created by Oliver Bell on 15/01/2017
 * Copyright (c) 2017. by Oliver bell <freshollie@gmail.com>
 *
 * Last modified 14/06/17 23:15
 */

package com.freshollie.monkeyboard.keystoneradio.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import java.text.DecimalFormat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.freshollie.monkeyboard.keystoneradio.radio.RadioDevice;
import com.freshollie.monkeyboard.keystoneradio.ui.PlayerActivity;
import com.freshollie.monkeyboard.keystoneradio.R;

/**
 * Handles the player service notification, will update using the players metadata when
 * requested
 */
public class RadioPlayerNotification {
    private RadioPlayerService playerService;
    private NotificationCompat.Builder mediaNotificationBuilder;
    private NotificationManager notificationManager;

    private boolean wasPlaying = false;
    private int lastFrequency;

    private int NOTIFICATION_ID = 1;

    public RadioPlayerNotification(RadioPlayerService service) {
        playerService = service;
        notificationManager = (NotificationManager)
                playerService.getSystemService(Context.NOTIFICATION_SERVICE);


        Log.d("RadioPlayerNotification", "Starting foreground");
        playerService.getMediaSession().setActive(true);
        update();
    }

    private Notification buildNotification() {
        int playIcon;
        String playAction;
        String playDescription;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(playerService);

        // Used for setting a pause of play icon and action depending on the current play state
        if (playerService.getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
            playIcon = R.drawable.ic_notification_pause;
            playAction = RadioPlayerService.ACTION_PAUSE;
            playDescription = "pause";
            builder.setOngoing(true);
        } else {
            playIcon = R.drawable.ic_notification_play;
            playAction = RadioPlayerService.ACTION_PLAY;
            playDescription = "play";
        }

        if (playerService.getRadioMode() == RadioDevice.Values.STREAM_MODE_FM) {

            String trackName = playerService.getMetadata()
                    .getString(MediaMetadataCompat.METADATA_KEY_TITLE);

            if (trackName == null || trackName.isEmpty()) {
                trackName = new DecimalFormat("#.0").format(
                                    playerService.getMetadata().getLong(
                                            MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER
                                    ) / 1000.0
                );
            }

            return builder.setShowWhen(false)
                    .setLargeIcon(
                            ((BitmapDrawable) ResourcesCompat.getDrawableForDensity(
                                    playerService.getResources(),
                                    R.mipmap.ic_launcher,
                                    480,
                                    null
                            )
                            ).getBitmap()
                    )
                    .setSmallIcon(R.drawable.ic_notification_radio)
                    .setColor(ContextCompat.getColor(playerService, R.color.colorPrimaryDark))
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    playerService,
                                    0,
                                    new Intent(playerService, PlayerActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                    )
                    .setStyle(
                            new NotificationCompat.MediaStyle()
                                    .setMediaSession(
                                            playerService.getMediaSession()
                                                    .getSessionToken()
                                    )
                                    .setShowCancelButton(true)
                                    .setShowActionsInCompactView(0, 2, 4)
                                    .setCancelButtonIntent(
                                            getPendingIntentForAction(
                                                    RadioPlayerService.ACTION_STOP
                                            )
                                    )
                    )
                    .setContentTitle(trackName)
                    .setContentText(
                            playerService.getMetadata().getString(
                                    MediaMetadataCompat.METADATA_KEY_GENRE
                            )
                    )
                    .addAction(
                            R.drawable.ic_notification_skip_prev,
                            "prev",
                            getPendingIntentForAction(RadioPlayerService.ACTION_PREVIOUS)
                    )
                    .addAction(
                            R.drawable.ic_fast_rewind_white_24dp,
                            "search_backwards",
                            getPendingIntentForAction(RadioPlayerService.ACTION_SEARCH_BACKWARDS)
                    )
                    .addAction(
                            playIcon,
                            playDescription,
                            getPendingIntentForAction(playAction)
                    )
                    .addAction(
                            R.drawable.ic_fast_forward_white_24dp,
                            "search_forwards",
                            getPendingIntentForAction(RadioPlayerService.ACTION_SEARCH_FORWARDS)
                    )
                    .addAction(
                            R.drawable.ic_notification_skip_next,
                            "next",
                            getPendingIntentForAction(RadioPlayerService.ACTION_NEXT)
                    )
                    .setDeleteIntent(
                            getPendingIntentForAction(
                                    RadioPlayerService.ACTION_STOP
                            )
                    )
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();
        } else {
            return builder.setShowWhen(false)
                    .setLargeIcon(
                            ((BitmapDrawable) ResourcesCompat.getDrawableForDensity(
                                    playerService.getResources(),
                                    R.mipmap.ic_launcher,
                                    480,
                                    null
                            )
                            ).getBitmap()
                    )
                    .setSmallIcon(R.drawable.ic_notification_radio)
                    .setColor(ContextCompat.getColor(playerService, R.color.colorPrimaryDark))
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    playerService,
                                    0,
                                    new Intent(playerService, PlayerActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                    )
                    .setStyle(
                            new NotificationCompat.MediaStyle()
                                    .setMediaSession(
                                            playerService.getMediaSession()
                                                    .getSessionToken()
                                    )
                                    .setShowCancelButton(true)
                                    .setShowActionsInCompactView(0, 1, 2)
                                    .setCancelButtonIntent(
                                            getPendingIntentForAction(
                                                    RadioPlayerService.ACTION_STOP
                                            )
                                    )
                    )
                    .setContentTitle(playerService.getMetadata()
                            .getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                    )
                    .setContentText(
                            playerService.getMetadata().getString(
                                    MediaMetadataCompat.METADATA_KEY_GENRE
                            )
                    )
                    .addAction(
                            R.drawable.ic_notification_skip_prev,
                            "prev",
                            getPendingIntentForAction(RadioPlayerService.ACTION_PREVIOUS)
                    )
                    .addAction(
                            playIcon,
                            playDescription,
                            getPendingIntentForAction(playAction)
                    )
                    .addAction(
                            R.drawable.ic_notification_skip_next,
                            "next",
                            getPendingIntentForAction(RadioPlayerService.ACTION_NEXT)
                    )
                    .setDeleteIntent(
                            getPendingIntentForAction(
                                    RadioPlayerService.ACTION_STOP
                            )
                    )
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();
        }
    }

    private PendingIntent getPendingIntentForAction(String action) {
        return PendingIntent.getService(
                playerService,
                0,
                new Intent(playerService, RadioPlayerService.class)
                        .setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    public void update() {
        if (playerService.getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
            playerService.startForeground(
                    NOTIFICATION_ID,
                    buildNotification()
            );
        } else {
            playerService.stopForeground(false);
            notificationManager.notify(
                    NOTIFICATION_ID,
                    buildNotification()
            );
        }
    }

    public void cancel() {
        playerService.getMediaSession().setActive(false);
        notificationManager.cancel(NOTIFICATION_ID);
        playerService.stopForeground(true);
    }
}
