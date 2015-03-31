package com.fireapps.whoisresponding;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RespondingFragment extends Fragment {

    @InjectView(R.id.responding_ButtonStation)Button stationButton;
    @InjectView(R.id.responding_ButtonNone)Button noneButton;
    @InjectView(R.id.responding_ButtonScene)Button sceneButton;

    TextToSpeech textToSpeech;

    public RespondingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_responding, container, false);
        ButterKnife.inject(this, view);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        stationButton.setTypeface(font);
        noneButton.setTypeface(font);
        sceneButton.setTypeface(font);


        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
            }
        });


        stationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stationButton.setTextColor(getResources().getColor(R.color.primary_red));
                stationButton.setTextColor(getResources().getColor(R.color.primary_red));
                noneButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                sceneButton.setTextColor(getResources().getColor(R.color.md_white_1000));

                textToSpeech.speak("Copy, Responding to station.", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        noneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noneButton.setTextColor(getResources().getColor(R.color.primary_red));
                noneButton.setTextColor(getResources().getColor(R.color.primary_red));
                stationButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                sceneButton.setTextColor(getResources().getColor(R.color.md_white_1000));
            }
        });
        sceneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sceneButton.setTextColor(getResources().getColor(R.color.primary_red));
                sceneButton.setTextColor(getResources().getColor(R.color.primary_red));
                noneButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                stationButton.setTextColor(getResources().getColor(R.color.md_white_1000));
            }
        });

        return view;
    }


}
