package br.com.tonhaosemacento.rachaconta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ServerValue;
import br.com.tonhaosemacento.rachaconta.utils.Constants;
import br.com.tonhaosemacento.rachaconta.utils.SharedPrefManager;
import br.com.tonhaosemacento.rachaconta.utils.Utils;
import br.com.tonhaosemacento.rachaconta.models.User;
import com.firebase.client.Firebase;

import java.util.HashMap;

// This class handles Google Firebase Authentication and also saves the user details to Firebase
public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    public FirebaseAuth mAuth; //this need to be public to be called in drawer
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private String idToken;
    public SharedPrefManager sharedPrefManager;
    private final Context mContext = this;

    private String name, email;
    private String photo;
    private Uri photoUri;
    private SignInButton mSignInButton;

    /**
     * onCreate is ther first method to be called when activity is called
     * **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setActions();
        configureSignIn();
    }

    /**
     * used to bind the wiews with variables
     * **/
    private void findViews() {
        mSignInButton = (SignInButton) findViewById(R.id.login_with_google);
    }

    /**
     * set actions with the binded objects
     * tried butter knife but cant go on, confuses me
     * **/
    private void setActions(){
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mSignInButton.setOnClickListener(this);
    }

    //return the mAuth to be used in other activity
    public FirebaseAuth getmAuth(){
        return this.mAuth;
    }

    //This method creates a new user on our own Firebase database
    //after a successful Authentication on Firebase
    //It also saves the user info to SharedPreference
    private void createUserInFirebaseHelper(){

        //Since Firebase does not allow "." in the key name, we'll have to encode and change the "." to ","
        // using the encodeEmail method in class Utils
        final String encodedEmail = Utils.encodeEmail(email.toLowerCase());

        //create an object of Firebase database and pass the the Firebase URL
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);

        //Add a Listerner to that above location
        userLocation.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    // Insert into Firebase database
                    User newUser = new User(name, photo, encodedEmail, timestampJoined);
                    userLocation.setValue(newUser);

                    Toast.makeText(MainActivity.this, getString(R.string.account_created), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.d(TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
                //hideProgressDialog();
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN){
                }
                else {
                    Toast.makeText(MainActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // This method configures Google SignIn
    public void configureSignIn(){
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(MainActivity.this.getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
                    createUserInFirebaseHelper();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    // This method is called when the signIn button is clicked on the layout
    // It prompts the user to select a Google account.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // This is the method where the result of clicking the signIn button will be handled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                idToken = account.getIdToken();

                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
                photo = photoUri.toString();

                // Save Data to SharedPreference
                sharedPrefManager = new SharedPrefManager(mContext);
                sharedPrefManager.saveIsLoggedIn(mContext, true);

                sharedPrefManager.saveEmail(mContext, email);
                sharedPrefManager.saveName(mContext, name);
                sharedPrefManager.savePhoto(mContext, photo);

                sharedPrefManager.saveToken(mContext, idToken);
                //sharedPrefManager.saveIsLoggedIn(mContext, true);

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, getString(R.string.login_unsuccessful));
                Toast.makeText(this, getString(R.string.login_unsuccessful) , Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(AuthCredential credential){
        //loading
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(MainActivity.this, getString(R.string.authentication_failed),
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            //navigate to drawer page
                            createUserInFirebaseHelper();
                            Toast.makeText(MainActivity.this, getString(R.string.login_successful),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, NavDrawerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        hideProgressDialog();
                    }
                });
    }

    //if app starts and firebase is not null
    //signout from firebase
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null){
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    //when app stop remove the listener
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Called when the client is connected state
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    //Called when the client is temporarily in a disconnected state
    @Override
    public void onConnectionSuspended(int i) {

    }

    //onClick google signin button
    @Override
    public void onClick(View view) {

        Utils utils = new Utils(this);
        int id = view.getId();

        if (id == R.id.login_with_google){
            //verify conectivity
            if (utils.isNetworkAvailable()){
                //signin
                signIn();
            }else {
                //no internet :'(
                Toast.makeText(MainActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Provides callbacks for scenarios that result in a failed attempt to connect the client to the service
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
