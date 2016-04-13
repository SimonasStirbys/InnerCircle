package se.gu.group1.watch;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class LocationActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MapsActivity.class.getSimpleName();

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }


    private void handleNewLocation(Location location) {
        Bundle extras = getIntent().getExtras();

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double[] worldCoordinates = new double[2];

        double a0;
        double a1;
        double a2;
        double[] plainText = new double [3];

        int radius;
        ArrayList<String> selectedContacts = new ArrayList<String>(0);


        Log.i("LATITUDE", String.valueOf(latitude));
        Log.i("LONGITUDE", String.valueOf(longitude));

        worldCoordinates = coordinateConverter(latitude, longitude);

        a0 = (worldCoordinates[0]*worldCoordinates[0])+ (worldCoordinates[1]*worldCoordinates[1]);
        a1 = 2*worldCoordinates[0];
        a2 = 2*worldCoordinates[1];

        plainText[0] = a0;
        plainText[1] = a1;
        plainText[2] = a2;

        radius = extras.getInt("radius");
        Log.d("RADIUS", String.valueOf(radius));

        selectedContacts = extras.getStringArrayList("selected_contacts");
        for (int i = 0; i<selectedContacts.size(); i++){
            Log.d("CONTACTS", selectedContacts.get(i).toString());
        }

        //pass data to another activity
        Intent i = new Intent(getApplicationContext(), MultipleResults.class);
        i.putExtra("plain_text", plainText);
        i.putExtra("result_contacts", selectedContacts);
        startActivity(i);

    }


    // The mapping between latitude, longitude and pixels is defined by the web mercator projection.
    // Credit to: https://developers.google.com/maps/documentation/javascript/examples/map-coordinates?csw=1
    public double[] coordinateConverter(double latitude, double longitude) {
        double[] worldCoordinates = new double[2];
        double siny = Math.sin(latitude * Math.PI / 180);

        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);

        double worldCoordinateX = 256 * (0.5 + longitude / 360);
        double worldCoordinateY = 256 * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI));
        Log.i("WORLD COORDINATE X", String.valueOf(worldCoordinateX));
        Log.i("WORLD COORDINATE Y", String.valueOf(worldCoordinateY));
        worldCoordinates[0] = worldCoordinateX;
        worldCoordinates[1] = worldCoordinateY;

        return worldCoordinates;

    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

}
