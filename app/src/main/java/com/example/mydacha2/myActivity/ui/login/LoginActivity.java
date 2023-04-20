package com.example.mydacha2.myActivity.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.Entity.Users;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.databinding.ActivityLoginBinding;
import com.example.mydacha2.myActivity.data.MyLogin;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout textInputLayoutUsername;
    TextInputLayout textInputLayoutPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.mydacha2.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EditText usernameEdit = findViewById(R.id.username);
        textInputLayoutUsername = findViewById(R.id.viewView_username);
        textInputLayoutPassword = findViewById(R.id.viewView_password);

        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        Button buttonRegistration = findViewById(R.id.buttonRegistration);
        final ProgressBar loadingProgressBar = binding.loading;

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textInputLayoutUsername.setErrorEnabled(false);
                loginButton.setEnabled(true);
            }
        };
        TextWatcher afterChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textInputLayoutPassword.setErrorEnabled(false);
                loginButton.setEnabled(true);
            }
        };
        usernameEdit.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterChangedListener);

        loginButton.setOnClickListener(v -> {
            if(usernameEdit.getText().toString().isEmpty()){
                usernameEdit.setError(getResources().getString(R.string.not_user));
                return;
            }

            if( passwordEditText.getText().toString().isEmpty()){
                passwordEditText.setError(getResources().getString(R.string.not_password));
                return;
            }

            loadingProgressBar.setVisibility(View.VISIBLE);
            Users user = new Users();
            if(!usernameEdit.getText().toString().isEmpty() &&
                    !passwordEditText.getText().toString().isEmpty()){
                 user = MyLogin.getLogin(usernameEdit.getText().toString(),
                        passwordEditText.getText().toString(),
                        this);
            }
            if (user.displayName != null) {
                updateUiWithUser();
            } else {
                showLoginFailed(getString(R.string.invalid_username));
                buttonRegistration.setEnabled(true);
                loginButton.setEnabled(false);
            }
            loadingProgressBar.setVisibility(View.INVISIBLE);
        });

        buttonRegistration.setOnClickListener(v -> {
            if (usernameEdit.getText().toString().isEmpty()) {
                usernameEdit.setError(getResources().getString(R.string.not_user));
                return;
            }

            if (passwordEditText.getText().toString().isEmpty()) {
                passwordEditText.setError(getResources().getString(R.string.not_password));
                return;
            }

            loadingProgressBar.setVisibility(View.VISIBLE);
            Users user = new Users(usernameEdit.getText().toString(),
                    passwordEditText.getText().toString(), 2L);
            MyLogin.setUser( user,this);

            updateUiWithUser();
        });
    }

    private void updateUiWithUser() {
     //   String welcome = getString(R.string.welcome) + model.getDisplayName();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}