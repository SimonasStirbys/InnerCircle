package se.gu.group1.watch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Simonas on 10/04/2016.
 */
public class SettingsActivity extends Activity{
    String[] activeContactList = new String[]{"Alice", "Bob", "Cyril", "David", "Ellen", "Fred", "Garry"};
    ArrayList<String> activeSelectedContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        //Set up the spinner for user's own radius limitation
        Spinner spinner = (Spinner) findViewById(R.id.limited_detection_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.radius_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        //Set up the spinner for actively followed contacts' range
        Spinner spinner2 = (Spinner) findViewById(R.id.active_contact_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter2 = ArrayAdapter.createFromResource(this,
                R.array.radius_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(spinnerAdapter2);


        //Sets up the Contact List__________________________________________________________________
        ArrayAdapter<String> contactListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, activeContactList);
        final ListView listView = (ListView) findViewById(R.id.active_contact_list);
        listView.setAdapter(contactListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = listView.getItemAtPosition(position).toString();
                if (activeSelectedContacts.contains(selection)) {
                    for (int i = 0; i <= activeSelectedContacts.size(); i++) {
                        if (activeSelectedContacts.get(i).toString().equals(selection)) {
                            activeSelectedContacts.remove(i);
                            view.setBackgroundColor(Color.WHITE);
                        }
                    }
                } else {
                    activeSelectedContacts.add(selection);
                    view.setBackgroundColor(Color.CYAN);
                }
            }
        });
        //__________________________________________________________________________________________




    }



    public void applyChanges(View view){



        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }



}