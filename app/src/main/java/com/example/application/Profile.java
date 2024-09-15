package com.example.application;

public class Profile {
    private int id;
    private String name;
    private int age;
    private String problems;
    private String address;
    private String mobile;

    public Profile(int id, String name, int age, String problems, String address, String mobile) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.problems = problems;
        this.address = address;
        this.mobile = mobile;
    }

    // New constructor to match the parameters from AddProfileActivity
    public Profile(String name, int age, String problems, String address, String mobile) {
        this.name = name;
        this.age = age;
        this.problems = problems;
        this.address = address;
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getProblems() {
        return problems;
    }

    public String getAddress() {
        return address;
    }

    public String getMobile() {
        return mobile;
    }
}
