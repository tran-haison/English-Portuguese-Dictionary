package com.tranhaison.englishportugesedictionary.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tranhaison.englishportugesedictionary.FragmentListener;
import com.tranhaison.englishportugesedictionary.R;

public class FavoritesFragment extends Fragment {

    private String value = "This is Favorite";
    private FragmentListener fragmentListener;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonDetail2 = view.findViewById(R.id.buttonDetail2);
        buttonDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragmentListener != null)
                    fragmentListener.onItemClick(value);
            }
        });
    }

    public void setOnFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }
}