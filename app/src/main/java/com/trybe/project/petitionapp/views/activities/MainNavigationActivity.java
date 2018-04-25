package com.trybe.project.petitionapp.views.activities;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

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
import com.trybe.project.petitionapp.R;
import com.trybe.project.petitionapp.views.fragments.AddNewPetitionFragment;
import com.trybe.project.petitionapp.views.fragments.NewsFragment;
import com.trybe.project.petitionapp.views.fragments.PetitionsFragment;
import com.trybe.project.petitionapp.views.fragments.ProfileFragment;
import com.trybe.project.petitionapp.views.fragments.VictoriesFragment;

import java.util.List;

/**
 * Created by Waqas Khalid Obeidy on 29/3/2018.
 */
public class MainNavigationActivity extends BaseActivity {

    private final String TAG = MainNavigationActivity.class.getSimpleName().toString();
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore firebaseFirestore;

    private PetitionsFragment petitionsFragment;
    private NewsFragment newsFragment;
    private AddNewPetitionFragment addNewPetitionFragment;
    private VictoriesFragment victoriesFragment;
    private ProfileFragment profileFragment;
    BottomNavigationView navigation;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
            switch (item.getItemId()) {
                case R.id.navigation_petitions:
                    replaceFragment(petitionsFragment,currentFragment);
                    //animateToFragment(petitionsFragment, "petitionsFragment");
                    return true;
                case R.id.navigation_news:
                    replaceFragment(newsFragment,currentFragment);

                    // animateToFragment(newsFragment, "newsFragment");
                    return true;
                case R.id.navigation_victories:
                    replaceFragment(victoriesFragment,currentFragment);

                    // animateToFragment(victoriesFragment, "victoriesFragment");
                    return true;
                case R.id.navigation_profile:
                    replaceFragment(profileFragment,currentFragment);

                    //animateToFragment(profileFragment, "profileFragment");
                    //startActivity(new Intent(MainNavigationActivity.this, NewAnnouncementActivity.class));
                    return true;
                case R.id.navigation_add_new:
                   /* for(Fragment fragment:getSupportFragmentManager().getFragments()){

                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }*/

                   //startActivity(new Intent(MainNavigationActivity.this, NewPetitionActivity.class));

                    //replaceFragment(addNewPetitionFragment);
                    //animateToFragment(addNewPetitionFragment, "addNewPetitionFragment");
                    return true;
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
        getSupportActionBar().setTitle("");
        firebaseFirestore = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initializeFragments();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //BottomNavigationViewHelper.disableShiftMode(navigation);
        //navigation.getMenu().findItem(R.id.navigation_petitions).setChecked(true);

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


    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainNavigationActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();

        Fragment fragment = getVisibleFragment();
        if (fragment!=null){
            switch (fragment.getTag()){
                case "petitionsFragment":
                    //Toast.makeText(this, "petitionsFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_petitions).setChecked(true);

                    break;
                case "newsFragment":
                    Toast.makeText(this, "newsFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_news).setChecked(true);

                    break;
                case "addNewPetitionFragment":
                    //Toast.makeText(this, "addNewPetitionFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_add_new).setChecked(true);

                    break;
                case "victoriesFragment":
                    //Toast.makeText(this, "victoriesFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_victories).setChecked(true);

                    break;
                case "profileFragment":
                    //Toast.makeText(this, "profileFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_profile).setChecked(true);

                    break;



            }

        }

    }

    private void initializeFragments() {
        //initializeFragments
        petitionsFragment = new PetitionsFragment();
        newsFragment = new NewsFragment();
        addNewPetitionFragment = new AddNewPetitionFragment();
        victoriesFragment = new VictoriesFragment();
        profileFragment = new ProfileFragment();
        //animateToFragment(petitionsFragment,"petitionsFragment");


        //animateToFragment(petitionsFragment,"petitionsFragment");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.mainContainer, petitionsFragment,"petitionsFragment");
        fragmentTransaction.add(R.id.mainContainer, newsFragment,"newsFragment");
        fragmentTransaction.add(R.id.mainContainer, addNewPetitionFragment, "addNewPetitionFragment");
        fragmentTransaction.add(R.id.mainContainer, victoriesFragment, "victoriesFragment");
        fragmentTransaction.add(R.id.mainContainer, profileFragment, "profileFragment");

        fragmentTransaction.hide(newsFragment);
        fragmentTransaction.hide(addNewPetitionFragment);
        fragmentTransaction.hide(victoriesFragment);
        fragmentTransaction.hide(profileFragment);


        fragmentTransaction.commit();
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
            case R.id.menu_add:
                // Action goes here
                startActivity(new Intent(MainNavigationActivity.this, NewPetitionActivity.class));
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

    public void replaceFragment(Fragment fragment, Fragment currentFragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.hide(petitionsFragment);
        fragmentTransaction.hide(newsFragment);
        fragmentTransaction.hide(addNewPetitionFragment);
        fragmentTransaction.hide(victoriesFragment);
        fragmentTransaction.hide(profileFragment);

        //fragmentTransaction.replace(R.id.mainContainer,fragment);
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();

    }


    /*
    private void animateToFragment(Fragment newFragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, newFragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.show(newFragment);

        if (newFragment == petitionsFragment){
            fragmentTransaction.hide(addNewPetitionFragment);
            fragmentTransaction.hide(newsFragment);
            fragmentTransaction.hide(profileFragment);
            fragmentTransaction.hide(victoriesFragment);
        }

        if (newFragment == addNewPetitionFragment ){
            fragmentTransaction.hide(petitionsFragment);
            fragmentTransaction.hide(newsFragment);
            fragmentTransaction.hide(profileFragment);
            fragmentTransaction.hide(victoriesFragment);
        }

        if (newFragment == newsFragment){
            fragmentTransaction.hide(addNewPetitionFragment);
            fragmentTransaction.hide( petitionsFragment);
            fragmentTransaction.hide(profileFragment);
            fragmentTransaction.hide(victoriesFragment);
        }

        if (newFragment == profileFragment){
            fragmentTransaction.hide(addNewPetitionFragment);
            fragmentTransaction.hide(newsFragment);
            fragmentTransaction.hide( petitionsFragment);
            fragmentTransaction.hide(victoriesFragment);
        }

        if (newFragment == victoriesFragment){
            fragmentTransaction.hide(addNewPetitionFragment);
            fragmentTransaction.hide(newsFragment);
            fragmentTransaction.hide(profileFragment);
            fragmentTransaction.hide( petitionsFragment);
        }

        fragmentTransaction.commit();

    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainNavigationActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment fragment = getVisibleFragment();
        if (fragment!=null){
            switch (fragment.getTag()){
                case "petitionsFragment":
                    //Toast.makeText(this, "petitionsFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_petitions).setChecked(true);

                    break;
                case "newsFragment":
                    Toast.makeText(this, "newsFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_news).setChecked(true);

                    break;
                case "addNewPetitionFragment":
                    //Toast.makeText(this, "addNewPetitionFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_add_new).setChecked(true);

                    break;
                case "victoriesFragment":
                    //Toast.makeText(this, "victoriesFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_victories).setChecked(true);

                    break;
                case "profileFragment":
                    //Toast.makeText(this, "profileFragment", Toast.LENGTH_SHORT).show();
                    navigation.getMenu().findItem(R.id.navigation_profile).setChecked(true);

                    break;



            }

        }else{
            finish();
        }

    }*/
}
