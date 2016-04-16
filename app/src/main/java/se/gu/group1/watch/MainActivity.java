package se.gu.group1.watch;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity  {

    private Toolbar toolbar;
  /*  SearchView searchView;
    MenuItem myActionMenuItem;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;*/
    ElgamalCrypto crypto;
    PublicKey pk;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    String json;
    int radius=500; // radius selected
    int[]recp={456};//recp Id
    CipherText[] cred=new CipherText[3];
    int xA=0;//Alice x-coordinate
    int yA=0;//Alice y-coordinate
    PublicKey Pk;//public key
    String keys;
    SendData data=new SendData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Watch");
        setSupportActionBar(toolbar);
        crypto=new ElgamalCrypto();
        Pk = new PublicKey(crypto.getP(), crypto.getG(), crypto.getY());
        SecretKey secretK = new SecretKey(crypto.getSecretKey());


        settings = this.getSharedPreferences("Cred",
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear().commit();//remove previous shared preferences(no need to have if keys are the same)
        keys = settings.getString("credentials", null);
        if(json!=null){
            data.execute(parseLocReqBeforeSend(recp, radius, keys)); // aray of recp Id , 500 is radius, keys ( data.execute() wil send the JsonObject to server)
        }else{//if sharedPrefecnces is empty
            generateEncryptedLocation(Pk, xA, yA); //generate keys
            editor.putString("credentials", storeKeys());//store keys
           Log.d("JsonString", parseLocReqBeforeSend(recp, radius, storeKeys()));// print the result
          //  editor.putString("JSONString", parseLocReqBeforeSend(new int[]{123,456,789},500));
            data.execute(parseLocReqBeforeSend(recp, radius, storeKeys()));//send the Request JsonObject to server
            editor.commit();// submit changes to sharedPreferences
        }


    }

    private String storeKeys() {
        JSONObject jsonReq=new JSONObject();
        try {
            jsonReq.put("A0.C0", cred[0].C0.toString());
            jsonReq.put("A0.C1", cred[0].C1.toString());
            jsonReq.put("A1.C0", cred[1].C0.toString());
            jsonReq.put("A1.C1", cred[1].C1.toString());
            jsonReq.put("A2.C0", cred[2].C0.toString());
            jsonReq.put("A2.C1", cred[2].C1.toString());
            jsonReq.put("P", crypto.getP().toString());
            jsonReq.put("G", crypto.getG().toString());
            jsonReq.put("Y", crypto.getY().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonReq.toString();
    }

    public String parseLocReqBeforeSend(int[] recpName,int radius,String keys) {

        JSONObject jsonF = new JSONObject();
        JSONObject jsonReq=null;
        try {
            jsonReq = new JSONObject(keys);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObj = new JSONObject();

        try {
            jsonReq.put("Sender_ID", 123);// Alice ID
            jsonF.put("Recepient_ID", new JSONArray(recpName)); // Arrays of recp ID
            jsonReq.put("Radius", radius);//radius

            jsonF.put("Cred",jsonReq);// add all the keys in the message
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            jsonObj.put("Requests", jsonF);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();


    }

    public  void generateEncryptedLocation(PublicKey Pk,int xA,int yA){// publickey ,Alice  x-coordinate, Alice y-coordinate

        CipherText a0 = crypto.encryption(Pk, (int) Math.pow(xA, 2) + (int) Math.pow(yA, 2));
        CipherText a1 = crypto.encryption(Pk, 2 * xA);
        CipherText a2 = crypto.encryption(Pk, 2 * yA);
        cred[0]=a0;
        cred[1]=a1;
        cred[2]=a2;

    }



}

class SendData extends AsyncTask<String,Void,Void>{ // responsible for sending data to server
    Client client=new Client("54.191.125.60", 5050);
    @Override
    protected Void doInBackground(String... params) {
        client.connect();
        client.sendDataToServer(params[0]);
        return null;
    }
}
