package se.gu.group1.watch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MultipleResults extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_results);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Watch");
        setSupportActionBar(toolbar);
    }
}
