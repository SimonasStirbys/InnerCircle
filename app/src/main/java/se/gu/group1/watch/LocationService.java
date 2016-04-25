package se.gu.group1.watch;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }
    IBinder mBinder = new LocalBinder();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    ResultReceiver resultReceiver;
    Bundle bundle = new Bundle();
    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultReceiver = intent.getParcelableExtra("receiver");

        mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnected(Bundle bundle) {

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            handleNewLocation(location);
        }
    }
    private void handleNewLocation(Location location) {
        Log.d("Location in service", location.toString());
        final double currentLatitude = location.getLatitude();
        final double currentLongitude = location.getLongitude();
        sendCoordinates(currentLatitude,currentLongitude);



    }

    private void sendCoordinates(double x,double y) {
        double[] arr=coordinateConverter(x,y);
        ArrayList<String> locArr=new ArrayList<>();
        locArr.add(String.valueOf(arr[0]));
        locArr.add(String.valueOf(arr[1]));
        bundle.putStringArrayList("Location", locArr);
        resultReceiver.send(100, bundle);
    }

    public double[] coordinateConverter(double latitude, double longitude) {
        double[] worldCoordinates = new double[2];
        double siny = Math.sin(latitude * Math.PI / 180);

        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);

        double worldCoordinateX = 256 * (0.5 + longitude / 360);
        double worldCoordinateY = 256 * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI));
        Log.i("LATITUDE", String.valueOf(worldCoordinateX));
        Log.i("WORLD COORDINATE Y", String.valueOf(worldCoordinateY));
        worldCoordinates[0] = worldCoordinateX;
        worldCoordinates[1] = worldCoordinateY;

        return worldCoordinates;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location)  {

        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
