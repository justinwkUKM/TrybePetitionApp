package com.trybe.project.petitionapp.models;

import android.support.annotation.NonNull;

/**
 * Created by MyXLab on 28/3/2018.
 */

public class PetitionPostId {
    public String PetitionPostId;

    public <T extends PetitionPostId> T withId(@NonNull final String id){
        this.PetitionPostId = id;
        return (T) this;
    }
}
