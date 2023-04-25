package com.example.mydacha2.myActivity;

import static android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.Entity.ObjectControl;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.fragment.RoundButton;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.example.mydacha2.supportclass.MyButtonClickListener;
import com.example.mydacha2.supportclass.MyCheckedChangeListener;
import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyDataAdapter;
import com.example.mydacha2.supportclass.MyListData;
import com.example.mydacha2.supportclass.OnSelectedButtonListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = 33)
public class MyObject extends AppCompatActivity implements MyButtonClickListener, OnSelectedButtonListener, MyClickListener, MyCheckedChangeListener {
    private static final String LOG_TAG = "MyObject";
    FrameLayout frameLayout;
    MyDataAdapter adapter;
    RoundButton roundButton;
    TextView textView;
    ObjectControlsDAO objectControlsDAO;
    List<ObjectControl> objectControlList;

    List<Long> selectObject = new ArrayList<>();
    private static final int MY_REQUEST_CODE_PERMISSION = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 4;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
    };

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    assert intent != null;
                    long id = intent.getLongExtra("id", -1);
                    if (id != - 1 ){
                        selectObject.remove(id);
                    }
                    setFragment();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        setContentView(R.layout.activity_my_object);
        frameLayout = findViewById(R.id.frameLayoutMyObject);
        roundButton = new RoundButton();

        textView = findViewById(R.id.textView_object);

        AppDatabase db = App.getInstance(this).getDatabase();
        objectControlsDAO = db.ObjectControlsDAO();
        objectControlList = new ArrayList<>();

        setFragment();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(LOG_TAG, "onRequestPermissionsResult");

        if (requestCode == READ_EXTERNAL_STORAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                Log.d(LOG_TAG, "Permission Granted: " + permissions[0]);

            } else {
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_PERMISSION
                );
                Log.d(LOG_TAG, "Permission Denied: " + permissions[0]);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
         super.onSaveInstanceState(outState);
         outState.putSerializable("selectObject", (Serializable) selectObject);
     }

    @Override
    protected void onStop() {
         super.onStop();
     }

    @Override
    protected void onStart() {
         super.onStart();
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_object, menu);
        return true;
    }

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
        }  else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFragment() {

        textView.setText(R.string.object_control);

        if(objectControlsDAO.count() != 0){
            objectControlList = objectControlsDAO.select();
        }

        List<MyListData> myListData = new ArrayList<>();

        for (ObjectControl oc : objectControlList) {
            if (oc.name != null){
                myListData.add(new MyListData(oc.id_object, oc.name, oc.picture_url, oc.description));
            }
        }

        RecyclerView recyclerView = findViewById(R.id.listObject);
        recyclerView.scrollToPosition(0);
        adapter = new MyDataAdapter(myListData, this, this);
      //  adapter.setSelectObject(selectObject);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.frameLayoutMyObjectButton, roundButton);
        fragmentTransaction2.show(roundButton);
        fragmentTransaction2.addToBackStack(null);
        fragmentTransaction2.commit();
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    public void onButtonSelected(int buttonIndex) {
        if (buttonIndex == R.id.button_round_add){
                setFragmentAdd();
        } else if (buttonIndex == R.id.buttonSave) {
            setFragment();
        } else if (buttonIndex == R.id.buttonCancel) {//  selectObject.clear();
             setFragment();
        }
    }

    private void deleteObject(List<Long> selectObject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ask_a_question)
                .setCancelable(false)
                .setNegativeButton(R.string.delete, (dialog, id) -> {
                     for (int i = 0; i<selectObject.size(); i++ ) {
                         objectControlsDAO.deleteId(selectObject.get(i).intValue());
                     }
                     selectObject.clear();
                     setFragment();
                    dialog.cancel();
                })
                .setNeutralButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setIcon(R.drawable.icons8_question_mark_64);
        alert.setTitle(R.string.question);
        alert.show();
     }

    public void setFragmentAdd() {
        Intent intent = new Intent(this, AddObjectActivity.class);
        mStartForResult.launch(intent);
    }

    public void setFragmentUpdate(List<Long> selectObject) {
          for (int i = 0; i<selectObject.size(); i++ ) {
             Intent intent = new Intent(this, AddObjectActivity.class);
             intent.putExtra("id", selectObject.get(i).longValue());
             mStartForResult.launch(intent);
         }
     }

    @Override
    public void onCheckedChange(Long position, boolean b) {
        if(b){
            selectObject.add(position);
        }else {
            selectObject.remove(position);
        }
    }

    @Override
    public void onItemClick(Long position) {
        if(selectObject.size() == 0) {
            Intent managementObjectActivity = new Intent(this, ManagementObject.class);
            managementObjectActivity.putExtra("id", position);
            mStartForResult.launch(managementObjectActivity);
        } else {
            onButtonClick(position);
        }
    }

    @Override
    public void onItemLongClick(Long position) {

    }

    @Override
    public void onButtonClick(Long position) {
        if (selectObject.size() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.ask_a_question)
                    .setCancelable(false)
                    .setPositiveButton(R.string.update, (dialogInterface, i) -> setFragmentUpdate(selectObject))
                    .setNegativeButton(R.string.delete, (dialog, id) -> {
                        deleteObject(selectObject);
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
}