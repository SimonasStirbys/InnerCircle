package se.gu.group1.watch;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private Toolbar toolbar;

    private LinearLayoutManager mLayoutManager;

    ArrayList<String> contactList = new ArrayList<String>();
    ArrayList<String> selectedContacts = new ArrayList<String>();
    ArrayList<Holder> itemList = new ArrayList<Holder>();
    static  ArrayList<String> resultsArray = new ArrayList<>();
    CustomListAdapter listAdapter;
    ListView listView;
    AliceRequest alice;
    ElgamalCrypto crypto;
    ArrayList<String> recp = new ArrayList<>();//recp Id
    CipherText[] cred=new CipherText[3];
    int xA=0;//Alice x-coordinate
    int yA=0;//Alice y-coordinate
    static PublicKey Pk;//public key
    String keys;
    //SendData data;
    static MyResult resultReceiver = new MyResult(null);
    SharedPreferences prefs;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prefs = getSharedPreferences("UserCred",
                Context.MODE_PRIVATE);

        crypto = new ElgamalCrypto();
        Pk = new PublicKey(crypto.getP(), crypto.getG(), crypto.getY());
        alice=new AliceRequest();
        storeSecretKey();

        
        //Requesting permission to use user's location.
        //this is necessary since android API 23.
        //int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        Intent alarm = new Intent(this, LocationService.class);
        alarm.putExtra("receiver", resultReceiver);
        startService(alarm);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        username=prefs.getString("Username", "");
        contactList.add("Alice");
        contactList.add("Bob");
        contactList.add("Cyril");
        contactList.add("David");
        contactList.add("Ellen");
        contactList.add("Fred");
        contactList.add("Garry");
        contactList.add("Henry");
        contactList.add("Igor");
        contactList.add("John");
        contactList.add("Katherine");
        contactList.add("Louise");
        contactList.add("Marcus");
        contactList.remove(username);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Watch");
        setSupportActionBar(toolbar);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        servicesConnected(this);


        //Sets up the spinner to show range options_________________________________________________
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.radius_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        //Sets up the Contact List__________________________________________________________________
        for (int i = 0; i < contactList.size(); i++) {
            Holder itemHolder = new Holder();
            itemHolder.setTitle(contactList.get(i));
            itemHolder.setColor(Color.WHITE);
            itemList.add(itemHolder);
        }

        listAdapter = new CustomListAdapter(this, itemList);
        listView = (ListView) findViewById(R.id.contact_list);
        listView.setAdapter(listAdapter);

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        toolbar.setTitle("Watch");
//        setSupportActionBar(toolbar);
//        crypto=new ElgamalCrypto();
//        Pk = new PublicKey(crypto.getP(), crypto.getG(), crypto.getY());
//        SecretKey secretK = new SecretKey(crypto.getSecretKey());
//
//
//        settings = this.getSharedPreferences("Cred",
//                Context.MODE_PRIVATE);
//        editor = settings.edit();
//        editor.clear().commit();//remove previous shared preferences(no need to have if keys are the same)
//        keys = settings.getString("credentials", null);
//        if(json!=null){
//            data.execute(parseLocReqBeforeSend(recp, radius, keys)); // aray of recp Id , 500 is radius, keys ( data.execute() wil send the JsonObject to server)
//        }else{//if sharedPrefecnces is empty
//            generateEncryptedLocation(Pk, xA, yA); //generate keys
//            editor.putString("credentials", storeKeys());//store keys
//           Log.d("JsonString", parseLocReqBeforeSend(recp, radius, storeKeys()));// print the result
//          //  editor.putString("JSONString", parseLocReqBeforeSend(new int[]{123,456,789},500));
//            data.execute(parseLocReqBeforeSend(recp, radius, storeKeys()));//send the Request JsonObject to server
//            editor.commit();// submit changes to sharedPreferences
        }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Intent alarm = new Intent(this, LocationService.class);
                    alarm.putExtra("receiver", resultReceiver);
                    startService(alarm);

                } else {
                    // permission denied.
                }
                return;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, R.id.app_settings, Menu.NONE, R.string.settings_title);
        menu.add(Menu.NONE, R.id.map_tab, Menu.NONE, R.string.map_title);

        return true;
    }
    public void locate(View view) {
        SendData data = new SendData(prefs, Pk, getApplicationContext());

        resultsArray.clear();
        selectedContacts.clear();
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getColor() == Color.CYAN) {
                selectedContacts.add(itemList.get(i).getTitle());
            }
        }

        if (selectedContacts.size() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Please select at least one contact before locating their proximity.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else {
            Log.d("numberOfContacts", ""+selectedContacts.size());
            xA=resultReceiver.makePrecsion()[0];
            yA=resultReceiver.makePrecsion()[1];
            Log.d("Coordinate xA", ""+xA);
            Log.d("Coordinate yA", ""+yA);
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            int radius = Integer.parseInt(spinner.getSelectedItem().toString());

//            radius = Integer.parseInt(spinner.getSelectedItem().toString());
//            Intent n = new Intent(getApplicationContext(), LocationFetcher.class);
//            n.putExtra("radius", radius);
//            n.putExtra("selected_contacts", selectedContacts);
//            startActivity(n);

//            radius = Integer.parseInt(spinner.getSelectedItem().toString());
//            Intent i = new Intent(getApplicationContext(), MultipleResults.class);
//            i.putExtra("radius", radius);
//            i.putExtra("selected_contacts", selectedContacts);
//            startActivity(i);


//            Set<String> converterArray = new Set
            String[] contacts=new String[selectedContacts.size()];
            contacts=selectedContacts.toArray(contacts);
            String contactsArray= Arrays.toString(contacts);
            Log.d("Array",contactsArray);
            storeString(contactsArray);


            storeContactNumber(selectedContacts.size());
                alice.generateEncryptedLocation(crypto,Pk,cred,xA,yA);//generate keys

               // Log.d("JsonString", parseLocReqBeforeSend(selectedContacts, radius, storeKeys()));// print the result
                //  editor.putString("JSONString", parseLocReqBeforeSend(new int[]{123,456,789},500));
                data.execute(alice.makeJsonObject(crypto, cred,radius,selectedContacts, username));//send the Request JsonObject to server
            for(int i=0; i<selectedContacts.size(); i++){
                resultsArray.add(selectedContacts.get(i));
                resultsArray.add("Hello");
            }
            Intent resultsPage = new Intent(this, MultipleResults.class);
            resultsPage.putExtra("results_array", resultsArray);
            startActivity(resultsPage);
        }

    }
    public void selectAll(View view) {
        itemList.clear();

        Button p1_button = (Button) findViewById(R.id.select_all);
        if (p1_button.getText().toString().equals("Select All")) {
            for (int i = 0; i < contactList.size(); i++) {
                Holder itemHolder = new Holder();
                itemHolder.setTitle(contactList.get(i));
                itemHolder.setColor(Color.CYAN);
                itemList.add(itemHolder);
            }
            p1_button.setText("Deselect All");

        } else {
            p1_button.setText("Select All");
            for (int i = 0; i < contactList.size(); i++) {
                Holder itemHolder = new Holder();
                itemHolder.setTitle(contactList.get(i));
                itemHolder.setColor(Color.WHITE);
                itemList.add(itemHolder);
            }
        }

        listAdapter = new CustomListAdapter(this, itemList);
        listView = (ListView) findViewById(R.id.contact_list);
        listView.setAdapter(listAdapter);
    }


    private boolean servicesConnected(Context context) {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("AS", "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            Log.i("AS", "Services connected - Error");
        }

        Log.i("AS", "Services connected - Error 2");
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_tab:
                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                int radius = Integer.parseInt(spinner.getSelectedItem().toString());
                Intent n = new Intent(getApplicationContext(), MapsActivity.class);
                n.putExtra("radius", radius);
                startActivity(n);
                return true;

            case R.id.app_settings:
                // User chose the "Settings" item, show the app settings UI
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void storeSecretKey() {
        String secret=crypto.getSecretKey().toString();
        SharedPreferences prefs = getSharedPreferences("UserCred",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Secret Key", secret);
        editor.commit();
        Log.d("Done"," Secret Key stored");
    }

    private void storeContactNumber(int number) {
        SharedPreferences prefs = getSharedPreferences("UserCred",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Size", number);
        editor.commit();
    }

    private void storeString(String string) {
        SharedPreferences prefs = getSharedPreferences("UserCred",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selected_contacts", string);
        editor.commit();
    }

}

class SendData extends AsyncTask<String,Void,Void>{


    Client client=new Client("54.191.125.60", 5050);
    private SharedPreferences prefs;
    private PublicKey pk;
    Context context;

    public SendData(){

    }
    public SendData(SharedPreferences prefs, PublicKey pk, Context context) {

        this.prefs = prefs;
        this.pk = pk;
        this.context = context;

    }

    @Override
    protected Void doInBackground(String... params) {
        client.connect();
        if(!params[0].equals("Answer")){
        client.sendDataToServer(params[0]);
        } else {
            try {
                client.receiveData(prefs, pk, context);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.disconect();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
