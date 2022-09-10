package com.example.teckit.tickets;

import com.example.teckit.dao.Ticket;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class ReadTicketResponse extends ResponseEntry {
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

    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public ReadTicketResponse(Ticket ticket) {
        super(ticket);

        this.comments = new ArrayList<>();
        for (com.example.teckit.dao.Comment item : ticket.getComments()) {
            Comment comment = new Comment();
            comment.timestamp = item.getTimestamp();
            comment.creator = item.getCreator().getName();
            comment.text = item.getText();
            this.comments.add(comment);
        }
    }
}
