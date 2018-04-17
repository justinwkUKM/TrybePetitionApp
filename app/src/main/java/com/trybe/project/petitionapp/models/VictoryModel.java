package com.trybe.project.petitionapp.models;

import java.util.Date;

/**
 * Created by MyXLab on 26/3/2018.
 */

public class VictoryModel extends PetitionPostId {
    private String victory_petition_author, victory_petition_cover_image_url, victory_petition_cover_image_thumb_url,
            victory_petition_title, victory_petition_desc, victory_petition_target_supporters, victory_petition_start_date,
            victory_petition_stop_date;
    private Date victory_petition_timestamp;


    public VictoryModel() {
    }

    public VictoryModel(String victory_petition_author, String victory_petition_cover_image_url, String victory_petition_cover_image_thumb_url, String victory_petition_title, String victory_petition_desc, String victory_petition_target_supporters, String victory_petition_start_date, String victory_petition_stop_date, Date victory_petition_timestamp, AnnouncementModel announcementModel) {
        this.victory_petition_author = victory_petition_author;
        this.victory_petition_cover_image_url = victory_petition_cover_image_url;
        this.victory_petition_cover_image_thumb_url = victory_petition_cover_image_thumb_url;
        this.victory_petition_title = victory_petition_title;
        this.victory_petition_desc = victory_petition_desc;
        this.victory_petition_target_supporters = victory_petition_target_supporters;
        this.victory_petition_start_date = victory_petition_start_date;
        this.victory_petition_stop_date = victory_petition_stop_date;
        this.victory_petition_timestamp = victory_petition_timestamp;
    }

    public String getPetition_author() {
        return victory_petition_author;
    }

    public void setPetition_author(String victory_petition_author) {
        this.victory_petition_author = victory_petition_author;
    }

    public String getPetition_cover_image_url() {
        return victory_petition_cover_image_url;
    }

    public void setPetition_cover_image_url(String victory_petition_cover_image_url) {
        this.victory_petition_cover_image_url = victory_petition_cover_image_url;
    }

    public String getPetition_cover_image_thumb_url() {
        return victory_petition_cover_image_thumb_url;
    }

    public void setPetition_cover_image_thumb_url(String victory_petition_cover_image_thumb_url) {
        this.victory_petition_cover_image_thumb_url = victory_petition_cover_image_thumb_url;
    }

    public String getPetition_title() {
        return victory_petition_title;
    }

    public void setPetition_title(String victory_petition_title) {
        this.victory_petition_title = victory_petition_title;
    }

    public String getPetition_desc() {
        return victory_petition_desc;
    }

    public void setPetition_desc(String victory_petition_desc) {
        this.victory_petition_desc = victory_petition_desc;
    }

    public String getPetition_target_supporters() {
        return victory_petition_target_supporters;
    }

    public void setPetition_target_supporters(String victory_petition_target_supporters) {
        this.victory_petition_target_supporters = victory_petition_target_supporters;
    }

    public String getPetition_start_date() {
        return victory_petition_start_date;
    }

    public void setPetition_start_date(String victory_petition_start_date) {
        this.victory_petition_start_date = victory_petition_start_date;
    }

    public String getPetition_stop_date() {
        return victory_petition_stop_date;
    }

    public void setPetition_stop_date(String victory_petition_stop_date) {
        this.victory_petition_stop_date = victory_petition_stop_date;
    }

    public Date getPetition_timestamp() {
        return victory_petition_timestamp;
    }

    public void setPetition_timestamp(Date victory_petition_timestamp) {
        this.victory_petition_timestamp = victory_petition_timestamp;
    }

}
