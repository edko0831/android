package com.example.mydacha2.myActivity.data;

import android.content.Context;

import com.example.mydacha2.DAO.UsersDAO;
import com.example.mydacha2.Entity.Users;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;


public class MyLogin {

    public static Users getLogin(String username, String password, Context context) {
            UsersDAO usersDAO;
            AppDatabase db = App.getInstance(context).getDatabase();
            usersDAO = db.usersDAO();
            Users user = usersDAO.selectUser(username,password);

            if(user != null) {
                return user;
            } else {
                return new Users();
            }
    }
    public static void setUser(Users users, Context context) {
        UsersDAO usersDAO;
        AppDatabase db = App.getInstance(context).getDatabase();
        usersDAO = db.usersDAO();
        usersDAO.insert(users);

        usersDAO.selectUser(users.displayName, users.password);
    }

  //  public void logout() {}
}