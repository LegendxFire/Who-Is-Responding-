package com.fireapps.whoisresponding;

import android.app.Application;
import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by austinhodak on 3/28/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Parse Init
        Parse.initialize(this, "rQV5z206sFSGeudJ94H5iXiOV2muYQlLNtMJ4WWM", "AO9u6iRO8TG9zrmgpv4FF52EyPA8FmURvOAap0kk");

    }
}
