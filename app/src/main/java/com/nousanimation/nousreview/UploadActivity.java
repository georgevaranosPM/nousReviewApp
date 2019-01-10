package com.nousanimation.nousreview;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private static final String SQL_FILE_NAME = "nousreview-db";
    private TextView creation_name;
    private Spinner dropdown_production, dropdown_model;
    private Date date;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private TextView description_text;
    private int the_path;
    public Limit limits;
    private SQLiteDatabase appDB;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        context = this;

        //READ DATABASE
        appDB = getBaseContext().openOrCreateDatabase(SQL_FILE_NAME, Context.MODE_PRIVATE, null);

        DownloadData downloadData = new DownloadData();
        downloadData.execute("https://api.myjson.com/bins/6hxkg");

        dropdown_production = findViewById(R.id.production_selection);
        String[] production_items = new String[]{"Advertisment", "New Movie", "Video Game"};
        ArrayAdapter<String> production_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, production_items);
        dropdown_production.setAdapter(production_adapter);

        creation_name = findViewById(R.id.name_text);
        creation_name.setHint("sthgreat.obj");

        TextView date_textview = findViewById(R.id.date_text);
        date = new Date();
        date_textview.setText(dateFormat.format(date));

        description_text = findViewById(R.id.description_container);

        dropdown_model = findViewById(R.id.model_selection);
        String[] model_items = new String[]{"Android", "Horse", "Space Cruiser"};
        ArrayAdapter<String> model_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, model_items);
        dropdown_model.setAdapter(model_adapter);

        Button browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dropdown_model.getSelectedItemPosition()==0) {
                    the_path = R.raw.android_obj;
                }
                else if (dropdown_model.getSelectedItemPosition()==1) {
                    the_path = R.raw.horse_obj;
                }
                else if (dropdown_model.getSelectedItemPosition()==2) {
                    the_path = R.raw.space_cruiser_obj;
                }

                //READING OBJ MODEL
                InputStream is = getApplicationContext().getResources().openRawResource(the_path);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuilder sBuilder = new StringBuilder();
                String line = null;
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while(line != null){
                    sBuilder.append(line).append("\n");
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int countFace = sBuilder.toString().length() - sBuilder.toString().replace("f", "").length();
                int countVert = sBuilder.toString().length() - sBuilder.toString().replace("v", "").length();

                if(countFace >= limits.minFaceCount && countFace <= limits.maxFaceCount) {
                    if(countVert >= limits.minVertCount && countVert <= limits.maxVertCount) {
                        //NEW ITEM IN DATABASE
                        appDB.execSQL("insert into works(name, production, date, path, description) " +
                                "values('"+creation_name.getText().toString()+"','"+dropdown_production.getSelectedItem().toString()+"', '"+dateFormat.format(date)+"', '"+the_path+"', '"+description_text.getText().toString()+"')");

                        Intent upload_Intent = new Intent(UploadActivity.this, MainActivity.class);
                        //upload_Intent.putExtra("path_for_file", the_path);
                        startActivity(upload_Intent);
                    }
                    else {
                        Toast errorOnVert_toast = Toast.makeText(getApplicationContext(), "Not the appropriate number of vertices!", Toast.LENGTH_LONG);
                        errorOnVert_toast.show();
                    }
                }
                else {
                    Toast errorOnFace_toast = Toast.makeText(getApplicationContext(), "Not the appropriate number of faces!", Toast.LENGTH_LONG);
                    errorOnFace_toast.show();
                }
            }
        });

    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadLimitsTask";

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Log.d(TAG, "onPostExecute parameter is " + jsonData );
            JSONParser parser = new JSONParser(limits);
            parser.parseJson(jsonData);

            limits = parser.getLimits();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground starts with: " + strings[0]);
            String postData = downloadJSON(strings[0]);
            if(postData == null){
                Log.e(TAG, "doInBackground: Error downloading from url " + strings[0] );
            }
            return postData;
        }

        private String downloadJSON(String urlPath) {
            StringBuilder sb = new StringBuilder();

            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "DownloadJSON: Response code was " + responseCode);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line = reader.readLine();
                while(line != null){
                    sb.append(line).append("\n");
                    line = reader.readLine();
                }

                reader.close();

            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadJSON: not correct URL: "+urlPath , e);
            } catch (IOException e) {
                Log.e(TAG, "downloadJSON: io error ",e);
            }

            return sb.toString();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        appDB.close();
    }
}
