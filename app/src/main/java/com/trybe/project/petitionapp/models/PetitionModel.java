package com.trybe.project.petitionapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Waqas Khalid Obeidy on 26/3/2018.
 */

public class PetitionModel extends PetitionPostId implements Parcelable {
    private String petition_author, petition_cover_image_url, petition_cover_image_thumb_url,
            petition_title, petition_desc, petition_target_supporters, petition_start_date,
            petition_stop_date;
    private Date petition_timestamp;
    private AnnouncementModel announcementModel;

    public PetitionModel() {
    }

    public PetitionModel(String petition_author, String petition_cover_image_url, String petition_cover_image_thumb_url, String petition_title, String petition_desc, String petition_target_supporters, String petition_start_date, String petition_stop_date, Date petition_timestamp, AnnouncementModel announcementModel) {
        this.petition_author = petition_author;
        this.petition_cover_image_url = petition_cover_image_url;
        this.petition_cover_image_thumb_url = petition_cover_image_thumb_url;
        this.petition_title = petition_title;
        this.petition_desc = petition_desc;
        this.petition_target_supporters = petition_target_supporters;
        this.petition_start_date = petition_start_date;
        this.petition_stop_date = petition_stop_date;
        this.petition_timestamp = petition_timestamp;
        this.announcementModel = announcementModel;
    }

    public String getPetition_author() {
        return petition_author;
    }

    public void setPetition_author(String petition_author) {
        this.petition_author = petition_author;
    }

    public String getPetition_cover_image_url() {
        return petition_cover_image_url;
    }

    public void setPetition_cover_image_url(String petition_cover_image_url) {
        this.petition_cover_image_url = petition_cover_image_url;
    }

    public String getPetition_cover_image_thumb_url() {
        return petition_cover_image_thumb_url;
    }

    public void setPetition_cover_image_thumb_url(String petition_cover_image_thumb_url) {
        this.petition_cover_image_thumb_url = petition_cover_image_thumb_url;
    }

    public String getPetition_title() {
        return petition_title;
    }

    public void setPetition_title(String petition_title) {
        this.petition_title = petition_title;
    }

    public String getPetition_desc() {
        return petition_desc;
    }

    public void setPetition_desc(String petition_desc) {
        this.petition_desc = petition_desc;
    }

    public String getPetition_target_supporters() {
        return petition_target_supporters;
    }

    public void setPetition_target_supporters(String petition_target_supporters) {
        this.petition_target_supporters = petition_target_supporters;
    }

    public String getPetition_start_date() {
        return petition_start_date;
    }

    public void setPetition_start_date(String petition_start_date) {
        this.petition_start_date = petition_start_date;
    }

    public String getPetition_stop_date() {
        return petition_stop_date;
    }

    public void setPetition_stop_date(String petition_stop_date) {
        this.petition_stop_date = petition_stop_date;
    }

    public Date getPetition_timestamp() {
        return petition_timestamp;
    }

    public void setPetition_timestamp(Date petition_timestamp) {
        this.petition_timestamp = petition_timestamp;
    }

    public AnnouncementModel getAnnouncementModel() {
        return announcementModel;
    }

    public void setAnnouncementModel(AnnouncementModel announcementModel) {
        this.announcementModel = announcementModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.petition_author);
        dest.writeString(this.petition_cover_image_url);
        dest.writeString(this.petition_cover_image_thumb_url);
        dest.writeString(this.petition_title);
        dest.writeString(this.petition_desc);
        dest.writeString(this.petition_target_supporters);
        dest.writeString(this.petition_start_date);
        dest.writeString(this.petition_stop_date);
        dest.writeLong(this.petition_timestamp != null ? this.petition_timestamp.getTime() : -1);
        //dest.writeParcelable(this.announcementModel, flags);
    }

    protected PetitionModel(Parcel in) {
        this.petition_author = in.readString();
        this.petition_cover_image_url = in.readString();
        this.petition_cover_image_thumb_url = in.readString();
        this.petition_title = in.readString();
        this.petition_desc = in.readString();
        this.petition_target_supporters = in.readString();
        this.petition_start_date = in.readString();
        this.petition_stop_date = in.readString();
        long tmpPetition_timestamp = in.readLong();
        this.petition_timestamp = tmpPetition_timestamp == -1 ? null : new Date(tmpPetition_timestamp);
        //this.announcementModel = in.readParcelable(AnnouncementModel.class.getClassLoader());
    }

    public static final Parcelable.Creator<PetitionModel> CREATOR = new Parcelable.Creator<PetitionModel>() {
        @Override
        public PetitionModel createFromParcel(Parcel source) {
            return new PetitionModel(source);
        }

        @Override
        public PetitionModel[] newArray(int size) {
            return new PetitionModel[size];
        }
    };
}
