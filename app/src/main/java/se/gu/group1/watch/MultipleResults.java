package se.gu.group1.watch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;

public class MultipleResults extends AppCompatActivity {
    private Toolbar toolbar;
   static ArrayAdapter<String> resultsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_results);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Watch");
        setSupportActionBar(toolbar);
        resultsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.resultsArray);
        GridView resultsView = (GridView) findViewById(R.id.resultsView);
        resultsView.setAdapter(resultsAdapter);
    runThread();
        //display selected contacts and range


    }

    private void runThread() {

        new Thread() {
            public void run() {
                int i=0;
                while (i++ < 1000) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                resultsAdapter.notifyDataSetChanged();
                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    public void returnToMain(View view){

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

}
