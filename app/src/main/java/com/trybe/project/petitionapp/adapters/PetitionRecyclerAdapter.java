package com.trybe.project.petitionapp.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.trybe.project.petitionapp.R;
import com.trybe.project.petitionapp.models.PetitionModel;
import com.trybe.project.petitionapp.views.activities.MainNavigationActivity;
import com.trybe.project.petitionapp.views.activities.NewAnnouncementActivity;
import com.trybe.project.petitionapp.views.activities.NewPetitionActivity;
import com.trybe.project.petitionapp.views.activities.PetitionDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Waqas Khalid Obeidy on 29/3/2018.
 */

public class PetitionRecyclerAdapter extends RecyclerView.Adapter<PetitionRecyclerAdapter.ViewHolder> {

    private static final String TAG = PetitionRecyclerAdapter.class.getSimpleName();
    private List<PetitionModel> petitionModels;
    private Activity mContext;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private int signatureSize=0;
    private int announcementSize=0;
    private String currentUser;

    public PetitionRecyclerAdapter(List<PetitionModel> petitionModelList, Activity context) {
        this.petitionModels = petitionModelList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.petition_list_item, parent, false);
        //this.mContext = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //holder.setIsRecyclable(false);

        final String stPetitionTitle, stPetitionDesc, stPetitionSupporters, image_url, thumb_url, user_id, start_date, end_date;
        final String[] user_profile_image = new String[1];
        final String stCurrentUserId, stPetitionPostId;
        if (petitionModels != null) {
            //long milliSeconds = announcementModels.get(position).getPetition_timestamp().getTime();
            //String dateString = DateFormat.format("dd/MM/yyyy", new Date(milliSeconds)).toString();
            //Log.w(TAG, dateString);
            stCurrentUserId = firebaseAuth.getCurrentUser().getUid();
            stPetitionPostId = petitionModels.get(position).PetitionPostId;
            stPetitionTitle = petitionModels.get(position).getPetition_title();
            stPetitionDesc = petitionModels.get(position).getPetition_desc();
            stPetitionSupporters = petitionModels.get(position).getPetition_target_supporters();
            image_url = petitionModels.get(position).getPetition_cover_image_url();
            thumb_url = petitionModels.get(position).getPetition_cover_image_thumb_url();
            start_date = petitionModels.get(position).getPetition_start_date();
            end_date = petitionModels.get(position).getPetition_stop_date();


            holder.tvPetitionTitle.setText(stPetitionTitle);
            holder.tvPetitionDesc.setText(stPetitionDesc);

            final int progress = 0;
            final int totalSupporters = Integer.parseInt(stPetitionSupporters);
            holder.supportersProgressBar.setMax(totalSupporters);
            holder.supportersProgressBar.setProgress(progress);
            holder.tvPetitionSupporters.setText(progress + " of " + stPetitionSupporters + " have signed!");
            holder.btSignPetition.setBackground(mContext.getResources().getDrawable(R.drawable.sign_petition_button_bg));
            holder.btSignPetition.setTextColor(mContext.getResources().getColor(R.color.colorAccent2));
            holder.btSignPetition.setText("Sign Now!");


            final RequestOptions placeHolderRequest = new RequestOptions();
            placeHolderRequest.placeholder(R.drawable.com_facebook_profile_picture_blank_square);
            Glide.with(mContext).setDefaultRequestOptions(placeHolderRequest).load(image_url).thumbnail(Glide.with(mContext).load(thumb_url)).into(holder.petionCoverImageView);

            user_id = petitionModels.get(position).getPetition_author();
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String user_name = task.getResult().getString("user_name");
                        user_profile_image[0] = task.getResult().getString("user_profile_image");
                        Glide.with(mContext).setDefaultRequestOptions(placeHolderRequest).load(user_profile_image[0]).into(holder.profileImage);

                    } else {

                        String error = task.getException().getMessage();
                        //Log.e(TAG, error);
                        //Toast.makeText(AccountSetupActivity.this, "FireStore error "+error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Announcement petitions support count
            firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Announcements").addSnapshotListener((Activity) mContext, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {



                            int size = queryDocumentSnapshots.size();

                            announcementSize = size;



                        } else {

                            int size = queryDocumentSnapshots.size();

                            announcementSize = size;
                        }
                    }else {

                    }



                }
            });

            //Signed petitions support count
            firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").addSnapshotListener((Activity) mContext, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {


                            holder.supportersProgressBar.setMax(totalSupporters);
                            int size = queryDocumentSnapshots.size();

                            signatureSize = size;

                            if (size > totalSupporters-1) {
                                //TODO:Fix This
                                saveToFirestore(Uri.parse(image_url), thumb_url, user_id, stPetitionTitle, stPetitionDesc, stPetitionSupporters, stPetitionPostId, start_date, end_date);
                            }else {
                                delFromFirebase(stPetitionPostId,"Victories");

                            }
                            holder.supportersProgressBar.setProgress(size);
                            holder.tvPetitionSupporters.setText(size + " of " + totalSupporters + " supporters have Signed this petition");

                        } else {
                            holder.supportersProgressBar.setMax(totalSupporters);
                            int size = queryDocumentSnapshots.size();
                            if (size<totalSupporters){
                                delFromFirebase(stPetitionPostId,"Victories");
                            }
                            size = 0;
                            holder.supportersProgressBar.setProgress(size);
                            holder.tvPetitionSupporters.setText(size + " of " + totalSupporters + " supporters have Signed this petition");
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
                            holder.btSignPetition.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                            holder.btSignPetition.setTextColor(mContext.getResources().getColor(android.R.color.white));
                            holder.btSignPetition.setText("Signed!");
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


            holder.btSignPetition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.btSignPetition.setEnabled(false);
                    firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").document(stCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().exists()) {

                                    Map<String, Object> signatureMap = new HashMap<>();
                                    signatureMap.put("timestamp", FieldValue.serverTimestamp());
                                    signatureMap.put("user_name", firebaseAuth.getCurrentUser().getDisplayName());
                                    signatureMap.put("user_id", firebaseAuth.getCurrentUser().getUid());
                                    signatureMap.put("user_profile_image_url", user_profile_image[0]);

                                    firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").document(stCurrentUserId).set(signatureMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                //holder.btLikeAnnouncement.setEnabled(false);
                                                holder.btSignPetition.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                                                holder.btSignPetition.setTextColor(mContext.getResources().getColor(android.R.color.white));
                                                holder.btSignPetition.setText("Signed!");
                                            } else {
                                                String error = task.getException().getMessage();
                                                //Log.e(TAG, error);
                                            }
                                        }
                                    });

                                } else {
                                    delFromFirebase(stCurrentUserId, "Petitions/" + stPetitionPostId + "/Signatures");
                                    //firebaseFirestore.collection("Petitions/" + stPetitionPostId + "/Signatures").document(stCurrentUserId).delete();
                                    holder.btSignPetition.setBackground(mContext.getResources().getDrawable(R.drawable.sign_petition_button_bg));
                                    holder.btSignPetition.setTextColor(mContext.getResources().getColor(R.color.colorAccent2));
                                    holder.btSignPetition.setText("Sign Now!");
                                    //  Toast.makeText(mContext, "Data Doesnt Exist", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                String error = task.getException().getMessage();
                                //Log.e(TAG, error);
                            }

                            //
                            holder.btSignPetition.setEnabled(true);
                        }
                    });

                }
            });

                holder.btCreateAnnouncement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (stPetitionPostId.isEmpty() || stPetitionPostId == null) {
                            Log.w(TAG, stPetitionPostId);
                        } else {
                            Intent i = new Intent(mContext, NewAnnouncementActivity.class);
                            Log.w(TAG, stPetitionPostId);
                            i.putExtra("stPetitionPostId", stPetitionPostId);
                            mContext.startActivity(i);
                        }

                    }
                });


            if (user_id.equals(currentUser)){
                holder.btCreateAnnouncement.setVisibility(View.VISIBLE);

            }   else{

                holder.btCreateAnnouncement.setVisibility(View.INVISIBLE);

            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext.getApplicationContext(), PetitionDetails.class);

                    Pair[] pairs = new Pair[5];
                    pairs[0] = new Pair<View, String>(holder.profileImage, "userProfileImage");
                    pairs[1] = new Pair<View, String>(holder.supportersProgressBar, "petitionProgressBar");
                    pairs[2] = new Pair<View, String>(holder.tvPetitionDesc, "petitionDescription");
                    pairs[3] = new Pair<View, String>(holder.petionCoverImageView, "petitionCoverImage");
                    pairs[4] = new Pair<View, String>(holder.btSignPetition, "petitionSignInButton");

                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(mContext, pairs);

                    Bundle bundle = new Bundle();
                    bundle.putInt("announcementSize",announcementSize);
                    bundle.putInt("signatureSize",signatureSize);
                    bundle.putString("stPetitionPostId",stPetitionPostId);
                    bundle.putParcelable("parcel",petitionModels.get(position));
                    i.putExtras(bundle);




                    mContext.startActivity(i,activityOptions.toBundle());
                }
            });
        }
    }

    private void delFromFirebase(String stPetitionPostId, String path) {
        firebaseFirestore.collection(path).document(stPetitionPostId).delete();
    }

    @Override
    public int getItemCount() {
        return petitionModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPetitionTitle, tvPetitionDesc, tvPetitionSupporters;
        public ProgressBar supportersProgressBar;
        public ImageView petionCoverImageView;
        public Button btSignPetition;
        public CircleImageView profileImage;
        private ImageButton btCreateAnnouncement;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPetitionTitle = itemView.findViewById(R.id.tvPetitionTitle);
            tvPetitionDesc = itemView.findViewById(R.id.tvPetitionDesc);
            tvPetitionSupporters = itemView.findViewById(R.id.tvNoOfSupporters);
            supportersProgressBar = itemView.findViewById(R.id.progressBarSupportersCount);
            petionCoverImageView = itemView.findViewById(R.id.imageViewPetitionCover);
            btSignPetition = itemView.findViewById(R.id.btSignPetition);
            profileImage = itemView.findViewById(R.id.profile_image_petitions);
            btCreateAnnouncement = itemView.findViewById(R.id.btCreateAnnouncement);
        }
    }

    private void saveToFirestore(Uri parse, String thumb_url, String user_id, String stPetitionTitle, String stPetitionDesc, String stPetitionSupporters, final String stPetitionPostId, String start_date, String end_date) {
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
                                            Toast.makeText(mContext, "v_Petition Added", Toast.LENGTH_SHORT).show();
                                        } else {
                                            String error = task.getException().getMessage();
                                            //Log.e(TAG, error);
                                            Toast.makeText(mContext, "v_victory_petition_FireStore error " + error, Toast.LENGTH_SHORT).show();
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
