package se.gu.group1.watch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;

public class MultipleResults extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_results);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Watch");
        setSupportActionBar(toolbar);


        //get selected contacts
        Bundle extras = getIntent().getExtras();
        ArrayList<String> resultsArray = extras.getStringArrayList("result_contacts");


        //display selected contacts and range
        ArrayAdapter<String> resultsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, resultsArray);
        GridView resultsView = (GridView) findViewById(R.id.resultsView);
        resultsView.setAdapter(resultsAdapter);
    }

    public void returnToMain(View view){

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

}
