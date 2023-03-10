package com.example.mydacha2.fragment;

import static com.example.mydacha2.R.layout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyListMain;
import com.example.mydacha2.supportclass.MyMainAdapter;

public class ItemMainFragment extends Fragment implements MyClickListener {

    public  MyListMain[] myListMain;

    public ItemMainFragment() {}

    public static ItemMainFragment newInstance() {
        ItemMainFragment fragment = new ItemMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(layout.fragment_main_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.scrollToPosition(0);
        MyMainAdapter adapter = new MyMainAdapter(myListMain, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void setMyListMain(MyListMain[] myListMain) {
        this.myListMain = myListMain;
    }

    @Override
    public void onItemClick(Long position) {
        Toast.makeText(getContext(), position.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemLongClick(Long position) {

    }
}