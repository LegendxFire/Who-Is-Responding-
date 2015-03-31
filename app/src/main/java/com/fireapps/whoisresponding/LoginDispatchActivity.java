package com.fireapps.whoisresponding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by austinhodak on 3/28/15.
 */
public abstract class LoginDispatchActivity extends Activity {

    //protected abstract Class<?> getTargetClass();

    private static final int LOGIN_REQUEST = 0;
    private static final int TARGET_REQUEST = 1;

    private static final String LOG_TAG = "ParseLoginDispatch";

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runDispatch();
    }

    @Override
    final protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode);
        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            runDispatch();
        } else {
            finish();
        }
    }

    private void runDispatch() {
        if (ParseUser.getCurrentUser() != null) {
            //debugLog(getString(R.string.com_parse_ui_login_dispatch_user_logged_in) + getTargetClass());
            startActivityForResult(new Intent(this, MainNavigation.class), TARGET_REQUEST);
        } else {
            //debugLog(getString(R.string.com_parse_ui_login_dispatch_user_not_logged_in));
            Intent i = new Intent(this, SignInActivity.class);
            startActivityForResult(i, LOGIN_REQUEST);
        }
    }

    private void debugLog(String message) {
        if (Parse.getLogLevel() <= Parse.LOG_LEVEL_DEBUG &&
                Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, message);
        }
    }
}
