package com.trybe.project.petitionapp.views.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.trybe.project.petitionapp.adapters.AnnouncementRecyclerAdapter;
import com.trybe.project.petitionapp.models.AnnouncementModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {


    public static final int LIMIT = 7;
    private AnnouncementRecyclerAdapter adapter;

    private List<AnnouncementModel> announcementModelList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;
    private RecyclerView recyclerView;
    private ProgressBar progressBarProfile;
    private ConstraintLayout consLayout;
    private ImageView imageViewEmptyState;
    private TextView textViewPlaceholder;
    private RelativeLayout layourPlaceHolder;


    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_petitions, container, false);
        this.layourPlaceHolder = (RelativeLayout) view.findViewById(R.id.layourPlaceHolder);
        this.textViewPlaceholder = (TextView) view.findViewById(R.id.textViewPlaceholder);
        this.imageViewEmptyState = (ImageView) view.findViewById(R.id.imageViewEmptyState);
        this.consLayout = (ConstraintLayout) view.findViewById(R.id.consLayout);
        this.progressBarProfile = (ProgressBar) view.findViewById(R.id.progressBarProfile);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.petitionsRecyclerView);



        return view;

    }



    @Override
    public void onResume() {
        super.onResume();
        announcementModelList = new ArrayList<>();
        adapter = new AnnouncementRecyclerAdapter(announcementModelList, getActivity());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                                        //Log.e("Announcement ID", announcementPostId);
                                                        AnnouncementModel petitionModel = doc.getDocument().toObject(AnnouncementModel.class).withId(announcementPostId);
                                                        if (isFirstPageFirstLoad) {
                                                            announcementModelList.add(petitionModel);
                                                        } else {
                                                            announcementModelList.add(0, petitionModel);
                                                        }
                                                        adapter.notifyDataSetChanged();
                                                        checkForEmptyView();
                                                    }
                                                } else {
                                                    //Log.e("Announcement", "Not Exist");
                                                    checkForEmptyView();
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
                        checkForEmptyView();
                    }
                }
            });
        }
    }

    public void loadMorePetitions() {
        Query nextQuery = firebaseFirestore.collection("Petitions")
                .orderBy("petition_timestamp", Query.Direction.DESCENDING)
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
                                firebaseFirestore.collection("Petitions/" + petitionPostId + "/Announcements").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                        if (queryDocumentSnapshots != null) {
                                            if (!queryDocumentSnapshots.isEmpty()) {

                                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                                    String announcementPostId = doc.getDocument().getId();
                                                    //Log.e("Announcement ID", announcementPostId);
                                                    AnnouncementModel petitionModel = doc.getDocument().toObject(AnnouncementModel.class).withId(announcementPostId);
                                                    announcementModelList.add(petitionModel);
                                                    adapter.notifyDataSetChanged();

                                                }
                                            } else {
                                                //Log.e("Announcement", "Not Exist");

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

    private void checkForEmptyView() {
        if (adapter.getItemCount() == 0) {
            //Toast.makeText(getActivity(), "NO Data Found", Toast.LENGTH_SHORT).show();
            if (announcementModelList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                layourPlaceHolder.setVisibility(View.VISIBLE);
                imageViewEmptyState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().recreate();

                        //Toast.makeText(getActivity(), "NO Data Found On Press", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                layourPlaceHolder.setVisibility(View.GONE);
            }
        }
    }


}
