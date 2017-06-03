package br.com.tonhaosemacento.rachaconta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;
import android.support.design.widget.NavigationView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import br.com.tonhaosemacento.rachaconta.utils.SharedPrefManager;

// This class is a simple activity with NavigationDrawer
// we get data stored in sharedPrefference and display on the header view of the NavigationDrawer
public class NavDrawerActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    Context mContext = this;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView tvFullNameTextView, tvEmailTextView;
    private CircleImageView ciProfileImageView;
    private String cUsername, cEmail;
    FloatingActionButton btnAddExpense;

    //save date in phone
    SharedPrefManager sharedPrefManager;
    private GoogleApiClient mGoogleApiClient;
    //private FirebaseAuth mAuth;

    /**
     * onCreate is ther first method to be called when activity is called
     * **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        findViews();
        setActions();

    }


    /**
     * used to bind the wiews with variables
     * **/
    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //set up navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //is in findView instead of setActions because the
        //other views use this
        initNavigationDrawer();

        View header = navigationView.getHeaderView(0);

        tvFullNameTextView = (TextView) header.findViewById(R.id.fullName);
        tvEmailTextView = (TextView) header.findViewById(R.id.email);
        ciProfileImageView = (CircleImageView) header.findViewById(R.id.profileImage);
        btnAddExpense = (FloatingActionButton) findViewById(R.id.btn_add_expense);


    }

    /**
     * set actions with the binded objects
     * tried butter knife but cant go on, confuses me
     * **/
    private void setActions(){

        setSupportActionBar(toolbar);

        // create an object of sharedPreferenceManager and get stored user data
        sharedPrefManager = new SharedPrefManager(mContext);
        cUsername = sharedPrefManager.getName();
        cEmail = sharedPrefManager.getUserEmail();
        String uri = sharedPrefManager.getPhoto();
        Uri mPhotoUri = Uri.parse(uri);

        //Set data gotten from SharedPreference to the Navigation Header view
        tvFullNameTextView.setText(cUsername);
        tvEmailTextView.setText(cEmail);

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavDrawerActivity.this,AddExpenseActivity.class);
                startActivity(intent);
            }
        });

        Picasso.with(mContext)
                .load(mPhotoUri)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(ciProfileImageView);

        configureSignIn();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Initialize and add Listener to NavigationDrawer
    public void initNavigationDrawer(){


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.item_add_expense:
                        btnAddExpense.callOnClick();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        signOut();
                        drawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    // This method configures Google SignIn
    public void configureSignIn(){
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }

    //method to logout
    private void signOut(){
        //clear data in phone
        new SharedPrefManager(mContext).clear();
        //logout from firebase, but give me error xD
        //mAuth.signOut();

        //return to main activity
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Intent intent = new Intent(NavDrawerActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
