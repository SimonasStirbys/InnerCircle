package se.gu.group1.watch;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Omar on 4/15/2016.
 */
class Client  {

    String dstAddress;
    int dstPort;
    Socket socket = null;
    BufferedReader input;
    PrintWriter out;
    ArrayList<CipherText> encResults;
    LocationAproximity loc;
    // takes the Public Ip , port num
    Client(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
        loc=new LocationAproximity();

    }

// connects to server and initialize the input/output stream
    public void connect(){
        try {
            socket = new Socket(dstAddress, dstPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //send message to server and close the socket
    public int sendDataToServer(String msg) {

        Log.d("Msg to Server", msg);
       out.println(msg);

        return 1;
    }

    public int receiveData(SharedPreferences prefs, PublicKey pk) throws IOException, InterruptedException {
        String message;
        String nMessage;
        int x=0;
        while(x<30){
            sendDataToServer("{\"Check\":\"Bob\"}");
            Thread.sleep(8000);
            x++;
        }
        try {
            nMessage=input.readLine();
            message=nMessage;
            Log.d("Message from server", message.length()+"");


            AliceRequest alice=new AliceRequest();
            SecretKey secret= new SecretKey(new BigInteger(prefs.getString("Secret Key", "")));
            boolean bobRes = alice.parseBobResponse(secret, pk, message,loc);
            Log.d("Result",String.valueOf(bobRes));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        return 1;
    }



    public void disconect(){
        out.close();
        try {
            input.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
