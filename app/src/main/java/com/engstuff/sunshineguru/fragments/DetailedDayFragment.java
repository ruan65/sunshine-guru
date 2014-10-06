package com.engstuff.sunshineguru.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.engstuff.sunshineguru.R;

public class DetailedDayFragment extends Fragment {

    public DetailedDayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            ((TextView) rootView.findViewById(R.id.detailed_tv))
                    .setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
        return rootView;
    }
}
