package com.example.teckit.external;

import com.example.teckit.dao.Comment;
import com.example.teckit.dao.Ticket;
import com.example.teckit.dao.User;
import org.springframework.stereotype.Component;

/**
 * Stubbed component for managing the notifications
 * Intended purpose: receive events, send notifications as appropriate
 */
@Component
public class NotificationSender {
    public void onCommentAddedToTrackedTicket(User user, Ticket ticket, Comment comment) {
        //TODO
        // 1. Look up notification preferences
        // 2. For every notification medium, form a notification and send
    }
}
