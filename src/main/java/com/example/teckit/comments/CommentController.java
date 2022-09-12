package com.example.teckit.comments;

import com.example.teckit.dao.Comment;
import com.example.teckit.dao.DAL;
import com.example.teckit.dao.Ticket;
import com.example.teckit.dao.User;
import com.example.teckit.external.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Currently only has one method, for adding a comment to an existing ticket
 */
@RestController
@RequestMapping(path="/comments")
@ParametersAreNonnullByDefault
public class CommentController {
    @Autowired
    private DAL dal;

    @Autowired
    private NotificationSender notificationSender;

    /**
     * Add
     * @param request request body
     * @return Comment id
     */
    @PostMapping("/add")
    public @ResponseBody long addComment(@RequestBody AddCommentRequest request) {
        User user = dal.findUser(request.userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Ticket ticket = getExistingTicket(request.ticketId);

        Comment comment = new Comment();
        comment.setTimestamp(System.currentTimeMillis());
        comment.setCreatorId(user.getId());
        comment.setTicketId(request.ticketId);
        comment.setText(request.text);
        long id = dal.createOrUpdateComment(comment);

        // Send notifications
        Map<Integer,User> toNotify = ticket.getComments().stream().map(Comment::getCreator).collect(Collectors.toMap(User::getId, u -> u));
        if (!toNotify.containsKey(ticket.getCreatorId())) {
            toNotify.put(ticket.getCreatorId(), ticket.getCreator());
        }

        toNotify.remove(user.getId());
        toNotify.forEach((k, u) -> notificationSender.onCommentAddedToTrackedTicket(u, ticket, comment));

        return id;
    }

    private Ticket getExistingTicket(int ticketId) {
        Ticket ticket = dal.findTicket(ticketId);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ticket;
    }
}
