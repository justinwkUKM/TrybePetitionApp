package com.trybe.project.petitionapp.views.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trybe.project.petitionapp.R;
import com.trybe.project.petitionapp.models.PetitionModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetitionDetails extends BaseActivity {

    private static final String TAG = PetitionDetails.class.getSimpleName();
    Toolbar toolbar;
    private android.widget.ImageView imageView;
    private android.support.design.widget.CollapsingToolbarLayout collapsingtoolbar;
    private android.support.design.widget.AppBarLayout appbar;
    private android.widget.TextView tvPetitionTitle;
    private de.hdodenhof.circleimageview.CircleImageView profileimagepetitions;
    private android.widget.TextView tvPetitionDesc;
    private android.widget.ProgressBar progressBarSupportersCount;
    private android.widget.TextView tvNoOfSignatures;
    private android.widget.TextView tvNoOfTotalShares;
    private android.widget.TextView tvNoOfDaysLeft;
    private android.widget.TextView tvSupporters;
    private android.widget.TextView tvNoOfSupporters;
    private android.widget.TextView tvAnnouncements;
    private android.widget.TextView tvNoOfAnnouncements;
    private android.support.design.widget.CoordinatorLayout coordinator;
    String stPetitionPostId ="";
    private int signatureSize = 0;
    private int announcementSize = 0;private TextView tvHeader;
    private String user_id ="";
    private FirebaseFirestore firebaseFirestore;
    private String user_profile;
    private android.widget.Button btSignPetition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petition_details);
        this.btSignPetition = (Button) findViewById(R.id.btSignPetition);
        this.tvHeader = (TextView) findViewById(R.id.tvHeader);
        this.coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        this.tvNoOfAnnouncements = (TextView) findViewById(R.id.tvNoOfAnnouncements);
        this.tvAnnouncements = (TextView) findViewById(R.id.tvAnnouncements);
        this.tvNoOfSupporters = (TextView) findViewById(R.id.tvNoOfSupporters);
        this.tvSupporters = (TextView) findViewById(R.id.tvSupporters);
        this.tvNoOfDaysLeft = (TextView) findViewById(R.id.tvNoOfDaysLeft);
        this.tvNoOfTotalShares = (TextView) findViewById(R.id.tvNoOfTotalShares);
        this.tvNoOfSignatures = (TextView) findViewById(R.id.tvNoOfSignatures);
        this.progressBarSupportersCount = (ProgressBar) findViewById(R.id.progressBarSupportersCount);
        this.tvPetitionDesc = (TextView) findViewById(R.id.tvPetitionDesc);
        this.profileimagepetitions = (CircleImageView) findViewById(R.id.profile_image_petitions);
        this.tvPetitionTitle = (TextView) findViewById(R.id.tvPetitionTitle);
        this.appbar = (AppBarLayout) findViewById(R.id.app_bar);
        this.collapsingtoolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        PetitionModel petitionModel = bundle.getParcelable("parcel");
        stPetitionPostId = bundle.getString("stPetitionPostId");
        signatureSize = bundle.getInt("signatureSize");
        announcementSize = bundle.getInt("announcementSize");

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvHeader.setText(petitionModel.getPetition_title());
        tvPetitionDesc.setText(petitionModel.getPetition_desc());
        tvNoOfSignatures.setText(signatureSize +"\nof "+petitionModel.getPetition_target_supporters()+" Signatures");
        tvNoOfTotalShares.setText(signatureSize +"\nof "+petitionModel.getPetition_target_supporters()+" Signatures");
        tvNoOfDaysLeft.setText("42 \nDays Left");
        tvNoOfSupporters.setText(""+signatureSize);
        tvNoOfAnnouncements.setText(""+announcementSize);
        progressBarSupportersCount.setMax(signatureSize);
        progressBarSupportersCount.setProgress(signatureSize);


        user_id = petitionModel.getPetition_author();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {


                    String user_name = task.getResult().getString("user_name");
                    user_profile = task.getResult().getString("user_profile_image");

                    final RequestOptions placeHolderRequest = new RequestOptions();
                    placeHolderRequest.placeholder(R.drawable.com_facebook_profile_picture_blank_square);
                    Glide.with(getApplicationContext()).setDefaultRequestOptions(placeHolderRequest).load(user_profile).into(profileimagepetitions);

                } else {

                    String error = task.getException().getMessage();
                    Log.e(TAG, error);
                    //Toast.makeText(AccountSetupActivity.this, "FireStore error "+error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        final RequestOptions placeHolderRequest = new RequestOptions();
        placeHolderRequest.placeholder(R.drawable.com_facebook_profile_picture_blank_square);
        Glide.with(getApplicationContext()).setDefaultRequestOptions(placeHolderRequest).load(petitionModel.getPetition_cover_image_url()).into(imageView);
    }


}
