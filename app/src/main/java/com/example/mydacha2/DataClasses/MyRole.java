package com.example.mydacha2.DataClasses;

public class MyRole {
    long l;
    String name;
    private MyRole(long l, String name) {
        this.l = l;
        this.name = name;
    }

    public static MyRole[] getMyRole() {
        return new MyRole[]{
                new MyRole(1L, "admin"),
                new MyRole(2L, "user")
          //      new MyRole(3L, getString(R.string.action_settings)),

        };
    }
}
