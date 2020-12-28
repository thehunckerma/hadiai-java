package com.hadiai.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Entity
@Table(name = "groups", uniqueConstraints = { @UniqueConstraint(columnNames = "groupName"),
        @UniqueConstraint(columnNames = "groupManager") })
public class Group{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String groupName;

    @NotBlank
    private Date createdAt;

    @NotBlank
    @Size(max = 20)
    private User groupManager; 
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)

   

    public Group(){
    }

    public Group(String groupName, User groupManager, Date createdAt) {
        this.groupName = groupName;
        this.groupManager = groupManager;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public User getGroupManaer() {
        return groupManager;
    }

    public void setGroupManager(User groupManager) {
        this.groupManager = groupManager;
    }

    public Date getCreatedAt() {
        return createdAt ;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }