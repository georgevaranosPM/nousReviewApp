package com.nousanimation.nousreview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button review_Option = findViewById(R.id.review_Button);
        Button upload_Option = findViewById(R.id.upload_Button);

        Button signout = findViewById(R.id.signout_button);

        //Button listener gia ti metavasi sto activity opou tha elegxeis ta modela
        review_Option.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent review_intent = new Intent(MainActivity.this, WorksListActivity.class);
                startActivity(review_intent);
            }
        });

        //Button listener gia ti metavasi sto activity opou tha anevazeis modela
        upload_Option.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent upload_Intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(upload_Intent);
            }
        });

        //Button listener gia aposindesi
        signout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                LoginManager.getInstance().logOut();
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
            }
        });
    }

    //Override tis methodou tis opoias otan patas to koubi "Back" na se anagkazeis na vgeis mono me aposindesi
    @Override
    public void onBackPressed() {
        Toast cancel_toast = Toast.makeText(getApplicationContext(), "You must sign out!", Toast.LENGTH_SHORT);
        cancel_toast.show();
    }
}
