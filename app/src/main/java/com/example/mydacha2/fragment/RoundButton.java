package com.example.mydacha2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mydacha2.R;
import com.example.mydacha2.supportclass.OnSelectedButtonListener;


public class RoundButton extends Fragment implements View.OnClickListener{
    ImageButton button;

    public RoundButton() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_round_button, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.button_round_add);
        button.setImageResource(R.mipmap.icons8_plus_94_2_foreground);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int buttonIndex = view.getId();

        OnSelectedButtonListener listener = (OnSelectedButtonListener) getActivity();
        assert listener != null;
        listener.onButtonSelected(buttonIndex);

    }

}