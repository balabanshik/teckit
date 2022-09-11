package com.example.teckit.tickets;

import com.example.teckit.dao.Ticket;

public class ResponseEntry {
    private int id;
    private String creator;
    private String subject;
    private long timestamp;
    private int priority;
    private String status;
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

    public String getStatus() {
        return status;
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
        this.status = ticket.getStatus().toString();
        this.subject = ticket.getSubject();
        this.requestType = ticket.getTicketType().toString();
        this.description = ticket.getDescription();
    }
}
