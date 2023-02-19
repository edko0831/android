package com.example.mydacha2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.R;
import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyListMain;
import com.example.mydacha2.supportclass.MyMainAdapter;

public class MainActivityNewFragment extends Fragment {
    private final MyClickListener myClickListener;
    private final  MyListMain[] myListMain;

    public MainActivityNewFragment(MyClickListener myClickListener, MyListMain[] myListMain) {
        this.myClickListener = myClickListener;
        this.myListMain = myListMain;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.scrollToPosition(0);
        MyMainAdapter adapter = new MyMainAdapter(myListMain, myClickListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}