package com.example.teckit.tickets;

import com.example.teckit.dao.Ticket;

public class ResponseEntry {
    private int id;
    private String creator;
    private String subject;
    private long timestamp;
    private int priority;
    private String requestType;
    private String description;

    public int getId() {
        return id;
    }

    public String getCreator() {
        return creator;
    }

    public String getSubject() {
        return subject;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public String getRequestType() {
        return requestType;
    }

    public ResponseEntry(Ticket ticket) {
        this.id = ticket.getId();
        this.timestamp = ticket.getCreated();
        this.creator = ticket.getCreator().getName();
        this.priority = ticket.getPriority();
        this.requestType = ticket.getRequestType().toString();
        this.description = ticket.getDescription();
    }
}
