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

import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.Entity.ObjectControl;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.fragment.ItemFragment;
import com.example.mydacha2.fragment.RoundButton;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.example.mydacha2.supportclass.MyListObjectControl;
import com.example.mydacha2.supportclass.OnSelectedButtonListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = 33)
public class MyObject extends AppCompatActivity implements OnSelectedButtonListener {
    private static final String LOG_TAG = "MyObject";
    FrameLayout frameLayout;
    ItemFragment itemFragment;
    RoundButton roundButton;
    TextView textView;
    ObjectControlsDAO objectControlsDAO;
    List<ObjectControl> objectControlList;
    AlertDialog.Builder builder;
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
      //  selectObjectControlList = new ArrayList<>();

        builder = new AlertDialog.Builder(this);

        setFragment();
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFragment() {
        textView.setText(R.string.object_control);

        if(objectControlsDAO.count() != 0){
            objectControlList = objectControlsDAO.select();
        }

        List<MyListObjectControl> myListData = new ArrayList<>();

        for (ObjectControl oc : objectControlList) {
            if (oc.name != null){
                myListData.add(new MyListObjectControl(oc));
            }
        }

        itemFragment = new ItemFragment();

        if (selectObject.size() > 0) {
            itemFragment.setSelectObjectControl(selectObject);
        }

        itemFragment.myListData = myListData;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutMyObject, itemFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.frameLayoutMyObjectButton, roundButton);
        fragmentTransaction2.show(roundButton);
        fragmentTransaction2.addToBackStack(null);
        fragmentTransaction2.commit();
    }

    public void onButtonSelected(int buttonIndex) {
        if (buttonIndex == R.id.button_round_add) {
            selectObject = itemFragment.getSelectObjectControl();
            if (selectObject.size() != 0) {
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
            } else {
                setFragmentAdd();
            }
        } else if (buttonIndex == R.id.buttonSave) {
            setFragment();
        } else if (buttonIndex == R.id.buttonCancel) {//  selectObject.clear();
             setFragment();
        }
    }

    private void deleteObject(List<Long> selectObject) {
       for (int i = 0; i<selectObject.size(); i++ ) {
           objectControlsDAO.deleteId(selectObject.get(i).intValue());
       }
       setFragment();
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
             //startActivityForResult(intent,1);
         }
     }
 }