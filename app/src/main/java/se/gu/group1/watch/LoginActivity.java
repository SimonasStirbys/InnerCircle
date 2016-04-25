package se.gu.group1.watch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
//        crypto.initializeSumOfSquares();
    }

    public void loginUser(View view) {
        EditText usernameText = (EditText) findViewById(R.id.name_signIn);
        EditText passwordText = (EditText) findViewById(R.id.password_signIn);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        String[] usernamePassword = new String[] {username, password};

        if(checkUserExistance(usernamePassword)==true) {
            SharedPreferences prefs = getSharedPreferences("UserCred",
                    Context.MODE_PRIVATE);


                storeSecretKey();

            Intent register=new Intent(this,RegisterDeviceGCM.class);
            register.putExtra("Name",usernameText.getText().toString());
            startService(register);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            //do nothing
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


    //Use this method for checking if user credentials are legitimate
    //Mainly exists for the possiiility of an added database in the future
    protected boolean checkUserExistance(String[] usernamePassword){
        String[][] users = new String[][] {{"Simonas", "1234"}, {"Cyril", "4321"}, {"Bob", "1234"}};

        for(int i = 0; i<users.length; i++){
            if(users[i][0].equals(usernamePassword[0])&&users[i][1].equals(usernamePassword[1])){
                return true;
            }
        }

        return false;
    }
}

