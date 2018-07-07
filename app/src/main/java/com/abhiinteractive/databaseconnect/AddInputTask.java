package com.abhiinteractive.databaseconnect;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class AddInputTask extends AsyncTask<String, Void, String>{

    Context context;

    public AddInputTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        //Getting input from param
        String input = params[0];

        //TODO: Substitute this with your server details
        String addContactURL = "http://abhiinteractive.000webhostapp.com/add_input.php";
        try {
            //Setup HttpURLConnection with the server
            URL url = new URL(addContactURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data = URLEncoder.encode("input", "UTF-8") + "=" + URLEncoder.encode(input, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS = httpURLConnection.getInputStream();
            IS.close();

            httpURLConnection.disconnect();

            return "Input added";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "Failed to add input";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

}
