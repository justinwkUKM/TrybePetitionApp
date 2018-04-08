package com.trybe.project.petitionapp.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trybe.project.petitionapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewPetitionFragment extends Fragment {


    public AddNewPetitionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_petition, container, false);
    }

}
