package com.trybe.project.petitionapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.trybe.project.petitionapp.AnnouncementModel;
import com.trybe.project.petitionapp.AnnouncementRecyclerAdapter;
import com.trybe.project.petitionapp.PetitionModel;
import com.trybe.project.petitionapp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    private RecyclerView announcementListView;
    private AnnouncementRecyclerAdapter announcementRecyclerAdapter;

    private List<AnnouncementModel> announcementModelList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        announcementModelList = new ArrayList<>();
        announcementListView = view.findViewById(R.id.announcementRecyclerView);
        announcementRecyclerAdapter = new AnnouncementRecyclerAdapter(announcementModelList, getActivity());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(container.getContext());
        announcementListView.setLayoutManager(mLayoutManager);

        announcementListView.setAdapter(announcementRecyclerAdapter);

        /*if (petitionRecyclerAdapter.getItemCount()==0){
            Toast.makeText(getActivity(), "No petitions exist yet", Toast.LENGTH_SHORT).show();
        }*/

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            announcementListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {
                        //Toast.makeText(getActivity(), "reached end", Toast.LENGTH_SHORT).show();
                        loadMoreAnnouncements();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Petitions").
                    orderBy("petition_timestamp", Query.Direction.DESCENDING).limit(2);

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

                                    firebaseFirestore.collection("Petitions/" + petitionPostId + "/Announcements").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                            if (queryDocumentSnapshots != null) {
                                                if (!queryDocumentSnapshots.isEmpty()) {

                                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                                        String announcementPostId = doc.getDocument().getId();
                                                        Log.e("Announcement ID", announcementPostId);
                                                        AnnouncementModel petitionModel = doc.getDocument().toObject(AnnouncementModel.class).withId(announcementPostId);
                                                            if (isFirstPageFirstLoad){
                                                                announcementModelList.add(petitionModel);
                                                            }else {
                                                                 announcementModelList.add(0,petitionModel);
                                                            }

                                                            announcementRecyclerAdapter.notifyDataSetChanged();

                                                    }
                                                } else {
                                                    Log.e("Announcement", "Not Exist");;
                                                }
                                            }

                                        }
                                    });

                                } else {

                                }
                            }
                            isFirstPageFirstLoad = false;
                        } else {
                        }

                    } else {

                    }

                }
            });
        }

        return view;

    }

    public void loadMoreAnnouncements() {
        Query nextQuery = firebaseFirestore.collection("Petitions")
                .orderBy("petition_timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(2);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String petitionPostId = doc.getDocument().getId();
                                firebaseFirestore.collection("Petitions/" + petitionPostId + "/Announcements").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                        if (queryDocumentSnapshots != null) {
                                            if (!queryDocumentSnapshots.isEmpty()) {

                                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                                    String announcementPostId = doc.getDocument().getId();
                                                    Log.e("Announcement ID", announcementPostId);
                                                    AnnouncementModel petitionModel = doc.getDocument().toObject(AnnouncementModel.class).withId(announcementPostId);
                                                    announcementModelList.add(petitionModel);
                                                    announcementRecyclerAdapter.notifyDataSetChanged();

                                                }
                                            } else {
                                                Log.e("Announcement", "Not Exist");;
                                            }
                                        }

                                    }
                                });


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