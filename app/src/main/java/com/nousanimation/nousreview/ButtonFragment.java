package com.nousanimation.nousreview;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

//To Button Fragment me to opoio o xristis exei ti dynatothta na epistrefei ana pasa stigmi stin arxiki othoni (MainActivity)
public class ButtonFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View theView = inflater.inflate(R.layout.home_button_fragment, container, false);

        Button home_option = theView.findViewById(R.id.home_button);

        //OnClickListener tou koumpiou gia epistorfi sti MainActivity
        home_option.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent review_intent = new Intent(getContext(), MainActivity.class);
                startActivity(review_intent);
            }
        });
        return theView;
    }
}