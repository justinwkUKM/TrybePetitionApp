package com.trybe.project.petitionapp.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.trybe.project.petitionapp.models.PetitionModel;
import com.trybe.project.petitionapp.adapters.PetitionRecyclerAdapter;
import com.trybe.project.petitionapp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PetitionsFragment extends Fragment {

    public static final int LIMIT = 5;
    private RecyclerView petitionsListView;
    private PetitionRecyclerAdapter petitionRecyclerAdapter;

    private List<PetitionModel> petitionModelList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;

    public PetitionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_petitions, container, false);
        petitionModelList = new ArrayList<>();
        petitionsListView = view.findViewById(R.id.petitionsRecyclerView);
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

                                    /*firebaseFirestore.collection("Petitions/" + petitionPostId + "/Announcements").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                            if (queryDocumentSnapshots != null) {
                                                if (!queryDocumentSnapshots.isEmpty()) {

                                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                                        String announcementPostId = doc.getDocument().getId();
                                                        Log.e("Announcement ID", announcementPostId);


                                                    }
                                                } else {
                                                    Log.e("Announcement", "Not Exist");;
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

}
