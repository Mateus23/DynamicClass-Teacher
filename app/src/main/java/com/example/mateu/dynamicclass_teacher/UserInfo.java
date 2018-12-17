package com.example.mateu.dynamicclass_teacher;

public class UserInfo {

    public String name;

    public String completeName;

    public String email;


    public UserInfo() {

    }


    public UserInfo(String uId, String name, String lastName, String email){
        this.name = name;
        this.completeName = CryptographyAdapter.encryptText(name + " " + lastName, uId);
        this.email = CryptographyAdapter.encryptText(email, uId);

    }

}


