package com.fireapps.whoisresponding;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.fireapps.whoisresponding.MainNavigation;
import com.parse.SaveCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class RespondingFragment extends Fragment implements AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @InjectView(R.id.responding_ButtonStation)Button stationButton;
    @InjectView(R.id.responding_ButtonNone)Button noneButton;
    @InjectView(R.id.responding_ButtonScene)Button sceneButton;

    @InjectView(R.id.responding_ListView)ListView listView;
    @InjectView(R.id.progressBarCircularIndeterminate)ProgressBarCircularIndeterminate spinner;

    TextToSpeech textToSpeech;
    private RespondingListAdapter adapter;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    ParseUser currentUser;

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

        currentUser = ParseUser.getCurrentUser();

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

                //respondingClicked(1);

                LongOperation task = new LongOperation();
                task.execute(new String[]{"Station"});
            }
        });
        noneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noneButton.setTextColor(getResources().getColor(R.color.primary_red));
                noneButton.setTextColor(getResources().getColor(R.color.primary_red));
                stationButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                sceneButton.setTextColor(getResources().getColor(R.color.md_white_1000));

                //respondingClicked(0);

                LongOperation task = new LongOperation();
                task.execute(new String[]{"NR"});
            }
        });
        sceneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sceneButton.setTextColor(getResources().getColor(R.color.primary_red));
                sceneButton.setTextColor(getResources().getColor(R.color.primary_red));
                noneButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                stationButton.setTextColor(getResources().getColor(R.color.md_white_1000));

                //respondingClicked(2);

                LongOperation task = new LongOperation();
                task.execute(new String[]{"Scene"});
            }
        });

        adapter = new RespondingListAdapter(getActivity(), new ArrayList<MemberObject>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        spinner.setVisibility(View.GONE);

        updateList();

        updateButtons();

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        setHasOptionsMenu(true);

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

                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        // only for gingerbread and newer versions
                        adapter.addAll(list);
                    } else {
                        for (int i = 0; i < list.size(); i++)
                        {
                            MemberObject memberObject = list.get(i);
                            adapter.add(memberObject);
                        }
                    }
                    spinner.setVisibility(View.GONE);
                } else {
                    //Try again, has to update.
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
            new BottomSheet.Builder(getActivity()).title(memberObject.get("name").toString()).sheet(R.menu.bottom_menu).listener(new DialogInterface.OnClickListener() {
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

    public void respondingClicked(int to){
        //0 = NR, 1 = STATION, 2 = SCENE
        ParseUser currentUser = ParseUser.getCurrentUser();

        String respondingFromCity = getLocationName();

        currentUser.put("respondingTo", "");
        if(to == 0) {
            currentUser.put("respondingTo", "NR");
        } else if(to == 1) {
            currentUser.put("respondingTo", "Station");
            currentUser.put("respondingFrom", respondingFromCity);
        } else if(to == 2) {
            currentUser.put("respondingTo", "Scene");
            currentUser.put("respondingFrom", respondingFromCity);
        }

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                updateList();
            }
        });

    }

    public void updateButtons() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String respondingStatus = currentUser.get("respondingTo").toString();

        Toast.makeText(getActivity(), respondingStatus, Toast.LENGTH_SHORT).show();

        switch (respondingStatus) {
            case "Station":
                stationButton.setTextColor(getResources().getColor(R.color.primary_red));
                noneButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                sceneButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                break;
            case "Scene":
                sceneButton.setTextColor(getResources().getColor(R.color.primary_red));
                noneButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                stationButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                break;
            case "NR":
                noneButton.setTextColor(getResources().getColor(R.color.primary_red));
                stationButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                sceneButton.setTextColor(getResources().getColor(R.color.md_white_1000));
                break;
        }

    }

    //Location Stuff
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPlayServices();
    }

    @Override
    public Object getSharedElementEnterTransition() {
        return super.getSharedElementEnterTransition();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_responding, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.call_department) {
            final String departmentID = currentUser.get("department").toString();


            //TODO Do on start and cache.
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Departments");
            query.getInBackground(departmentID, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        // object will be your game score
                        String departmentPhone = object.get("departmentPhone").toString();

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + departmentPhone));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }

                    } else {
                        // something went wrong
                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    public String getLocationName() {

        double latitude = 0;
        double longitude = 0;

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {

            latitude = mLastLocation.getLatitude();

            longitude = mLastLocation.getLongitude();

            //Toast.makeText(this, latitude + ", " + longitude, Toast.LENGTH_LONG).show();

            //setTitle(getLocationName(latitude, longitude));

        } else {

            Toast.makeText(getActivity(), "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_LONG).show();
        }

        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        try {

            List<Address> addresses = gcd.getFromLocation(latitude, longitude,
                    10);

            for (Address adrs : addresses) {
                if (adrs != null) {

                    String city = adrs.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                        System.out.println("city ::  " + cityName);
                    } else {

                    }
                    // // you should also try with addresses.get(0).toSring();

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;

    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                ParseUser currentUser = ParseUser.getCurrentUser();

                String respondingFromCity = getLocationName();

                currentUser.put("respondingTo", "");
                switch (params[0]) {
                    case "NR":
                        currentUser.put("respondingTo", "NR");
                        break;
                    case "Station":
                        currentUser.put("respondingTo", "Station");
                        currentUser.put("respondingFrom", respondingFromCity);
                        break;
                    case "Scene":
                        currentUser.put("respondingTo", "Scene");
                        currentUser.put("respondingFrom", respondingFromCity);
                        break;
                }

                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        updateList();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {}

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
