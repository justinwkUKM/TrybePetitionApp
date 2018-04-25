package com.trybe.project.petitionapp.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.trybe.project.petitionapp.adapters.VictoryRecyclerAdapter;
import com.trybe.project.petitionapp.models.PetitionModel;
import com.trybe.project.petitionapp.models.VictoryModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Waqas Khalid Obeidy on 29/3/2018.
 */



public class VictoriesFragment extends Fragment {


    private Button buttonRefresh;
    private RelativeLayout layourPlaceHolder;

    public static final int LIMIT = 7;
    private RecyclerView petitionsListView;
    private VictoryRecyclerAdapter victoryRecyclerAdapter;

    private List<VictoryModel> victoryModelList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;

    public VictoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_victories, container, false);
        this.layourPlaceHolder = (RelativeLayout) view.findViewById(R.id.layourPlaceHolder);
        this.buttonRefresh = (Button) view.findViewById(R.id.buttonRefresh);
        victoryModelList = new ArrayList<>();
        petitionsListView = view.findViewById(R.id.myPetitionsRecyclerView);
        victoryRecyclerAdapter = new VictoryRecyclerAdapter(victoryModelList, getActivity());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        petitionsListView.setLayoutManager(mLayoutManager);

        petitionsListView.setAdapter(victoryRecyclerAdapter);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            petitionsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {
                        loadMorePetitions();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Victories").
                    orderBy("victory_petition_timestamp", Query.Direction.DESCENDING).limit(LIMIT);

            //added getActivity as first parameter to bind this functionality to the lifecycle of the Activity
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            if (isFirstPageFirstLoad) {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            }

                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String petitionPostId = doc.getDocument().getId();

                                    VictoryModel victoryModel = new VictoryModel();

                                    victoryModel.setPetition_author(doc.getDocument().getString("victory_petition_author"));
                                    victoryModel.setPetition_cover_image_url(doc.getDocument().getString("victory_petition_cover_image_url"));
                                    victoryModel.setPetition_cover_image_thumb_url(doc.getDocument().getString("victory_petition_cover_image_thumb_url"));
                                    victoryModel.setPetition_title(doc.getDocument().getString("victory_petition_title"));
                                    victoryModel.setPetition_desc(doc.getDocument().getString("victory_petition_desc"));
                                    victoryModel.setPetition_target_supporters(doc.getDocument().getString("victory_petition_target_supporters"));
                                    victoryModel.setPetition_start_date(doc.getDocument().getString("victory_petition_start_date"));
                                    victoryModel.setPetition_stop_date(doc.getDocument().getString("victory_petition_stop_date"));
                                    victoryModel.setPetition_timestamp(doc.getDocument().getDate("victory_petition_timestamp"));


                                    if (isFirstPageFirstLoad) {
                                        victoryModelList.add(victoryModel);
                                    } else {
                                        victoryModelList.add(0, victoryModel);
                                    }

                                    victoryRecyclerAdapter.notifyDataSetChanged();

                                } else {

                                }
                                checkForEmptyView();
                            }
                            isFirstPageFirstLoad = false;
                        } else {
                        victoryModelList.clear();
                        checkForEmptyView();
                        }

                    } else {

                    }
                    checkForEmptyView();
                }
            });
        }

        return view;

    }

    public void loadMorePetitions() {
        Query nextQuery = firebaseFirestore.collection("Victories")
                .orderBy("victory_petition_timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(LIMIT);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String petitionPostId = doc.getDocument().getId();
                                VictoryModel victoryModel = doc.getDocument().toObject(VictoryModel.class).withId(petitionPostId);

                                victoryModelList.add(victoryModel);
                                victoryRecyclerAdapter.notifyDataSetChanged();

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
        if (victoryRecyclerAdapter.getItemCount() == 0) {
            if (victoryModelList.isEmpty()) {
                petitionsListView.setVisibility(View.GONE);
                layourPlaceHolder.setVisibility(View.VISIBLE);
                buttonRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //getActivity().recreate();
                        Toast.makeText(getActivity(), "No Victories Yet", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                petitionsListView.setVisibility(View.VISIBLE);
                layourPlaceHolder.setVisibility(View.GONE);
            }
        }
    }

}

