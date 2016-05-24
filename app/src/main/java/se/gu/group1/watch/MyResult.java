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
    static String latitude = "0";
    static String longitude = "0";

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
            x = resultData.getStringArrayList("world_coordinates").get(0);
            y = resultData.getStringArrayList("world_coordinates").get(1);
            latitude = resultData.getStringArrayList("degree_coordinates").get(0);
            longitude = resultData.getStringArrayList("degree_coordinates").get(1);
            isDone = true;
            makePrecsion();
        }
    }
    public int[] makePrecsion(){
        int xA = (int) (Double.valueOf(x)*10000);
        int yA = (int) (Double.valueOf(y)*10000);

        return new int[]{xA,yA};
    }

    public double[] getLatLng(){
        double lat = Double.valueOf(latitude);
        double lng = Double.valueOf(longitude);

        return new double[]{lat,lng};
    }


}