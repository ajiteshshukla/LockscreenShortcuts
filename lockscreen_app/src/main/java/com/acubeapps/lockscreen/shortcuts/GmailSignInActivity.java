package com.acubeapps.lockscreen.shortcuts;

import com.inmobi.oem.moments.matcher.demoapp.MockMomentsActivity;
import com.acubeapps.lockscreen.shortcuts.BuildConfig;
import com.acubeapps.lockscreen.shortcuts.analytics.Analytics;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import timber.log.Timber;

import javax.inject.Inject;


/**
 * Created by ajitesh.shukla on 6/29/16.
 */
public class GmailSignInActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private ConnectionResult connectionResult;

    private boolean mIntentInProgress;

    private boolean mShouldResolve;

    private static final int RC_SIGN_IN = 0;

    private static final String TAG = "Gmail SignIn : ";

    private SignInButton signInButton;

    @Inject
    Analytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.acubeapps.lockscreen.shortcuts.R.layout.activity_gmail_login);

        signInButton = (SignInButton) findViewById(com.acubeapps.lockscreen.shortcuts.R.id.btn_sign_in);
        signInButton.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        onSignInClicked();
    }

    private void resolveSignInError() {
        if (connectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == com.acubeapps.lockscreen.shortcuts.R.id.btn_sign_in) {
            onSignInClicked();
        }
    }

    @Override
    public void onConnectionSuspended(int inti) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            connectionResult = result;
            if (mShouldResolve) {
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mShouldResolve = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        getInfo();
        signInButton.setEnabled(false);
        Toast.makeText(this, "You have successfully logged in", Toast.LENGTH_LONG).show();
        if (BuildConfig.BUILD_TYPE.contains("debug")) {
            Intent mockMomentsActivity = new Intent(this, MockMomentsActivity.class);
            this.startActivity(mockMomentsActivity);
        }
    }

    private void getInfo() {
        mShouldResolve = false;
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person person = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = person.getDisplayName();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Timber.d("email - " + email);
                Timber.d("name - " + personName);
                this.getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit()
                        .putString(Constants.USERNAME, personName).apply();
                this.getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit()
                        .putString(Constants.EMAIL_ID, email).apply();
                analytics.logUserMeta(personName, email);

            } else {
                Timber.e("unable to get person info ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSignInClicked() {
        if (!mGoogleApiClient.isConnecting()) {
            mShouldResolve = true;
            mIntentInProgress = false;
            mGoogleApiClient.connect();
        }
    }
}
