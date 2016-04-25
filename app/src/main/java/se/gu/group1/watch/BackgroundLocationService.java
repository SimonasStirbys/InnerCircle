package se.gu.group1.watch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Simonas on 18/04/2016.
 */
public class BackgroundLocationService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("start","Receiver started");
        Intent background = new Intent(context, LocationService.class);
        context.startService(background);
    }
}
