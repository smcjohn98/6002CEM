package com.smc.crafthk.implementation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CraftHKMessagingService extends FirebaseMessagingService {

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    @Override
    public void onNewToken(String token) {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

        }
        if (remoteMessage.getNotification() != null) {

        }
    }
}
