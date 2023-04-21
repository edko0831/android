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

import com.example.mydacha2.DAO.UsersDAO;
import com.example.mydacha2.Entity.Users;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.example.mydacha2.supportclass.MyCheckedChangeListener;
import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyDataAdapter;
import com.example.mydacha2.supportclass.MyListData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsersActivity extends AppCompatActivity implements MyClickListener, MyCheckedChangeListener {
    RecyclerView recyclerView;
    public final List<Long> selectData = new ArrayList<>();

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    assert intent != null;
                    long id = intent.getLongExtra("id", -1);
                    if (id != - 1 ){
                        selectData.remove(id);
                    }
                    setAdapter();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView = findViewById(R.id.textView_object);
        textView.setText(R.string.list_users_page);

        ImageButton button = findViewById(R.id.imageButton_add);
        button.setOnClickListener(v -> openAddUsers());

        recyclerView = findViewById(R.id.recyclerViewListUsers);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.scrollToPosition(0);
        setAdapter();
    }

    private void setAdapter() {
        MyDataAdapter adapter = new MyDataAdapter(setMyUsers(), this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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

    private void openAddUsers() {
        mStartForResult.launch(new Intent(this, AddUserActivity.class));
    }

    @Override
    public void onCheckedChange(Long position, boolean b) {
        if (b) {
            selectData.add(position);
        } else {
            selectData.remove(position);
        }
    }

    @Override
    public void onItemClick(Long position) {
        if(selectData.size() > 0){
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.ask_a_question_users)
                    .setCancelable(false)
                    .setPositiveButton(R.string.update, (dialogInterface, i) -> setUpdateUser(selectData))
                    .setNegativeButton(R.string.delete, (dialog, id) -> {
                        deleteUser(selectData);
                        dialog.cancel();
                    })
                    .setNeutralButton(R.string.cancel, (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setIcon(R.drawable.icons8_question_mark_64);
            alert.setTitle(R.string.question);
            alert.show();
        }
    }

    private void deleteUser(List<Long> selectData) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ask_a_question_users)
                .setCancelable(false)
                .setNegativeButton(R.string.delete, (dialog, id) -> {
                    UsersDAO usersDAO;
                    AppDatabase db = App.getInstance(this).getDatabase();
                    usersDAO = db.usersDAO();
                    for (int i = 0; i<selectData.size(); i++ ) {
                        Users users = usersDAO.selectId(selectData.get(i).intValue());
                        if(users.roleId == 1) {
                            List<Users> listUsers = usersDAO.selectRole(1);
                            if(listUsers.size() == 1){
                                continue;
                            }
                        }
                        usersDAO.delete(users);
                    }
                    selectData.clear();
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

    private void setUpdateUser(List<Long> selectData) {
        for (int i = 0; i<selectData.size(); i++ ) {
            Intent addUserActivity = new Intent(this, AddUserActivity.class);
            addUserActivity.putExtra("id", selectData.get(i).longValue());
            mStartForResult.launch(addUserActivity);
        }
        selectData.clear();
    }

    @Override
    public void onItemLongClick(Long position) {}

    private List<MyListData> setMyUsers() {
        UsersDAO usersDAO;
        AppDatabase db = App.getInstance(this).getDatabase();
        usersDAO = db.usersDAO();
        List<Users> lisUsers = usersDAO.select();

        List<MyListData> myListData = new ArrayList<>();

        for (Users cp : lisUsers) {
            if (cp.displayName != null){
                myListData.add(new MyListData(cp.userId, cp.displayName, null, null));
            }
        }
        return myListData;
    }
}