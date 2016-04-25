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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private Toolbar toolbar;

    private LinearLayoutManager mLayoutManager;

    ArrayList<String> contactList = new ArrayList<String>();
    ArrayList<String> selectedContacts = new ArrayList<String>();
    ArrayList<Holder> itemList = new ArrayList<Holder>();

    CustomListAdapter listAdapter;
    ListView listView;
    AliceRequest alice;
    ElgamalCrypto crypto;
    PublicKey pk;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    String json;
    int radius; // radius selected
    ArrayList<String> recp = new ArrayList<>();//recp Id
    CipherText[] cred=new CipherText[3];
    int xA=0;//Alice x-coordinate
    int yA=0;//Alice y-coordinate
    static PublicKey Pk;//public key
    String keys;
    SendData data;
    MyResult resultReceiver = new MyResult(null);
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prefs = getSharedPreferences("UserCred",
                Context.MODE_PRIVATE);

        crypto = new ElgamalCrypto();
        Pk = new PublicKey(crypto.getP(), crypto.getG(), crypto.getY());
        alice=new AliceRequest();


        data=new SendData(prefs,Pk);
        //Requesting permission to use user's location.
        //this is necessary since android API 23.
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

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
            xA=resultReceiver.makePrecsion()[0];
            yA=resultReceiver.makePrecsion()[1];
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





                alice.generateEncryptedLocation(crypto,Pk,cred,xA,yA);//generate keys

               // Log.d("JsonString", parseLocReqBeforeSend(selectedContacts, radius, storeKeys()));// print the result
                //  editor.putString("JSONString", parseLocReqBeforeSend(new int[]{123,456,789},500));
                data.execute(parseLocReqBeforeSend(selectedContacts, radius,alice.makeJsonObject(crypto,cred)));//send the Request JsonObject to server

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



    public String parseLocReqBeforeSend(ArrayList<String> recpName, int radius, String keys) {
        Log.d("length"," "+recpName.size());
        JSONObject jsonF = new JSONObject();
        JSONObject jsonReq = null;
        String[] names=new String[recpName.size()];
        names=recpName.toArray(names);

        try {
            jsonReq = new JSONObject(keys);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObj = new JSONObject();

        try {
            jsonReq.put("Sender_ID", "Bob");// Alice ID
            jsonF.put("Recepient_ID", new JSONArray(names)); // Arrays of recp ID
            jsonReq.put("Radius", radius);//radius

            jsonF.put("Cred", jsonReq);// add all the keys in the message
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObj.put("Requests", jsonF);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj.toString();
    }





}

class SendData extends AsyncTask<String,Void,Void>{ // responsible for sending data to server

    Client client=new Client("54.191.125.60", 5050);
    private SharedPreferences prefs;
    private PublicKey pk;

    public SendData(){

    }
    public SendData(SharedPreferences prefs, PublicKey pk) {

        this.prefs = prefs;
        this.pk = pk;
    }

    @Override
    protected Void doInBackground(String... params) {
        client.connect();
        client.sendDataToServer(params[0]);
//        try {
//            client.receiveData(prefs,pk);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        client.disconect();
        return null;
    }


}
