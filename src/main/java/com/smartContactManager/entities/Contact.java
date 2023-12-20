package com.smartContactManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "CONTACT")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;

    @Size(min=2, max=20, message="min 2 and max 20 characters are allowed !")
    @NotBlank(message= "Name field is required")
    private String name;

    @Size(min=10, max=12, message="enter valid phone number !")
    @NotBlank(message= "phone number is required")
    private String phone;

    @Email(regexp="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message="enter valid email !")
    @NotBlank(message= "email is required")
    @Column(unique = true)
    private String email;

    @NotBlank(message="work field is required")
    private String work;
    private String imageUrl;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Column(length = 200)
    private String about;

    public Contact(){
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj){

        return this.cId == ((Contact)obj).getcId();
    }

//    @Override
//    public String toString() {
//        return "Contact{" +
//                "cId=" + cId +
//                ", name='" + name + '\'' +
//                ", phone='" + phone + '\'' +
//                ", email='" + email + '\'' +
//                ", work='" + work + '\'' +
//                ", imageUrl='" + imageUrl + '\'' +
//                ", user=" + user +
//                ", about='" + about + '\'' +
//                '}';
//    }
}
