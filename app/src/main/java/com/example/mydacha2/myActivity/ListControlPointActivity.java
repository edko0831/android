package com.example.mydacha2.myActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    public final List<Long> selectControlPoint = new ArrayList<>();
    RecyclerView recyclerView;
    MyControlPointAdapter adapter;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    assert intent != null;
                    long id = intent.getLongExtra("id", -1);
                    if (id != - 1 ){
                        selectControlPoint.remove(id);
                       }
                     setAdapter();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_control_point);

        TextView textView = findViewById(R.id.textView_object);
        textView.setText(R.string.list_control_point_page);

        ImageButton button = findViewById(R.id.imageButton_add);
        button.setOnClickListener(v -> openAddControlPointActivity());

        recyclerView = findViewById(R.id.recyclerViewListControlPoint);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.scrollToPosition(0);
        setAdapter();
    }

    private void setAdapter(){
        adapter = new MyControlPointAdapter(setMyControlPoint(), this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void openAddControlPointActivity(){
        if(selectControlPoint.size() > 0){
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.ask_a_question)
                    .setCancelable(false)
                    .setPositiveButton(R.string.update, (dialogInterface, i) -> setAddControlPointActivity(selectControlPoint))
                    .setNegativeButton(R.string.delete, (dialog, id) -> {
                        deleteControlPoint(selectControlPoint);
                        dialog.cancel();
                    })
                    .setNeutralButton(R.string.cancel, (dialog, id) -> dialog.cancel());
            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setIcon(R.drawable.icons8_question_mark_64);
            alert.setTitle(R.string.question);
            alert.show();
        } else {
            Intent controlPointActivity = new Intent(this, AddControlPointActivity.class);
            mStartForResult.launch(controlPointActivity);
        }
     }

    private void deleteControlPoint(List<Long> selectControlPoint) {
        for (int i = 0; i<selectControlPoint.size(); i++ ) {
            controlPointDAO.delete(controlPointDAO.selectId(selectControlPoint.get(i).intValue()));
            selectControlPoint.remove(selectControlPoint.get(i));
        }
        selectControlPoint.clear();
        setAdapter();
    }

    private void setAddControlPointActivity(List<Long> selectControlPoint) {
        for (int i = 0; i<selectControlPoint.size(); i++ ) {
            Intent controlPointActivity = new Intent(this, AddControlPointActivity.class);
            controlPointActivity.putExtra("id", selectControlPoint.get(i).longValue());
            mStartForResult.launch(controlPointActivity);
        }
        selectControlPoint.clear();
        setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        if (b) {
            selectControlPoint.add(position);
        } else {
            selectControlPoint.remove(position);
        }
    }
}