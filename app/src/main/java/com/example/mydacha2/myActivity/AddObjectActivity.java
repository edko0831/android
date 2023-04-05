package com.example.mydacha2.myActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.Entity.ObjectControl;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddObjectActivity extends AppCompatActivity implements View.OnClickListener{
    EditText id;
    EditText name;
    EditText description;
    EditText picture;
    EditText basicTopic;

    private ObjectControl objectControl = new ObjectControl();
    ObjectControlsDAO objectControlsDAO;
    TextView textView;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final String LOG_TAG = "AndroidExample";
    TextInputLayout textName;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                 if (result != null) {
                    Intent intent  = result.getData();
                    assert intent != null;
                    Uri fileUri = intent.getData();
                    Log.i(LOG_TAG, "Uri: " + fileUri);

                    String filePath = null;
                    try {
                        filePath = FileUtils.getPath(this, fileUri);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Error: " + e);
                        Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                    if (filePath != null) {
                        this.picture.setText(filePath);
                    } else {
                        this.picture.setText(fileUri.getPath());
                    }
                }
            });

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textName.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object);

        ImageButton button = findViewById(R.id.imageButton_open);
        button.setBackgroundResource(R.drawable.icons8_three_dots_94_background);
        button.setImageResource(R.mipmap.icons8_three_dots_94_foreground);
        button.setOnClickListener(v -> askPermissionAndBrowseFile());

        id = findViewById(R.id.textView_id);
        name = findViewById(R.id.textEdit_name);
        description = findViewById(R.id.textEdit_description);
        picture = findViewById(R.id.textEdit_picture);
        textView = findViewById(R.id.textView_object);
        textView.setText(R.string.object_control_add);
        textName = findViewById(R.id.text_user_name);
        basicTopic = findViewById(R.id.textEdit_basicTopic);

        name.addTextChangedListener(textWatcher);

        LinearLayout linearButton = findViewById(R.id.linearLayoutButton);
        @SuppressLint("InflateParams")
        View viewButton = getLayoutInflater().inflate(R.layout.buttonlayout, null);
        linearButton.addView(viewButton);

        Button buttonSave = viewButton.findViewById(R.id.buttonSave);
        Button buttonCancel = viewButton.findViewById(R.id.buttonCancel);
        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

        AppDatabase db = App.getInstance(this).getDatabase();
        objectControlsDAO = db.ObjectControlsDAO();

        Bundle arguments = getIntent().getExtras();

        if(arguments != null){
            setFields(arguments);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setFields(Bundle arguments) {
        long getId = arguments.getLong("id");
        objectControl = objectControlsDAO.selectId((int) getId);
        textView.setText(R.string.object_control_update);
        id.setText(objectControl.id_object.toString());
        name.setText(objectControl.name);
        description.setText(objectControl.description);
        picture.setText(objectControl.picture_url);
        basicTopic.setText(objectControl.basicTopic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_object, menu);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonSave) {
            if (setNewObjectControl()) {
                finish();
            }
        } else if (id == R.id.buttonCancel) {
            finish();
        }
    }

    private boolean setNewObjectControl() {

        if(name.getText().toString().isEmpty()){
            textName.setError(getResources().getString(R.string.not_name));
            return false;
        }
      //  String r = id.getText().toString();
        if (!id.getText().toString().isEmpty()){
            objectControl.id_object = Long.parseLong(id.getText().toString());
        }
        objectControl.name = name.getText().toString();
        objectControl.description = description.getText().toString();
        objectControl.picture_url = picture.getText().toString();
        objectControl.basicTopic = basicTopic.getText().toString();

        if (objectControl.id_object == null) {
            objectControlsDAO.insert(objectControl);
            Intent intentResult = new Intent();
            setResult(RESULT_OK, intentResult);
        } else {
            objectControlsDAO.update(objectControl);
            Intent intentResult = new Intent();
            intentResult.putExtra("id", objectControl.id_object);
            setResult(RESULT_OK, intentResult);
        }
        return true;
    }

    private void askPermissionAndBrowseFile()  {

        int permisson = ActivityCompat.checkSelfPermission(this,
                 Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permisson != PackageManager.PERMISSION_GRANTED) {
            // If don't have permission so prompt the user.
            this.requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_REQUEST_CODE_PERMISSION
            );
        }
        doBrowseFile();
    }

    private void doBrowseFile() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("image/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        mStartForResult.launch(chooseFileIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        if (requestCode == MY_REQUEST_CODE_PERMISSION) {// Note: If request is cancelled, the result arrays are empty.
            // Permissions granted (CALL_PHONE).
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(LOG_TAG, "Permission granted!");
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                doBrowseFile();
            }
        }
    }

    public String getPath()  {
        return this.picture.getText().toString();
    }

    public static class FileUtils {

        private static final String LOG_TAG = "FileUtils";

        @SuppressLint("NewApi")
        public static String getPath(final Context context, final Uri uri) {
             String[] selectionArgs;
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");

                    String fullPath = getPathFromExtSD(split);
                    if (!fullPath.equals("")) {
                        return fullPath;
                    } else {
                        return null;
                    }
                }

                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    {
                        final String id;
                        try (Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                String fileName = cursor.getString(0);
                                String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                                if (!TextUtils.isEmpty(path)) {
                                    return path;
                                }
                            }
                        }
                        id = DocumentsContract.getDocumentId(uri);
                        if (!TextUtils.isEmpty(id)) {
                            if (id.startsWith("raw:")) {
                                return id.replaceFirst("raw:", "");
                            }
                            String[] contentUriPrefixesToTry = new String[] {
                                    "content://downloads/public_downloads",
                                    "content://downloads/my_downloads"
                            };
                            for (String contentUriPrefix : contentUriPrefixesToTry) {
                                try {
                                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.parseLong(id));

                                     return getDataColumn(context, contentUri, null, null);
                                } catch (NumberFormatException e) {
                                    // In Android 8 and Android P the id is not a number
                                    return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                                }
                            }
                        }
                    }
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;

                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = "_id=?";
                    selectionArgs = new String[]{split[1]};

                    return getDataColumn(context, contentUri, selection,
                            selectionArgs);
                } else if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri, context);
                }
            }

            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }

                if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri, context);
                }
                if( Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                    // return getFilePathFromURI(context,uri);
                    return getMediaFilePathForN(uri, context);
                    // return getRealPathFromURI(context,uri);
                } else {
                    return getDataColumn(context, uri, null, null);
                }
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return uri.getPath();
        }

        private static boolean fileExists(String filePath) {
              return new File(filePath).exists();
        }

        private static String getPathFromExtSD(String[] pathData) {
            final String type = pathData[0];
            final String relativePath = "/" + pathData[1];
            String fullPath;

            if ("primary".equalsIgnoreCase(type)) {
                fullPath = Environment.getExternalStorageDirectory() + relativePath;
                if (fileExists(fullPath)) {
                    return fullPath;
                }
            }

            // Environment.isExternalStorageRemovable() is `true` for external and internal storage
            // so we cannot relay on it.
            //
            // instead, for each possible path, check if file exists
            // we'll start with secondary storage as this could be our (physically) removable sd card
            fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }

            fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;

            return fullPath;
        }

        private static String getDriveFilePath(Uri uri, Context context) {
            ContentResolver contentResolver = context.getContentResolver();
            String name;
            try (Cursor returnCursor = contentResolver.query(uri, null, null, null, null)) {

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                name = (returnCursor.getString(nameIndex));
            }
            File file = new File(context.getCacheDir(), name);
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                int read;
                int maxBufferSize = 1024 * 1024;  // 1 * 1024 * 1024;
                int bytesAvailable = inputStream.available();

                // int bufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                Log.e("File Size", "Size " + file.length());
                inputStream.close();
                outputStream.close();
                Log.e("File Path", "Path " + file.getPath());
                Log.e("File Size", "Size " + file.length());
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            return file.getPath();
        }

        private static String getMediaFilePathForN(Uri uri, Context context) {
            @SuppressLint("Recycle") Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = (returnCursor.getString(nameIndex));
            File file = new File(context.getFilesDir(), name);
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                int read;
                int maxBufferSize = 1024 * 1024;   // 1 * 1024 * 1024
                int bytesAvailable = inputStream.available();

                //int bufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                Log.e("File Size", "Size " + file.length());
                inputStream.close();
                outputStream.close();
                Log.e("File Path", "Path " + file.getPath());
                Log.e("File Size", "Size " + file.length());
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            return file.getPath();
        }


        private static String getDataColumn(Context context, Uri uri,
                                            String selection, String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        selection, selectionArgs, null);

                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return null;
        }

        /**
         * @param uri - The Uri to check.
         * @return - Whether the Uri authority is ExternalStorageProvider.
         */
        private static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri - The Uri to check.
         * @return - Whether the Uri authority is DownloadsProvider.
         */
        private static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri - The Uri to check.
         * @return - Whether the Uri authority is MediaProvider.
         */
        private static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri - The Uri to check.
         * @return - Whether the Uri authority is Google Photos.
         */
        private static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Drive.
         */
        private static boolean isGoogleDriveUri(Uri uri) {
            return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) //
                    || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
        }


    }
}

