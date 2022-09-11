package com.example.teckit.dao;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String name;
    private boolean staff;
    private boolean admin;

    @Transient
    private StudentData data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStaff() {
        return this.staff;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public StudentData getData() {
        return data;
    }

    public void setData(StudentData data) {
        this.data = data;
    }
}
