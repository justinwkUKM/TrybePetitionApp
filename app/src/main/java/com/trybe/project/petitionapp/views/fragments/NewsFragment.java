package com.trybe.project.petitionapp.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.trybe.project.petitionapp.adapters.AnnouncementRecyclerAdapter;
import com.trybe.project.petitionapp.models.AnnouncementModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    public static final int LIMIT = 5;
    private RecyclerView announcementListView;
    private AnnouncementRecyclerAdapter announcementRecyclerAdapter;

    private List<AnnouncementModel> announcementModelList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;
    private RecyclerView announcementRecyclerView;
    private android.widget.Button buttonRefresh;
    private android.widget.TextView textViewPlaceholder;
    private android.widget.RelativeLayout layourPlaceHolder;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        this.layourPlaceHolder = (RelativeLayout) view.findViewById(R.id.layourPlaceHolder);
        this.textViewPlaceholder = (TextView) view.findViewById(R.id.textViewPlaceholder);
        this.buttonRefresh = (Button) view.findViewById(R.id.buttonRefresh);
        this.announcementRecyclerView = (RecyclerView) view.findViewById(R.id.announcementRecyclerView);
        announcementModelList = new ArrayList<>();
        announcementListView = view.findViewById(R.id.announcementRecyclerView);
        announcementRecyclerAdapter = new AnnouncementRecyclerAdapter(announcementModelList, getActivity());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
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
                                                    //checkForEmptyView();

                                                }
                                            }

                                        }
                                    });

                                } else {

                                }
                            }
                            isFirstPageFirstLoad = false;
                        } else {
                            checkForEmptyView();
                        }


                    } else {
                        checkForEmptyView();
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

    private void checkForEmptyView() {
        if (announcementRecyclerAdapter.getItemCount() == 0) {
            if (announcementModelList.isEmpty()) {
                announcementListView.setVisibility(View.GONE);
                layourPlaceHolder.setVisibility(View.VISIBLE);
                buttonRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().recreate();

                    }
                });
            } else {
                announcementListView.setVisibility(View.VISIBLE);
                layourPlaceHolder.setVisibility(View.GONE);
            }
        }
    }
}
