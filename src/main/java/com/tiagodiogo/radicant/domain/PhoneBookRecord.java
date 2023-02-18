package com.tiagodiogo.radicant.domain;

import java.io.Serializable;

public class PhoneBookRecord implements Serializable {

    private Long id;
    private String name;
    private String email;
    private Integer mobile;

    public PhoneBookRecord() {
        // empty constructor for serialization
    }

    public PhoneBookRecord(String[] csv) {
        this.id = Long.valueOf(csv[0]);
        this.name = csv[1];
        this.email = csv[2];
        this.mobile = Integer.valueOf(csv[3]);
    }

    public String toCSV() {
        return String.format("%d,%s,%s,%d", this.id, this.name, this.email, this.mobile);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getMobile() {
        return mobile;
    }

    public void setMobile(Integer mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "PhoneBookRecord{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", mobile=" + mobile + '}';
    }
}
