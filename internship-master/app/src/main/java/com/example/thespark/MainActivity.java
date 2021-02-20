package com.example.thespark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    TextView textView;
    private static final int RC_SIGN_IN = 1;
    LoginButton loginButton;
    CallbackManager callbackManager;
    ImageView imageView;
    TextView txtUsername, txtEmail;
    CircleImageView circleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.login_button);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        circleImageView = findViewById(R.id.proflie_pic);

        //  FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        checkLoginStatus();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
            }

            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {

            }


        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.google_btn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    AccessTokenTracker tokentracker = new AccessTokenTracker() {

        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken == null) {

                txtUsername.setText("");
                txtEmail.setText("");
                circleImageView.setImageResource(0);
                Toast.makeText(MainActivity.this, "User Logged Out ", Toast.LENGTH_LONG).show();


            } else {
                loadUserProfile(currentAccessToken);
            }


        }

    } ;

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            MainActivity.super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }

        }

        private void handleSignInResult(GoogleSignInResult result) {
            if (result.isSuccess()) {
                gotoProfile();
            } else {
                Toast.makeText(getApplicationContext(), "Sign in cancel", Toast.LENGTH_LONG).show();
            }
        }

        private void loadUserProfile(AccessToken newAccessToken) {
            GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {

                public void onCompleted(JSONObject object, GraphResponse response) {

                    try {
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");
                        String email = object.getString("email");
                        String id = object.getString("id");
                        String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                        txtEmail.setText(email);
                        txtUsername.setText(first_name + " " + last_name);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.dontAnimate();

                        Glide.with(MainActivity.this).load(image_url).into(circleImageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "first_name,last_name,email,id");
            request.setParameters(parameters);
            request.executeAsync();


        }

        private void checkLoginStatus() {

            if (AccessToken.getCurrentAccessToken() != null) {
                loadUserProfile(AccessToken.getCurrentAccessToken());
            }
        }


        private void gotoProfile() {
            Intent intent = new Intent(MainActivity.this, Profile.class);
            startActivity(intent);
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

    }
