package com.lenovo.newgame.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lenovo.newgame.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
        FirebaseAuth mAuth;
    LinearLayout trigno, learnbook, object;
    ImageButton setting, share;
    static final int RC_SIGN_IN =1;
    private static String TAG = "LoginActivity";
    private GoogleApiClient mGoogleSignInClient;

    FirebaseUser user;
    ProgressDialog mprogress;
    public DatabaseReference myref;
    public DatabaseReference mchild;
    public StorageReference storeimage;
    private static final String LOCALE_KEY = "localekey";
    private static final String HINDI_LOCALE = "hi";
    private static final String ENGLISH_LOCALE = "en_US";
    private static final String LOCALE_PREF_KEY = "localePref";
    @BindView(R.id.simpleProgressBar)
    RoundCornerProgressBar simpleProgressBar;
    @BindView(R.id.simpleProgressBar2)
    RoundCornerProgressBar simpleProgressBar2;
    @BindView(R.id.simpleProgressBar3)
    RoundCornerProgressBar simpleProgressBar3;

    int basiclevel,objectlevel,trignolevel;
    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
//        learn=(LinearLayout) findViewById(R.id.learn);
        trigno = (LinearLayout) findViewById(R.id.trigo);
        learnbook = (LinearLayout) findViewById(R.id.learnbook);
        object = (LinearLayout) findViewById(R.id.object);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.bclick);
        SharedPreferences prefs = this.getSharedPreferences(
                "level", Context.MODE_PRIVATE);
        basiclevel = prefs.getInt("basiclevel",0);
        objectlevel = prefs.getInt("objectlevel",0);
        trignolevel = prefs.getInt("trignolevel",0);
        simpleProgressBar.setProgress(basiclevel*10);
        simpleProgressBar2.setMax( 100 );
        simpleProgressBar2.setProgress(objectlevel*10);
        simpleProgressBar3.setProgress(trignolevel*10);
        simpleProgressBar3.setMax(100);
 mprogress = new ProgressDialog(this);
        myref = FirebaseDatabase.getInstance().getReference();
        mchild = myref.child("User").push();
        storeimage = FirebaseStorage.getInstance().getReference();

        // ...
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi( Auth.GOOGLE_SIGN_IN_API,gso)
                .build();



        learnbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent i = new Intent(HomeActivity.this, Main2Activity.class);
                startActivity(i);
            }
        });


        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent i = new Intent(HomeActivity.this, object.class);
                startActivity(i);
            }
        });

        trigno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent i = new Intent(HomeActivity.this, TrignoActivity.class);
                startActivity(i);
            }
        });
    }

    public void easylevel(View view) {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.bclick);
        mp.start();
        Intent intent = new Intent(HomeActivity.this, basic.class);
        startActivity(intent);
    }




    //   Sign In function
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                mprogress.setMessage("Please wait..... ");
                mprogress.show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
                mprogress.dismiss();

            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            mprogress.show();

            mprogress.dismiss();
            Intent intent = new Intent(HomeActivity.this,Scoreborad.class);
            startActivity(intent);

            finish();

        }
        else
        {
            signIn();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser User= mAuth.getCurrentUser();
                            uploadData(user);
                            updateUI( User );

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(HomeActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                        }


                        // ...
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    private void uploadData(FirebaseUser user) {
        if(user != null)
        {
            int scorefinal = objectlevel+basiclevel+trignolevel;
            String name =  user.getDisplayName();
            Toast.makeText( this, ""+name, Toast.LENGTH_SHORT ).show();
            Uri photo = user.getPhotoUrl();
            String score = String.valueOf( scorefinal );
            mchild.child("username").setValue(name);
            mchild.child("profileimage").setValue(photo.toString());
            mchild.child( "score" ).setValue( score );
            Toast.makeText(this, "Score Udapted", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(HomeActivity.this,Scoreborad.class));
        }
        else{
            Toast.makeText( this, "Not uplaod data", Toast.LENGTH_SHORT ).show();
        }
    }

    public void leadboard(View view) {
        signIn();
    }
}
