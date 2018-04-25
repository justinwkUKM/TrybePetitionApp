package com.trybe.project.petitionapp.models;

import java.util.Date;

/**
 * Created by Waqas Khalid Obeidy on 26/3/2018.
 */

public class AnnouncementModel extends AnnouncementPostId {
    private String announcement_author, announcement_cover_image_url, announcement_cover_image_thumb_url,
            announcement_title, announcement_desc,announcement_petition_id;
    private Date announcement_timestamp;


    public AnnouncementModel() {
    }

    public AnnouncementModel(String announcement_author, String announcement_cover_image_url, String announcement_cover_image_thumb_url, String announcement_title, String announcement_desc, String announcement_petition_id, Date announcement_timestamp) {
        this.announcement_author = announcement_author;
        this.announcement_cover_image_url = announcement_cover_image_url;
        this.announcement_cover_image_thumb_url = announcement_cover_image_thumb_url;
        this.announcement_title = announcement_title;
        this.announcement_desc = announcement_desc;
        this.announcement_petition_id = announcement_petition_id;
        this.announcement_timestamp = announcement_timestamp;
    }

    public String getAnnouncement_author() {
        return announcement_author;
    }

    public void setAnnouncement_author(String announcement_author) {
        this.announcement_author = announcement_author;
    }

    public String getAnnouncement_cover_image_url() {
        return announcement_cover_image_url;
    }

    public void setAnnouncement_cover_image_url(String announcement_cover_image_url) {
        this.announcement_cover_image_url = announcement_cover_image_url;
    }

    public String getAnnouncement_cover_image_thumb_url() {
        return announcement_cover_image_thumb_url;
    }

    public void setAnnouncement_cover_image_thumb_url(String announcement_cover_image_thumb_url) {
        this.announcement_cover_image_thumb_url = announcement_cover_image_thumb_url;
    }

    public String getAnnouncement_title() {
        return announcement_title;
    }

    public void setAnnouncement_title(String announcement_title) {
        this.announcement_title = announcement_title;
    }

    public String getAnnouncement_desc() {
        return announcement_desc;
    }

    public void setAnnouncement_desc(String announcement_desc) {
        this.announcement_desc = announcement_desc;
    }

    public String getAnnouncement_petition_id() {
        return announcement_petition_id;
    }

    public void setAnnouncement_petition_id(String announcement_petition_id) {
        this.announcement_petition_id = announcement_petition_id;
    }

    public Date getAnnouncement_timestamp() {
        return announcement_timestamp;
    }

    public void setAnnouncement_timestamp(Date announcement_timestamp) {
        this.announcement_timestamp = announcement_timestamp;
    }
}
