package com.hadiai.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.Random; 

import com.hadiai.model.User;

@Entity
@Table(name = "sections")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Section {

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
        })
    @JoinTable(name = "sections_students",
        joinColumns = { @JoinColumn(name = "user_id") },
        inverseJoinColumns = { @JoinColumn(name = "section_id") })
    private Set<User> students = new HashSet<>();

    public Section() {
    }

    public Section(String name, String token) {
        this.name = name;
        this.token = generateToken();
    }    
    
    public Section(String name, String token, Set<User> students) {
        this.name = name;
        this.token = generateToken();
        this.students = students;
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
    
    public Set<User> getStudents() {
        return students;
    }

    public void setStudents(Set<User> students) {
        this.students = students;
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
