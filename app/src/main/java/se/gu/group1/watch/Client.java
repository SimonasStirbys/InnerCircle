package se.gu.group1.watch;

import android.os.AsyncTask;
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
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Omar on 4/15/2016.
 */
class Client  {

    String dstAddress;
    int dstPort;
    Socket socket = null;
    BufferedReader input;
    PrintWriter out;

    // takes the Public Ip , port num
    Client(String addr, int port) {
        dstAddress = addr;
        dstPort = port;

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
        out.close();
        try {
            input.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }




}
