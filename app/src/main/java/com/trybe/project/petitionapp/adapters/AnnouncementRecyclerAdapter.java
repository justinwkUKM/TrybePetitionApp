package com.trybe.project.petitionapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trybe.project.petitionapp.R;
import com.trybe.project.petitionapp.models.AnnouncementModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MyXLab on 30/3/2018.
 */

public class AnnouncementRecyclerAdapter extends RecyclerView.Adapter<AnnouncementRecyclerAdapter.ViewHolder> {

    private static final String TAG = AnnouncementRecyclerAdapter.class.getSimpleName();
    public List<AnnouncementModel> announcementModels;
    private Context mContext;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public AnnouncementRecyclerAdapter(List<AnnouncementModel> announcementModels, Activity context) {
        this.announcementModels = announcementModels;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_list_item, parent, false);
        //this.mContext = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String   stAnnouncementTitle, stAnnouncementDesc, image_url, thumb_url, user_id;
        final String  stCurrentUserId, stAnnouncementPostId;
        if (announcementModels != null) {
            //long milliSeconds = announcementModels.get(position).getAnnouncement_timestamp().getTime();
            //String dateString = DateFormat.format("dd/MM/yyyy", new Date(milliSeconds)).toString();
            //Log.w(TAG, dateString);
            stCurrentUserId = firebaseAuth.getCurrentUser().getUid();
            stAnnouncementPostId = announcementModels.get(position).getAnnouncement_petition_id();
            stAnnouncementTitle = announcementModels.get(position).getAnnouncement_title();
            stAnnouncementDesc = announcementModels.get(position).getAnnouncement_desc();
            image_url = announcementModels.get(position).getAnnouncement_cover_image_url();
            thumb_url = announcementModels.get(position).getAnnouncement_cover_image_thumb_url();
            holder.tvAnnouncementTitle.setText(stAnnouncementTitle);
            holder.tvAnnouncementDesc.setText(stAnnouncementDesc);

            final RequestOptions placeHolderRequest = new RequestOptions();
            placeHolderRequest.placeholder(R.drawable.com_facebook_profile_picture_blank_square);
            Glide.with(mContext).setDefaultRequestOptions(placeHolderRequest).load(image_url).thumbnail(Glide.with(mContext).load(thumb_url)).into(holder.AnnouncementCoverImageView);

            user_id = announcementModels.get(position).getAnnouncement_author();

            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    //TODO Fix This Error: Failed to get document because the client is offline
                    if (task.getResult().exists()){
                        if (task.isSuccessful()) {
                            String user_name = task.getResult().getString("user_name");
                            String user_profile_image = task.getResult().getString("user_profile_image");
                            Glide.with(mContext).setDefaultRequestOptions(placeHolderRequest).load(user_profile_image).into(holder.profileImage);

                        } else {

                            String error = task.getException().getMessage();
                            Log.e(TAG, error);
                            //Toast.makeText(AccountSetupActivity.this, "FireStore error "+error, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Log.e(TAG, "Document Doesn't Exist");
                    }

                }
            });

            /*//Signed petitions support count
            firebaseFirestore.collection("Petitions/" + stAnnouncementPostId + "/Announcements").addSnapshotListener((Activity) mContext, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {


                        } else {
                            //Toast.makeText(mContext, "Data Doesnt Exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            //Signed Announcement Feature

            firebaseFirestore.collection("Petitions/" + stAnnouncementPostId + "/Announcements").document(stCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {


                        } else {

                        }

                    } else {
                        String error = task.getException().getMessage();
                        Log.e(TAG, error);
                    }
                }
            });


            holder.btLikeAnnouncement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    firebaseFirestore.collection("Petitions/" + stAnnouncementPostId + "/Announcements").document(stCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().exists()) {

                                    Map<String, Object> signatureMap = new HashMap<>();
                                    signatureMap.put("timestamp", FieldValue.serverTimestamp());
                                    signatureMap.put("user_name", firebaseAuth.getCurrentUser().getDisplayName());
                                    firebaseFirestore.collection("Petitions/" + stAnnouncementPostId + "/Announcements").document(stCurrentUserId).set(signatureMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                //holder.btLikeAnnouncement.setEnabled(false);
                                                holder.btLikeAnnouncement.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                                                *//*holder.btLikeAnnouncement.setTextColor(mContext.getResources().getColor(android.R.color.white));
                                                holder.btLikeAnnouncement.setText("Signed!");*//*
                                            } else {
                                                String error = task.getException().getMessage();
                                                Log.e(TAG, error);
                                            }
                                        }
                                    });

                                } else {
                                    firebaseFirestore.collection("Petitions/" + stAnnouncementPostId + "/Announcements").document(stCurrentUserId).delete();
                                    holder.btLikeAnnouncement.setBackground(mContext.getResources().getDrawable(R.drawable.sign_petition_button_bg));
                                    *//*holder.btLikeAnnouncement.setTextColor(mContext.getResources().getColor(R.color.colorAccent2));
                                    holder.btLikeAnnouncement.setText("Unsigned!");*//*
                                    //  Toast.makeText(mContext, "Data Doesnt Exist", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                String error = task.getException().getMessage();
                                Log.e(TAG, error);
                            }
                        }
                    });

                }
            });

            holder.btReplyAnnouncement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user_id.isEmpty()||user_id == null){
                        Log.w(TAG, user_id);
                    }else {
                        Intent i = new Intent(mContext, NewAnnouncementActivity.class);
                        Log.w(TAG, user_id);
                        i.putExtra("stAnnouncementPostId", user_id);
                        mContext.startActivity(i);
                    }

                }
            });*/
        }



    }


    @Override
    public int getItemCount() {
        return announcementModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAnnouncementTitle, tvAnnouncementDesc;
        public ImageView AnnouncementCoverImageView;
        public ImageButton btLikeAnnouncement;
        public CircleImageView profileImage;
        private ImageButton btReplyAnnouncement;

        public ViewHolder(View itemView) {
            super(itemView);

            tvAnnouncementTitle = itemView.findViewById(R.id.tvAnnouncementTitle);
            tvAnnouncementDesc = itemView.findViewById(R.id.tvAnnouncementDesc);
            AnnouncementCoverImageView = itemView.findViewById(R.id.imageViewAnnouncementCover);
            btLikeAnnouncement = itemView.findViewById(R.id.btLikeAnnouncement);
            profileImage = itemView.findViewById(R.id.profile_image_Announcement);
            btReplyAnnouncement = itemView.findViewById(R.id.btReplyAnnouncement);
        }
    }
}
