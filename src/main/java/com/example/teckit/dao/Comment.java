package com.example.teckit.dao;

import javax.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private int ticketId;
    private long timestamp;
    private int creatorId;

    @Transient
    private User creator;

    @Column(length = 1000)
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int requestId) {
        this.ticketId = requestId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
