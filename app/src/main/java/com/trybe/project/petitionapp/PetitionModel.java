package com.trybe.project.petitionapp;

import com.google.firebase.Timestamp;

import java.util.Date;

/**
 * Created by MyXLab on 26/3/2018.
 */

public class PetitionModel {
    private String petition_author, petition_cover_image_url, petition_cover_image_thumb_url,
            petition_title, petition_desc, petition_target_supporters, petition_start_date,
            petition_stop_date;
    private Date petition_timestamp;

    public PetitionModel() {
    }

    public Date getPetition_timestamp() {
        return petition_timestamp;
    }

    public void setPetition_timestamp(Date petition_timestamp) {
        this.petition_timestamp = petition_timestamp;
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
}
