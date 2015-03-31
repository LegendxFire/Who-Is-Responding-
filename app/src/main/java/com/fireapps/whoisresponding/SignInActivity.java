package com.fireapps.whoisresponding;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SignInActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SignInFragment())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SignInFragment extends Fragment implements View.OnClickListener {

        @InjectView(R.id.signIn_EmailField)MaterialEditText emailField;
        @InjectView(R.id.signIn_EmailIcon)ImageView emailIcon;

        @InjectView(R.id.signIn_PasswordField)MaterialEditText passwordField;
        @InjectView(R.id.signIn_PasswordIcon)ImageView passwordIcon;

        @InjectView(R.id.signIn_Spinner)ProgressBarCircularIndeterminate spinner;

        @InjectView(R.id.signIn_Button)Button signInButton;

        public SignInFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
            ButterKnife.inject(this, rootView);

            emailField.setFloatingLabel(0);
            emailIcon.setImageDrawable(new IconDrawable(getActivity(), Iconify.IconValue.fa_envelope).color(Color.parseColor("#EEEEEE")).sizeDp(20));

            passwordField.setFloatingLabel(0);
            passwordIcon.setImageDrawable(new IconDrawable(getActivity(), Iconify.IconValue.fa_lock).color(Color.parseColor("#EEEEEE")).sizeDp(20));

            signInButton.setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onClick(View view) {
            //Second, Get Field Strings
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            //Third, Call Parse Method
            if (email.length() != 0 && password.length() != 0) {
                setSpinner(true);
                signInButton.setEnabled(false);

                ParseUser.logInInBackground(email, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            setSpinner(false);
                            Toast.makeText(getActivity(), user.getUsername(), Toast.LENGTH_SHORT).show();
                            signInButton.setEnabled(true);
                            getActivity().setResult(RESULT_OK);
                            getActivity().finish();
                        } else {
                            // Signup failed. Look at the ParseException to see what happened.
                            setSpinner(false);

                            emailField.setError("Wrong email or password.");
                            signInButton.setEnabled(true);
                        }
                    }
                });
            } else if (email.length() == 0) {
                emailField.setError("Email cannot be empty.");
            } else {
                passwordField.setError("Password cannot be empty.");
            }
        }

        private void setSpinner(boolean active){
            if(active){
                spinner.setVisibility(View.VISIBLE);
            } else {
                spinner.setVisibility(View.GONE);
            }
        }
    }
}
