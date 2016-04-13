package se.gu.group1.watch;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Simonas on 07/04/2016.
 * This class handles obtaining a regsitration token for the android application
 */
public class RegistrationIntentService extends IntentService {
    public boolean tokenSentToServer = false;


    public RegistrationIntentService(String name) {
        super(name);
    }


    @Override
    public void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
