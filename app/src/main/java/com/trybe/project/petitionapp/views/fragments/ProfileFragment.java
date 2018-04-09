package com.trybe.project.petitionapp.views.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.trybe.project.petitionapp.R;
import com.trybe.project.petitionapp.adapters.PetitionRecyclerAdapter;
import com.trybe.project.petitionapp.models.PetitionModel;
import com.trybe.project.petitionapp.views.activities.AccountSetupActivity;
import com.trybe.project.petitionapp.views.activities.LoginActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.trybe.project.petitionapp.views.fragments.PetitionsFragment.LIMIT;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private de.hdodenhof.circleimageview.CircleImageView profileimage;
    private android.widget.TextView etNameMP;
    private android.widget.Button btEditAccountSettings;
    private android.widget.ProgressBar progressBarMyProfile;
    private android.support.v7.widget.RecyclerView petitionsListView;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String name;
    private String email;
    private String user_id;

    private PetitionRecyclerAdapter petitionRecyclerAdapter;
    private List<PetitionModel> petitionModelList;
    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.petitionsListView = (RecyclerView) view.findViewById(R.id.myPetitionsRecyclerView);
        this.progressBarMyProfile = (ProgressBar) view.findViewById(R.id.progressBarMyProfile);
        this.btEditAccountSettings = (Button) view.findViewById(R.id.btEditAccountSettings);
        this.etNameMP = (TextView) view.findViewById(R.id.etNameMP);
        this.profileimage = (CircleImageView) view.findViewById(R.id.profile_image);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        progressBarMyProfile.setVisibility(View.VISIBLE);
        btEditAccountSettings.setEnabled(false);

        retrieveFromFirestore();
        btEditAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AccountSetupActivity.class));
            }
        });
        petitionModelList = new ArrayList<>();
        petitionsListView = view.findViewById(R.id.myPetitionsRecyclerView);
        petitionRecyclerAdapter = new PetitionRecyclerAdapter(petitionModelList, getActivity());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(container.getContext());
        petitionsListView.setLayoutManager(mLayoutManager);

        petitionsListView.setAdapter(petitionRecyclerAdapter);

        /*if (petitionRecyclerAdapter.getItemCount()==0){
            Toast.makeText(getActivity(), "No petitions exist yet", Toast.LENGTH_SHORT).show();
        }*/

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            petitionsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {
                        //Toast.makeText(getActivity(), "reached end", Toast.LENGTH_SHORT).show();
                        loadMorePetitions();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Petitions").
                    orderBy("petition_timestamp", Query.Direction.DESCENDING).limit(LIMIT);

            //added getActivity as first parameter to bind this functionality to the lifecycle of the Activity
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            if (isFirstPageFirstLoad){
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            }

                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String petitionPostId = doc.getDocument().getId();

                                    if (doc.getDocument().getString("petition_author").equals(user_id)) {

                                        PetitionModel petitionModel = doc.getDocument().toObject(PetitionModel.class).withId(petitionPostId);
                                        if (isFirstPageFirstLoad) {
                                            petitionModelList.add(petitionModel);
                                        } else {
                                            petitionModelList.add(0, petitionModel);
                                        }

                                        petitionRecyclerAdapter.notifyDataSetChanged();

                                    }

                                } else {

                                }
                            } isFirstPageFirstLoad =false;
                        } else {
                        }

                    } else {

                    }

                }
            });
        }

        return view;

    }

    public void loadMorePetitions() {
        Query nextQuery = firebaseFirestore.collection("Petitions")
                .orderBy("petition_timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(LIMIT);

        nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String petitionPostId = doc.getDocument().getId();

                                if (doc.getDocument().getString("petition_author").equals(user_id)) {

                                    PetitionModel petitionModel = doc.getDocument().toObject(PetitionModel.class).withId(petitionPostId);

                                    petitionModelList.add(petitionModel);
                                    petitionRecyclerAdapter.notifyDataSetChanged();

                                }



                            } else {

                            }
                        }
                    }

                } else {

                }

            }
        });
    }

    private void retrieveFromFirestore() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        //Toast.makeText(AccountSetupActivity.this, "Data Exist", Toast.LENGTH_SHORT).show();
                        name = task.getResult().getString("user_name");
                        email = task.getResult().getString("user_email");
                        String image_url = task.getResult().getString("user_profile_image");
                        etNameMP.setText(name);
                        RequestOptions placeHolderRequest = new RequestOptions();
                        placeHolderRequest.placeholder(R.drawable.ic_email_black_24dp);
                        Glide.with(getActivity()).setDefaultRequestOptions(placeHolderRequest).load(image_url).into(profileimage);
                    } else {
                        Toast.makeText(getActivity(), "Data doesn't Exist", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    String error = task.getException().getMessage();
                    Log.e("Error", error);
                    //Toast.makeText(AccountSetupActivity.this, "firebaseFirestore error "+error, Toast.LENGTH_SHORT).show();
                }
                progressBarMyProfile.setVisibility(View.GONE);
                btEditAccountSettings.setEnabled(true);


            }
        });
    }

}

/*

if (doc.getDocument().getString("petition_author").equals(user_id)) {

        PetitionModel petitionModel = doc.getDocument().toObject(PetitionModel.class).withId(petitionPostId);
        if (isFirstPageFirstLoad) {
        petitionModelList.add(petitionModel);
        } else {
        petitionModelList.add(0, petitionModel);
        }

        petitionRecyclerAdapter.notifyDataSetChanged();

        }*/

/*
    private void retrieveFromFirestore() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        //Toast.makeText(AccountSetupActivity.this, "Data Exist", Toast.LENGTH_SHORT).show();
                        name = task.getResult().getString("user_name");
                        email = task.getResult().getString("user_email");
                        String image_url = task.getResult().getString("user_profile_image");
                        etNameMP.setText(name);
                        RequestOptions placeHolderRequest = new RequestOptions();
                        placeHolderRequest.placeholder(R.drawable.ic_email_black_24dp);
                        Glide.with(getActivity()).setDefaultRequestOptions(placeHolderRequest).load(image_url).into(profileimage);
                    } else {
                        Toast.makeText(getActivity(), "Data doesn't Exist", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    String error = task.getException().getMessage();
                    Log.e("Error", error);
                    //Toast.makeText(AccountSetupActivity.this, "firebaseFirestore error "+error, Toast.LENGTH_SHORT).show();
                }
                progressBarMyProfile.setVisibility(View.GONE);
                btEditAccountSettings.setEnabled(true);


            }
        });
    }*/
