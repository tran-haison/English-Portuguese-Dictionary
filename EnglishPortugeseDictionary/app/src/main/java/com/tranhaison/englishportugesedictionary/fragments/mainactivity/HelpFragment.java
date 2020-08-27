package com.tranhaison.englishportugesedictionary.fragments.mainactivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tranhaison.englishportugesedictionary.R;

public class HelpFragment extends Fragment {

    // Init views
    Button btnSubmit;
    EditText etQuestionAsked;

    public HelpFragment() {
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
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Map views
        btnSubmit = view.findViewById(R.id.btnSubmit);
        etQuestionAsked = view.findViewById(R.id.etQuestionAsked);

        // Submit question
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = etQuestionAsked.getText().toString();
                Toast.makeText(getActivity(), question, Toast.LENGTH_SHORT).show();
            }
        });
    }
}