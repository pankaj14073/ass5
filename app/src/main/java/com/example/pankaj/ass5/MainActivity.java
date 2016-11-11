package com.example.pankaj.ass5;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {

    TextView tvDataJson;
    Button btnFetchWeather;
   TextView Tittle;
    String  title;
    private ActivityControl activityControl;
    private FetchWeatherData fetchWeatherData;

    protected class ActivityControl
    {
        protected boolean isInProgress;
        protected TextView textView;
        protected TextView tittle;
        protected String s;
        protected String t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvDataJson = (TextView) findViewById(R.id.tv_data_json);
        Tittle=(TextView) findViewById(R.id.tittle);
        btnFetchWeather = (Button) findViewById(R.id.btn_fetch_weather);
        restoreFromObject();

        btnFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchWeatherData().execute();

            }
        });
    }
    @Override
    public Object onRetainNonConfigurationInstance() {
        activityControl.textView = null;
        return activityControl;
    }
 public class data
 {
     String s;
     String t;
 }
    private void restoreFromObject()
    {
        activityControl = (ActivityControl) getLastNonConfigurationInstance();


        if (activityControl == null)
        {
            activityControl = new ActivityControl();
            activityControl.textView = tvDataJson;
            activityControl.tittle= Tittle;
          //  activityControl.textView.setText("Doing");
            //fetchWeatherData = new FetchWeatherData();
            //fetchWeatherData.execute((Void[])null);
        } else {
            activityControl.textView =tvDataJson;
            activityControl.tittle=Tittle;
            if (activityControl.isInProgress)
                activityControl.textView.setText("Doing");
            else {
                activityControl.textView.setText(activityControl.s);
                activityControl.tittle.setText(activityControl.t);
            }
            }
    }

    private class FetchWeatherData extends AsyncTask<Void, Void, data> {
        @Override
        protected data doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast


                URL url = new URL("https://www.iiitd.ac.in/about");
                String x = "https://www.iiitd.ac.in/about";
                Document document = Jsoup.connect(x).get();
                    title = document.title();


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                data d=new data();
                d.s=forecastJsonStr;
                d.t=title;
                return d;
            } catch (IOException e) {

                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        protected void onPostExecute(data d)
        {
            super.onPostExecute(d);
            tvDataJson.setText(d.s);
            Tittle.setText(d.t);
            activityControl.isInProgress = false;
            activityControl.s=d.s;
            activityControl.t=d.t;
            Log.i("json", d.s);
        }
    }

}
/*
class DownloadFileFromURL extends AsyncTask<String, String, String> {


@Override
protected void onPreExecute() {
    super.onPreExecute();
    System.out.println("Starting download");

    pDialog = new ProgressDialog(MainActivity.this);
    pDialog.setMessage("Loading... Please wait...");
    pDialog.setIndeterminate(false);
    pDialog.setCancelable(false);
    pDialog.show();
}

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            String root = Environment.getExternalStorageDirectory().toString();

            System.out.println("Downloading");
            URL url = new URL(f_url[0]);

            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file

            OutputStream output = new FileOutputStream(root+"/downloadedfile.html");
            byte data[] = new byte[1024];

            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;

                // writing data to file
                output.write(data, 0, count);

            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }


    @Override
    protected void onPostExecute(String file_url) {
        tvDataJson.setText(s);
        System.out.println("Downloaded");

        pDialog.dismiss();
    }

}

}
*/