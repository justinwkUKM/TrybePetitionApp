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
 * Created by Waqas Khalid Obeidy on 30/3/2018.
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
                            //Log.e(TAG, error);
                            //Toast.makeText(AccountSetupActivity.this, "FireStore error "+error, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //Log.e(TAG, "Document Doesn't Exist");
                    }

                }
            });
        }



    }


    @Override
    public int getItemCount() {
        return announcementModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAnnouncementTitle, tvAnnouncementDesc;
        public ImageView AnnouncementCoverImageView;
        public CircleImageView profileImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvAnnouncementTitle = itemView.findViewById(R.id.tvAnnouncementTitle);
            tvAnnouncementDesc = itemView.findViewById(R.id.tvAnnouncementDesc);
            AnnouncementCoverImageView = itemView.findViewById(R.id.imageViewAnnouncementCover);
            profileImage = itemView.findViewById(R.id.profile_image_Announcement);
        }
    }
}
