package com.example.mydacha2.DataClasses;

public class MyRole {
    private long roleId;
    private String name;
    private MyRole(long roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    public static MyRole[] getMyRole() {
        return new MyRole[]{
                new MyRole(1L, "admin"),
                new MyRole(2L, "user")
          //      new MyRole(3L, getString(R.string.action_settings)),

        };
    }

    public long getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }
}
