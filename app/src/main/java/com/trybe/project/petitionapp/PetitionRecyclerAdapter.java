package com.trybe.project.petitionapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MyXLab on 26/3/2018.
 */

public class PetitionRecyclerAdapter extends RecyclerView.Adapter<PetitionRecyclerAdapter.ViewHolder> {

    private static final String TAG = PetitionRecyclerAdapter.class.getSimpleName();
    public List<PetitionModel> petitionModels;
    private Context mContext;
    private FirebaseFirestore firebaseFirestore;
    public PetitionRecyclerAdapter(List<PetitionModel> petitionModelList, Context context){
        this.petitionModels = petitionModelList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.petition_list_item, parent,false);
        //this.mContext = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String stPetitionTitle, stPetitionDesc, stPetitionSupporters,image_url, user_id;

        long milliSeconds = petitionModels.get(position).getPetition_timestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy", new Date(milliSeconds)).toString();
        Log.w(TAG,dateString);

        stPetitionTitle = petitionModels.get(position).getPetition_title();
        stPetitionDesc = petitionModels.get(position).getPetition_desc();
        stPetitionSupporters = petitionModels.get(position).getPetition_target_supporters();
        image_url = petitionModels.get(position).getPetition_cover_image_url();
        holder.tvPetitionTitle.setText(stPetitionTitle);
        holder.tvPetitionDesc.setText(stPetitionDesc);

        Random rn = new Random();
        int range = 6 - 2 + 1;
        int progress =  rn.nextInt(range) + 2;
        int totalSupporters = Integer.parseInt(stPetitionSupporters);
        holder.supportersProgressBar.setMax(totalSupporters);
        holder.supportersProgressBar.setProgress(totalSupporters/progress);
        holder.tvPetitionSupporters.setText(totalSupporters/progress+ " of "+stPetitionSupporters+" have signed!");

        final RequestOptions placeHolderRequest = new RequestOptions();
        placeHolderRequest.placeholder(R.drawable.com_facebook_profile_picture_blank_square);
        Glide.with(mContext).setDefaultRequestOptions(placeHolderRequest).load(image_url).into(holder.petionCoverImageView);

        user_id = petitionModels.get(position).getPetition_author();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String user_name = task.getResult().getString("user_name");
                    String user_profile_image = task.getResult().getString("user_profile_image");
                    Glide.with(mContext).setDefaultRequestOptions(placeHolderRequest).load(user_profile_image).into(holder.profileImage);

                }else{

                    String error = task.getException().getMessage();
                    Log.e(TAG,error);
                    //Toast.makeText(AccountSetupActivity.this, "FireStore error "+error, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        public ViewHolder(View itemView) {
            super(itemView);

            tvPetitionTitle = itemView.findViewById(R.id.tvPetitionTitle);
            tvPetitionDesc = itemView.findViewById(R.id.tvPetitionDesc);
            tvPetitionSupporters = itemView.findViewById(R.id.tvNoOfSupporters);
            supportersProgressBar = itemView.findViewById(R.id.progressBarSupportersCount);
            petionCoverImageView = itemView.findViewById(R.id.imageViewPetitionCover);
            btSignPetition = itemView.findViewById(R.id.btSignPetition);
            profileImage = itemView.findViewById(R.id.profile_image_petitions);

        }
    }
}
