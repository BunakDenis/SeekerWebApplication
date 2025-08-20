package com.example.data.models.enums;

public enum UserRoles {

    ADMIN ("admin"),

    USER ("user"),

    TOURIST ("tourist");

    private String role;

    UserRoles(String role) {this.role = role;}

    public String getRole() {
        return role;
    }

}
