package se.gu.group1.watch;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private ElgamalCrypto crypto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        crypto=new ElgamalCrypto();

       // crypto.initializeSumOfSquares();

    }

    public void loginUser(View view) {
        final EditText usernameText = (EditText) findViewById(R.id.name_signIn);
        final EditText passwordText = (EditText) findViewById(R.id.password_signIn);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        String[] usernamePassword = new String[] {username, password};

        if(checkUserExistance(usernamePassword)==true) {
            String userName = usernameText.getText().toString();
            Intent register=new Intent(this,RegisterDeviceGCM.class);
            register.putExtra("Name",userName);
            startService(register);
            Intent mainPage = new Intent(this, MainActivity.class);
            mainPage.putExtra("Name",userName);
            startActivity(mainPage);



        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Incorrect Username and/or Password.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            usernameText.setText("");
                            passwordText.setText("");
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }



    //Use this method for checking if user credentials are legitimate
    protected boolean checkUserExistance(String[] usernamePassword){
        String[][] users = new String[][] {{"Alice", "1234"}, {"Bob", "1234"}, {"Cyril", "1234"}, {"David", "1234"}};

        for(int i = 0; i<users.length; i++){
            if(users[i][0].equals(usernamePassword[0])&&users[i][1].equals(usernamePassword[1])){
                return true;
            }
        }

        return false;
    }
}

