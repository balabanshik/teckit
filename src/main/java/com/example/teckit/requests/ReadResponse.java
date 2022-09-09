package com.example.teckit.requests;

import com.example.teckit.dao.Comment;
import com.example.teckit.dao.Request;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class ReadResponse {
    public static class Comment {
        private long timestamp;
        private String creator;
        private String text;

        public long getTimestamp() {
            return timestamp;
        }

        public String getCreator() {
            return creator;
        }

        public String getText() {
            return text;
        }
    }

    private int id;
    private String creator;
    private String subject;
    private long timestamp;
    private int priority;
    private String requestType;
    private String description;
    private List<Comment> comments;

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

    public List<Comment> getComments() {
        return comments;
    }

    public ReadResponse(Request request) {
        this.id = request.getId();
        this.timestamp = request.getCreated();
        this.creator = request.getCreator().getName();
        this.priority = request.getPriority();
        this.requestType = request.getRequestType().toString();
        this.description = request.getDescription();

        this.comments = new ArrayList<>();
        for (com.example.teckit.dao.Comment item : request.getComments()) {
            Comment comment = new Comment();
            comment.timestamp = item.getTimestamp();
            comment.creator = item.getCreator().getName();
            comment.text = item.getText();
            this.comments.add(comment);
        }
    }
}
