package com.trybe.project.petitionapp;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trybe.project.petitionapp.fragments.AddNewPetitionFragment;
import com.trybe.project.petitionapp.fragments.NewsFragment;
import com.trybe.project.petitionapp.fragments.PetitionsFragment;
import com.trybe.project.petitionapp.fragments.ProfileFragment;
import com.trybe.project.petitionapp.fragments.VictoriesFragment;

public class MainNavigationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore firebaseFirestore;

    private PetitionsFragment petitionsFragment;
    private NewsFragment newsFragment;
    private AddNewPetitionFragment addNewPetitionFragment;
    private VictoriesFragment victoriesFragment;
    private ProfileFragment profileFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_petitions:
                    replaceFragment(petitionsFragment);
                    return true;
                case R.id.navigation_news:
                    replaceFragment(newsFragment);
                    return true;
                case R.id.navigation_victories:
                   replaceFragment(victoriesFragment);
                    return true;
                case R.id.navigation_profile:
                    replaceFragment(profileFragment);
                    return true;
                case R.id.navigation_add_new:
                    startActivity(new Intent(MainNavigationActivity.this, NewPetitionActivity.class));
                    //replaceFragment(addNewPetitionFragment);
                    return true;
//                default:
//                    return false;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        Toolbar toolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cohort");
        firebaseFirestore = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        //initializeFragments
        petitionsFragment = new PetitionsFragment();
        newsFragment = new NewsFragment();
        addNewPetitionFragment = new AddNewPetitionFragment();
        victoriesFragment = new VictoriesFragment();
        profileFragment = new ProfileFragment();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser==null){
            updateUI(currentUser);
        }else{
            for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com")) {
                    System.out.println("User is signed in with Facebook");
                }else{
                    System.out.println("User is signed in with "+user.getProviderId());
                }
            }

        }

        retrieveFromFirestore(currentUser);


    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(petitionsFragment);
    }

    private void retrieveFromFirestore(FirebaseUser currentUser) {

        firebaseFirestore.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        //Toast.makeText(MainNavigationActivity.this, "Data Exist", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //Toast.makeText(MainNavigationActivity.this, "Data doesn't Exist", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainNavigationActivity.this, AccountSetupActivity.class));
                    }
                }else{
                    String error = task.getException().getMessage();
                    //Toast.makeText(MainNavigationActivity.this, "firebaseFirestore error "+error, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        startActivity(new Intent(MainNavigationActivity.this, LoginActivity.class));
        //Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                // Action goes here
                return true;
            case R.id.menu_account_settings:
                // Action goes here
                startActivity(new Intent(MainNavigationActivity.this, AccountSetupActivity.class));
                return true;
            case R.id.menu_logout:
                // Action goes here
                FirebaseUser currentUser = mAuth.getCurrentUser();
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                updateUI(currentUser);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer,fragment);
        fragmentTransaction.commit();

    }
}
