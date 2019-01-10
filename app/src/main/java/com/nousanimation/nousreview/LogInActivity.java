package com.nousanimation.nousreview;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.io.File;

public class LogInActivity extends AppCompatActivity {

    private static final String SQL_FILE_NAME = "nousreview-db";
    private static final String TABLE_NAME = "works";
    private static final String NAME = "name";
    private static final String PROD_NAME = "production";
    private static final String DATE = "date";
    private static final String PATH = "path";
    private static final String DESCRIPTION = "description";

    public CallbackManager callbackManager;
    private String user_ID;
    private SQLiteDatabase appDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        final Button login_button = findViewById(R.id.signin_Button);
        final Button fb_button = findViewById(R.id.login_button);
        
        login_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                fb_button.performClick();
            }
        });

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        user_ID = com.facebook.Profile.getCurrentProfile().getId();
                        if(user_ID.equals("2236247573319557") ||
                                user_ID.equals("100002482389766")) {
                            startActivity(new Intent(LogInActivity.this, MainActivity.class));
                        }
                        else {
                            Toast denied_toast = Toast.makeText(getApplicationContext(), "You are not an authorized user " + com.facebook.Profile.getCurrentProfile().getFirstName(), Toast.LENGTH_LONG);
                            denied_toast.show();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast error_toast = Toast.makeText(getApplicationContext(), "An error occured! Try again!", Toast.LENGTH_SHORT);
                        error_toast.show();
                    }
                });

        if(!fileExists()) {
            appDB = getBaseContext().openOrCreateDatabase(SQL_FILE_NAME, Context.MODE_PRIVATE, null);

            String CREATE_NEWUSER_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                    + NAME + " TEXT NOT NULL, " + PROD_NAME +
                    " TEXT NOT NULL, " + DATE + " TEXT NOT NULL, " + PATH + " TEXT NOT NULL, "
                    + DESCRIPTION + " TEXT" + ")";

            appDB.execSQL(CREATE_NEWUSER_TABLE);
        }

    }

    private boolean fileExists() {
        File file = getBaseContext().getDatabasePath(SQL_FILE_NAME);
        return file.exists();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent close_Intent = new Intent(Intent.ACTION_MAIN);
        close_Intent.addCategory(Intent.CATEGORY_HOME);
        close_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(close_Intent);
    }
}
