package se.gu.group1.watch;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by Simonas on 18/04/2016.
 */
class MyResult extends ResultReceiver {
    static String x ="0";
    static String y ="0";

    public boolean isDone() {
        return isDone;
    }

    boolean isDone;
    public MyResult(Handler handler) {
        super(handler);
    }


    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {


        if(resultCode == 100) {
            x = resultData.getStringArrayList("Location").get(0);
            y = resultData.getStringArrayList("Location").get(1);
            isDone = true;
            makePrecsion();
        }
        Log.d("CHECK COORDINATE", x);
        Log.d("CHECK COORDINATE", y);

    }
    public int[] makePrecsion(){
        int xA = (int) (Double.valueOf(x)*10000000);
        int yA = (int) (Double.valueOf(y)*10000000);

        return new int[]{xA,yA};
    }
}