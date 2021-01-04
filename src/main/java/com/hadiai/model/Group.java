package com.hadiai.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.Random; 

import com.hadiai.model.User;

@Entity
@Table(name = "_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;    
    
    @NotBlank
    @Size(max = 20)
    private String token;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE
            },
            mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    public Group() {
    }

    public Group(String name, String token, Set<User> users) {
        this.name = name;
        this.token = generateToken();
        this.users = users;
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
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = generateToken();
    }    
    
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    private String generateToken(){
        Random rand = new Random();
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        String token = String.valueOf(rand.nextInt(10));
        for (int i = 0; i < 7; i++) {
        	token = token + alphabet[rand.nextInt(26)];
        	token = token + String.valueOf(rand.nextInt(10));
        }
        return token;
    }
}
