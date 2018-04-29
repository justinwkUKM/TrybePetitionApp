package com.trybe.project.petitionapp.views.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.trybe.project.petitionapp.R;
import com.trybe.project.petitionapp.adapters.PetitionRecyclerAdapter;
import com.trybe.project.petitionapp.models.PetitionModel;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Waqas Khalid Obeidy on 29/3/2018.
 */



public class PetitionsFragment extends Fragment {

    public static final int LIMIT = 7;
    private PetitionRecyclerAdapter petitionRecyclerAdapter;

    private List<PetitionModel> petitionModelList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;
    private RecyclerView petitionsRecyclerView;
    private android.widget.TextView textViewPlaceholder;
    private android.widget.RelativeLayout layourPlaceHolder;
    private android.widget.ProgressBar progressBarMyProfile;
    private android.support.constraint.ConstraintLayout consLayout;
    private android.widget.ImageView imageViewEmptyState;

    public PetitionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_petitions, container, false);
        this.imageViewEmptyState = (ImageView) view.findViewById(R.id.imageViewEmptyState);
        this.consLayout = (ConstraintLayout) view.findViewById(R.id.consLayout);
        this.progressBarMyProfile = (ProgressBar) view.findViewById(R.id.progressBarMyProfile);
        this.layourPlaceHolder = (RelativeLayout) view.findViewById(R.id.layourPlaceHolder);
        this.textViewPlaceholder = (TextView) view.findViewById(R.id.textViewPlaceholder);
        this.petitionsRecyclerView = (RecyclerView) view.findViewById(R.id.petitionsRecyclerView);

        petitionModelList = new ArrayList<>();
        petitionRecyclerAdapter = new PetitionRecyclerAdapter(petitionModelList, getActivity());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        petitionsRecyclerView.setLayoutManager(mLayoutManager);

        petitionsRecyclerView.setAdapter(petitionRecyclerAdapter);

        /*if (petitionRecyclerAdapter.getItemCount()==0){
            Toast.makeText(getActivity(), "No petitions exist yet", Toast.LENGTH_SHORT).show();
        }*/

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            petitionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                                    /*firebaseFirestore.collection("Petitions/" + petitionPostId + "/Announcements").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                            if (queryDocumentSnapshots != null) {
                                                if (!queryDocumentSnapshots.isEmpty()) {

                                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                                        String announcementPostId = doc.getDocument().getId();
                                                        //Log.e("Announcement ID", announcementPostId);


                                                    }
                                                } else {
                                                    //Log.e("Announcement", "Not Exist");;
                                                }
                                            }

                                        }
                                    });*/


                                    PetitionModel petitionModel = doc.getDocument().toObject(PetitionModel.class).withId(petitionPostId);
                                    if (isFirstPageFirstLoad){
                                        petitionModelList.add(petitionModel);
                                    }else {
                                        petitionModelList.add(0,petitionModel);
                                    }

                                    petitionRecyclerAdapter.notifyDataSetChanged();

                                } else {

                                }
                            } isFirstPageFirstLoad =false;
                        } else {
                        }

                    } else {
                    }
                    checkForEmptyView();
                }
            });
        }

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        petitionRecyclerAdapter.notifyDataSetChanged();
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
                                PetitionModel petitionModel = doc.getDocument().toObject(PetitionModel.class).withId(petitionPostId);

                                petitionModelList.add(petitionModel);
                                petitionRecyclerAdapter.notifyDataSetChanged();

                            } else {

                            }
                        }
                    }

                } else {

                }

            }
        });
    }

    private void checkForEmptyView() {
        if (petitionRecyclerAdapter.getItemCount() == 0) {
            //Toast.makeText(getActivity(), "NO Data Found", Toast.LENGTH_SHORT).show();
            if (petitionModelList.isEmpty()) {
                petitionsRecyclerView.setVisibility(View.GONE);
                layourPlaceHolder.setVisibility(View.VISIBLE);
                imageViewEmptyState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().recreate();

                        //Toast.makeText(getActivity(), "NO Data Found On Press", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                petitionsRecyclerView.setVisibility(View.VISIBLE);
                layourPlaceHolder.setVisibility(View.GONE);
            }
        }
    }

}
