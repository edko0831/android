package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.ControlPointRepository;
import com.example.mydacha2.supportclass.MyCheckedChangeListener;
import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyControlPointAdapter;
import com.example.mydacha2.supportclass.MyListControlPoint;

import java.util.ArrayList;
import java.util.List;

public class ListControlPointActivity extends AppCompatActivity implements MyClickListener, MyCheckedChangeListener {
    ControlPointDAO controlPointDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_control_point);

        TextView textView = findViewById(R.id.textView_object);
        textView.setText(R.string.list_control_point_page);

        ImageButton button = findViewById(R.id.imageButton_add);
        button.setOnClickListener(v -> {
            Intent controlPointActivity = new Intent(this, AddControlPointActivity.class);
            startActivity(controlPointActivity);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewListControlPoint);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.scrollToPosition(0);
        MyControlPointAdapter adapter = new MyControlPointAdapter(setMyControlPoint(), this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Long position) {

    }

    @Override
    public void onItemLongClick(Long position) {

    }

    private List<MyListControlPoint> setMyControlPoint() {
        ControlPointRepository controlPointRepository = new ControlPointRepository(this);
        controlPointDAO = controlPointRepository.getControlPoint().controlPointDAO();
        List<ControlPoint> listControlPointDAO = controlPointDAO.select();

        List<MyListControlPoint> myListControlPoint = new ArrayList<>();

        for (ControlPoint cp : listControlPointDAO) {
            if (cp.name != null){
                myListControlPoint.add(new MyListControlPoint(cp));
            }
        }
        return myListControlPoint;
    }

    @Override
    public void onCheckedChange(Long position, boolean b) {

    }
}