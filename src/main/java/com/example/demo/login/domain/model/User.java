package com.example.demo.login.domain.model;

import lombok.Data;
import java.util.Date;

@Data
public class User {

    private String userId;
    private String password;
    private String userName;
    private Date birthday;
    private int age;
    private boolean marriage;
    private String role;//ロール
}
