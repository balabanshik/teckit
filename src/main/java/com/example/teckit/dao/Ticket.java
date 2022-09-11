package com.example.teckit.dao;

import com.example.teckit.tickets.TicketStatus;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private long created;
    private int creatorId;

    @Transient
    private User creator;

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;
    @Enumerated(EnumType.ORDINAL)
    private TicketStatus status;

    private String subject;
    private int priority;

    @Column(length = 1000)
    private String description;

    @Transient
    private List<Comment> comments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
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

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
