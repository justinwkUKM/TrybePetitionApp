package com.trybe.project.petitionapp.models;

import android.support.annotation.NonNull;

/**
 * Created by MyXLab on 28/3/2018.
 */

public class AnnouncementPostId {
    public String AnnouncementPostId;

    public <T extends AnnouncementPostId> T withId(@NonNull final String id){
        this.AnnouncementPostId = id;
        return (T) this;
    }
}
