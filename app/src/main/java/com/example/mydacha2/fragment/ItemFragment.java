package com.example.mydacha2.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.R;
import com.example.mydacha2.myActivity.ManagementObject;
import com.example.mydacha2.supportclass.MyButtonClickListener;
import com.example.mydacha2.supportclass.MyCheckedChangeListener;
import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyDataAdapter;
import com.example.mydacha2.supportclass.MyListData;

import java.util.ArrayList;
import java.util.List;


public class ItemFragment extends Fragment implements MyButtonClickListener, MyClickListener, MyCheckedChangeListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    public  List<MyListData> myListData;
    private final List<Long> selectObjectControl  = new ArrayList<>();

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    assert intent != null;
                }
            });


    public ItemFragment() {}

    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
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
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.scrollToPosition(0);
        MyDataAdapter adapter = new MyDataAdapter(myListData, this, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(Long position) {
        Intent managementObjectActivity = new Intent(getContext(), ManagementObject.class);
        managementObjectActivity.putExtra("id", position);
        mStartForResult.launch(managementObjectActivity);
     //   Toast.makeText(getActivity(),"click on item: "+ position,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemLongClick(Long position) {
     //   Toast.makeText(getActivity(),"click on item: "+ position,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckedChange(Long position, boolean b) {
        if(b){
            selectObjectControl.add(position);
        }else {
            selectObjectControl.remove(position);
        }
    }

    @Override
    public void onButtonClick(Long position) {

    }
}