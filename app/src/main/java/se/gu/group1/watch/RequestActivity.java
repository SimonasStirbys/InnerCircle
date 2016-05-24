package se.gu.group1.watch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.GridView;

/**
 * Created by Simonas on 09/05/2016.
 */
public class RequestActivity extends Activity {
    static ArrayAdapter<String> requestsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        requestsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.requests);
        GridView resultsView = (GridView) findViewById(R.id.request_display);
        resultsView.setAdapter(requestsAdapter);
    }

}
