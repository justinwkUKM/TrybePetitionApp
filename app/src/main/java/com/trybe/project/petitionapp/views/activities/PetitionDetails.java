package com.trybe.project.petitionapp.views.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.trybe.project.petitionapp.R;
import com.trybe.project.petitionapp.models.PetitionModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Waqas Khalid Obeidy on 18/4/2018.
 */
public class PetitionDetails extends BaseActivity {

    private static final String TAG = PetitionDetails.class.getSimpleName();
    Toolbar toolbar;
    private android.widget.ImageView imageView;
    private android.support.design.widget.CollapsingToolbarLayout collapsingtoolbar;
    private android.support.design.widget.AppBarLayout appbar;
    private android.widget.TextView tvPetitionAuthor;
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
    private int announcementSize = 0;
    private TextView tvHeader;
    private String petition_user_id ="";
    private FirebaseFirestore firebaseFirestore;
    private String user_profile;
    private android.widget.Button btSignPetition;
    FirebaseAuth firebaseAuth;
    int totalSupporters ;
    private String stCurrentUserId;


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
        this.tvPetitionAuthor = (TextView) findViewById(R.id.tvPetitionAuthor);
        this.appbar = (AppBarLayout) findViewById(R.id.app_bar);
        this.collapsingtoolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        stCurrentUserId = firebaseAuth.getCurrentUser().getUid();

        Bundle bundle = getIntent().getExtras();
        final PetitionModel petitionModel = bundle.getParcelable("parcel");
        stPetitionPostId = bundle.getString("stPetitionPostId");
        signatureSize = bundle.getInt("signatureSize");
        announcementSize = bundle.getInt("announcementSize");
        totalSupporters = Integer.parseInt(petitionModel.getPetition_target_supporters());

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvHeader.setText(petitionModel.getPetition_title());
        tvPetitionDesc.setText(petitionModel.getPetition_desc());
        //tvNoOfSignatures.setText(signatureSize + "\nof " + petitionModel.getPetition_target_supporters() + " Signatures");
        tvNoOfTotalShares.setText(0 + " Shares");

        Random ran = new Random();
        int x = ran.nextInt(9) + 12;

        tvNoOfDaysLeft.setText(x + "\nDays Left");
        progressBarSupportersCount.setMax(signatureSize);
        progressBarSupportersCount.setProgress(signatureSize);

        petition_user_id = petitionModel.getPetition_author();
        firebaseFirestore.collection("Users").document(petition_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String user_name = task.getResult().getString("user_name");
                    user_profile = task.getResult().getString("user_profile_image");


                    if (!petition_user_id.equals(firebaseAuth.getCurrentUser().getUid())) {
                        tvPetitionAuthor.setText(user_name);
                    }
                    final RequestOptions placeHolderRequest = new RequestOptions();
                    placeHolderRequest.placeholder(R.drawable.com_facebook_profile_picture_blank_square);
                    Glide.with(getApplicationContext()).setDefaultRequestOptions(placeHolderRequest).load(user_profile).into(profileimagepetitions);
                } else {
                    String error = task.getException().getMessage();
                    //Log.e(TAG, error);
                    //Toast.makeText(AccountSetupActivity.this, "FireStore error "+error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        final RequestOptions placeHolderRequest = new RequestOptions();
        placeHolderRequest.placeholder(R.drawable.com_facebook_profile_picture_blank_square);
        Glide.with(getApplicationContext()).setDefaultRequestOptions(placeHolderRequest).load(petitionModel.getPetition_cover_image_url()).into(imageView);






        //Signed petitions support count
        firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").addSnapshotListener((PetitionDetails.this), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {


                        progressBarSupportersCount.setMax(totalSupporters);
                        int size = queryDocumentSnapshots.size();

                        signatureSize = size;

                        if (size > totalSupporters-1) {
                            //TODO:Fix This
                            //saveToFirestore(Uri.parse(image_url), thumb_url, petition_user_id, stPetitionTitle, stPetitionDesc, stPetitionSupporters, stPetitionPostId, start_date, end_date);
                        }else {
                            //delFromFirebase(stPetitionPostId,"Victories");
                        }




                        progressBarSupportersCount.setProgress(size);
                        tvNoOfSignatures.setText(size + "\nof " + petitionModel.getPetition_target_supporters() + " Signatures");
                        tvNoOfSupporters.setText("" + size);


                    } else {
                        progressBarSupportersCount.setMax(totalSupporters);
                        int size = queryDocumentSnapshots.size();
                        if (size<totalSupporters){
                            delFromFirebase(stPetitionPostId,"Victories");
                        }
                        size = 0;
                        progressBarSupportersCount.setProgress(size);
                        tvNoOfSignatures.setText(size + "\nof " + petitionModel.getPetition_target_supporters() + " Signatures");
                        tvNoOfSupporters.setText("" + size);

                    }
                }

            }
        });


        //Signed petitions support count
        firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Announcements").addSnapshotListener((PetitionDetails.this), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {


                        int size = queryDocumentSnapshots.size();

                        announcementSize = size;


                        tvNoOfAnnouncements.setText("" + announcementSize);



                    } else {
                        progressBarSupportersCount.setMax(totalSupporters);
                        int size = queryDocumentSnapshots.size();
                        if (size<totalSupporters){
                            delFromFirebase(stPetitionPostId,"Victories");
                        }
                        size = 0;
                        progressBarSupportersCount.setProgress(size);
                        tvNoOfAnnouncements.setText("" + size);
                    }
                }

            }
        });

//Signed Petition Feature
        firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").document(stCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        //holder.btLikeAnnouncement.setEnabled(false);
                        btSignPetition.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        btSignPetition.setTextColor(getResources().getColor(android.R.color.white));
                        btSignPetition.setText("Signed!");
                        // Toast.makeText(mContext, "Data Exist", Toast.LENGTH_SHORT).show();
                    } else {

                        //  Toast.makeText(mContext, "Data Doesnt Exist", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String error = task.getException().getMessage();
                    //Log.e(TAG, error);
                }
            }
        });


        btSignPetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btSignPetition.setEnabled(false);
                firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").document(stCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {

                                Map<String, Object> signatureMap = new HashMap<>();
                                signatureMap.put("timestamp", FieldValue.serverTimestamp());
                                signatureMap.put("user_name", firebaseAuth.getCurrentUser().getDisplayName());
                                signatureMap.put("petition_user_id", firebaseAuth.getCurrentUser().getUid());
                                signatureMap.put("user_profile_image_url", user_profile);

                                firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").document(stCurrentUserId).set(signatureMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            btSignPetition.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                            btSignPetition.setTextColor(getResources().getColor(android.R.color.white));
                                            btSignPetition.setText("Signed!");
                                        } else {
                                            String error = task.getException().getMessage();
                                            //Log.e(TAG, error);
                                        }
                                    }
                                });

                            } else {
                                delFromFirebase(stCurrentUserId, "Petitions/" + stPetitionPostId + "/Signatures");
                                //firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").document(stCurrentUserId).delete();
                                btSignPetition.setBackground(getResources().getDrawable(R.drawable.sign_petition_button_bg));
                                btSignPetition.setTextColor(getResources().getColor(R.color.colorAccent2));
                                btSignPetition.setText("Sign Now!");
                                //  Toast.makeText(mContext, "Data Doesnt Exist", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            String error = task.getException().getMessage();
                            //Log.e(TAG, error);
                        }

                        //
                        btSignPetition.setEnabled(true);
                    }
                });

            }
        });


    }

    private void delFromFirebase(String stPetitionPostId, String path) {
        firebaseFirestore.collection(path).document(stPetitionPostId).delete();
    }


    private void saveToFirestore(Uri parse, final String thumb_url, String user_id, String stPetitionTitle, String stPetitionDesc, String stPetitionSupporters, final String stPetitionPostId, String start_date, String end_date) {
        final Map<String, Object> petitionMap = new HashMap<>();
        petitionMap.put("victory_petition_cover_image_url", parse.toString());
        petitionMap.put("victory_petition_cover_image_thumb_url", thumb_url);
        petitionMap.put("victory_petition_title", stPetitionTitle);
        petitionMap.put("victory_petition_desc", stPetitionDesc);
        petitionMap.put("victory_petition_target_supporters", stPetitionSupporters);
        petitionMap.put("victory_petition_post_id", stPetitionPostId);
        petitionMap.put("victory_petition_start_date", start_date);
        petitionMap.put("victory_petition_timestamp", FieldValue.serverTimestamp());
        petitionMap.put("victory_petition_stop_date", end_date);
        petitionMap.put("victory_petition_author", user_id);

        firebaseFirestore.collection("Victories").document(stPetitionPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        //Log.e(TAG, "exists");
                    } else {
                        //Log.e(TAG, "x exists");
                        firebaseFirestore.collection("Victories").document(stPetitionPostId).set(petitionMap).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PetitionDetails.this, "v_Petition Added", Toast.LENGTH_SHORT).show();
                                        } else {
                                            String error = task.getException().getMessage();
                                            //Log.e(TAG, error);
                                            Toast.makeText(PetitionDetails.this, "v_victory_petition_FireStore error " + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    String error = task.getException().getMessage();
                    //Log.e(TAG, error);
                }
            }
        });
    }

}
