package com.fireapps.whoisresponding;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.cocosw.bottomsheet.BottomSheet;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RespondingFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.responding_ButtonStation)Button stationButton;
    @InjectView(R.id.responding_ButtonNone)Button noneButton;
    @InjectView(R.id.responding_ButtonScene)Button sceneButton;

    @InjectView(R.id.responding_ListView)ListView listView;
    @InjectView(R.id.progressBarCircularIndeterminate)ProgressBarCircularIndeterminate spinner;

    TextToSpeech textToSpeech;
    private RespondingListAdapter adapter;

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

                //textToSpeech.speak("Copy, Responding to station.", TextToSpeech.QUEUE_FLUSH, null);
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

        adapter = new RespondingListAdapter(getActivity(), new ArrayList<MemberObject>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        spinner.setVisibility(View.GONE);

        updateList();

        return view;
    }

    public void updateList(){
        spinner.setVisibility(View.VISIBLE);
        String[] strings = {"Scene", "Station"};
        ParseQuery<MemberObject> query = ParseQuery.getQuery(MemberObject.class);
        query.whereEqualTo("department", ParseUser.getCurrentUser().get("department"));
        query.whereContainedIn("respondingTo", Arrays.asList(strings));
        query.findInBackground(new FindCallback<MemberObject>() {
            @Override
            public void done(List<MemberObject> list, ParseException e) {
                if(e == null) {
                    adapter.clear();
                    adapter.addAll(list);
                    spinner.setVisibility(View.GONE);
                } else {
                    //Try again, NEEDS to be updated.
                    updateList();
                    spinner.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ParseUser memberObject = adapter.getItem(i);

        String phoneNumber = null;
        try {
            phoneNumber = memberObject.get("phone").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(phoneNumber != null) {
            final String finalPhoneNumber = phoneNumber;
            new BottomSheet.Builder(getActivity()).title(memberObject.get("name").toString()).sheet(R.menu.bottom_menu).darkTheme().listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.call:
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + finalPhoneNumber));
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                            break;
                        case R.id.message:
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", finalPhoneNumber, null)));
                            break;
                    }
                }
            }).show();
        } else {

        }
    }
}
