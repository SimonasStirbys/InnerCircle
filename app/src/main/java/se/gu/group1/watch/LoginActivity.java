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
    }

    public void loginUser(View view) {
        EditText usernameText = (EditText) findViewById(R.id.name_signIn);
        EditText passwordText = (EditText) findViewById(R.id.password_signIn);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        String[] usernamePassword = new String[] {username, password};

        if(checkUserExistance(usernamePassword)==true) {
            String userName = usernameText.getText().toString();
            storeUserName(userName);

            Intent register=new Intent(this,RegisterDeviceGCM.class);
            register.putExtra("Name",userName);
            startService(register);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            //do nothing
        }
    }


    //Use this method for checking if user credentials are legitimate
    //Mainly exists for the possiiility of an added database in the future
    protected boolean checkUserExistance(String[] usernamePassword){
        String[][] users = new String[][] {{"Alice", "1234"}, {"Bob", "1234"},{"Cyril", "1234"},{"David", "1234"}};

        for(int i = 0; i<users.length; i++){
            if(users[i][0].equals(usernamePassword[0])&&users[i][1].equals(usernamePassword[1])){
                return true;
            }
        }

        return false;
    }

    //Store the user
    private void storeUserName(String user) {
        SharedPreferences prefs = getSharedPreferences("UserCred",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Username", user);
        editor.commit();
    }
}

