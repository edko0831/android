package com.example.mydacha2.myActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.example.mydacha2.supportclass.MyCheckedChangeListener;
import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyControlPointAdapter;
import com.example.mydacha2.supportclass.MyListControlPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        Intent controlPointActivity = new Intent(this, AddControlPointActivity.class);
        mStartForResult.launch(controlPointActivity);
    }

    private void deleteControlPoint(List<Long> selectControlPoint) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ask_a_question)
                .setCancelable(false)
                .setNegativeButton(R.string.delete, (dialog, id) -> {
                    for (int i = 0; i<selectControlPoint.size(); i++ ) {
                        controlPointDAO.delete(controlPointDAO.selectId(selectControlPoint.get(i).intValue()));
                    }
                    selectControlPoint.clear();
                    setAdapter();
                    dialog.cancel();
                })
                .setNeutralButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setIcon(R.drawable.icons8_question_mark_64);
        alert.setTitle(R.string.question);
        alert.show();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_control_point, menu);
        return true;
    }

    @RequiresApi(api = 33)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            // Toast.makeText(this, getString(R.string.action_settings), Toast.LENGTH_LONG).show();
        } else if (id == R.id.connect) {
            startActivity(new Intent(this, ConnectWiFi.class));
        } else if (id == R.id.home_page) {
            startActivity(new Intent(this, MainActivity.class));
            // Toast.makeText(this, getString(R.string.object_control), Toast.LENGTH_LONG).show();
        }  else if (id == R.id.action_close) {
            this.finishAffinity();
        }  else if (id == R.id.myObjet) {
            startActivity(new Intent(this, MyObject.class));
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemClick(Long position) {
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
        }
    }

    @Override
    public void onItemLongClick(Long position) {

    }

    private List<MyListControlPoint> setMyControlPoint() {
        AppDatabase db = App.getInstance(this).getDatabase();
        controlPointDAO = db.controlPointDAO();
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