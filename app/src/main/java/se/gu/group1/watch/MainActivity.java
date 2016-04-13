package se.gu.group1.watch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    SearchView searchView;
    MenuItem myActionMenuItem;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    String[] contactList = new String[]{"Alice", "Bob", "Cyril", "David", "Ellen", "Fred", "Garry"};
    ArrayList<String> selectedContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Watch");
        setSupportActionBar(toolbar);

        //mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //mRecyclerView.setAdapter(mAdapter);

        servicesConnected(this);


        //Sets up the spinner to show range options
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.radius_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        //Sets up the Contact List__________________________________________________________________
        ArrayAdapter<String> contactListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, contactList);
        final ListView listView = (ListView) findViewById(R.id.contact_list);
        listView.setAdapter(contactListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = listView.getItemAtPosition(position).toString();
                if(selectedContacts.contains(selection)){
                    for(int i = 0; i<=selectedContacts.size(); i++){
                        if (selectedContacts.get(i).toString().equals(selection)){
                            selectedContacts.remove(i);
                            view.setBackgroundColor(Color.WHITE);
                        }
                    }
                }else{
                    selectedContacts.add(selection);
                    view.setBackgroundColor(Color.CYAN);
                }

                Log.d("CONTAINS", "____________");
                for (int i = 0; i<=selectedContacts.size(); i++){
                    Log.d("CONTAINS", selectedContacts.get(i).toString());
                }
                Log.d("CONTAINS", "____________");

            }
        });
        //__________________________________________________________________________________________


        //Set up the map
//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);




        //Intent i = new Intent(getApplicationContext(), RegistrationIntentService.class);
        //startService(i);

        Intent i = new Intent(getApplicationContext(), LocationActivity.class);
        startService(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, R.id.app_settings, Menu.NONE, R.string.settings_title);

        return true;
    }

    public void locate(View view) {
        if(selectedContacts.size()==0){
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

        }else {
            //TO DO: implement handling for no contacts selected

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            int radius = 0;

            //        radius = Integer.parseInt(spinner.getSelectedItem().toString());
            //        Intent i = new Intent(getApplicationContext(), LocationActivity.class);
            //        i.putExtra("radius", radius);
            //        i.putExtra("selected_contacts", selectedContacts);
            //        startService(i);


            radius = Integer.parseInt(spinner.getSelectedItem().toString());
            Intent i = new Intent(getApplicationContext(), MultipleResults.class);
            i.putExtra("radius", radius);
            i.putExtra("selected_contacts", selectedContacts);
            startActivity(i);
        }
    }


    public void selectAll(View view){

        ArrayAdapter<String> contactListAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, contactList);
        final ListView listView = (ListView) findViewById(R.id.contact_list);
        listView.setAdapter(contactListAdapter2);
        listView.setActivated(true);


        Button p1_button = (Button)findViewById(R.id.select_all);

        if (selectedContacts.size()!=contactList.length){
            selectedContacts.clear();
            for (int i=0; i<contactList.length; i++){
                selectedContacts.add(i, contactList[i]);
                final int n = i;

                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.getChildAt(n).setBackgroundColor(Color.CYAN);
                    }
                });
            }
            p1_button.setText("Deselect All");
        }else{
            selectedContacts.clear();

            for (int i=0; i<contactList.length; i++){
                final int n = 0;
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.getChildAt(n).setBackgroundColor(Color.WHITE);
                    }
                });
            }
            p1_button.setText("Select All");
        }
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
            case R.id.app_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }





}
