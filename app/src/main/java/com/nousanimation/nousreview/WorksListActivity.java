package com.nousanimation.nousreview;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class WorksListActivity extends AppCompatActivity {

    private static final String SQL_FILE_NAME = "nousreview-db";
    private ArrayList<Work> allWorks_Review = new ArrayList<>();
    private ListView workListView;
    private SQLiteDatabase appDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works_list);

        //READ DATABASE
        appDB = getBaseContext().openOrCreateDatabase(SQL_FILE_NAME, Context.MODE_PRIVATE, null);
        readDataFromDB(0);

        final Spinner sortby_spinner = findViewById(R.id.sortby);
        String[] sorting = new String[]{"Date", "Alphabetically", "Production"};
        ArrayAdapter<String> sorting_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sorting);
        sortby_spinner.setAdapter(sorting_adapter);

        sortby_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SORTING", "With: " + position);
                allWorks_Review.clear();
                readDataFromDB(position);
                workListView.invalidateViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        parseData();



        workListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent modelView = new Intent(WorksListActivity.this, ModelViewActivity.class);
                modelView.putExtra("path_for_file", allWorks_Review.get(i).getPath());
                startActivity(modelView);
            }
        });

        workListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast description_Toast = Toast.makeText(getApplicationContext(), allWorks_Review.get(i).getDescription(), Toast.LENGTH_LONG);
                description_Toast.show();
                return true;
            }
        });



    }

    private void readDataFromDB(int how){
        Cursor cursor = appDB.rawQuery("select * from works",
                null);

        if(how==1) {
            cursor = appDB.rawQuery("select * from works order by name",
                    null);
        }
        else if(how==2) {
            cursor = appDB.rawQuery("select * from works order by production",
                    null);
        }

        while(cursor.moveToNext()){
            Work temp = new Work("", "", "", 0, "");
            temp.setName(cursor.getString(0));
            temp.setProduction(cursor.getString(1));
            temp.setUpload_date(cursor.getString(2));
            temp.setPath(cursor.getInt(3));
            temp.setDescription(cursor.getString(4));

            allWorks_Review.add(temp);

        }
        cursor.close();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(WorksListActivity.this, MainActivity.class));
    }

    private void parseData() {
        WorkAdapter workAdapter = new
                WorkAdapter(WorksListActivity.this,
                R.layout.work_record,
                allWorks_Review);

        workListView = findViewById(R.id.works_list);
        workListView.setAdapter(workAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        appDB.close();
    }
}
