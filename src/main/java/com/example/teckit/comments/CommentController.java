package com.example.teckit.comments;

import com.example.teckit.dao.Comment;
import com.example.teckit.dao.DAL;
import com.example.teckit.dao.Ticket;
import com.example.teckit.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.ParametersAreNonnullByDefault;

@RestController
@RequestMapping(path="/comments")
@ParametersAreNonnullByDefault
public class CommentController {
    @Autowired
    private DAL dal;

    @PostMapping("/add")
    public @ResponseBody long addComment(@RequestBody AddCommentRequest request) {
        User user = dal.findUser(request.userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        getExistingTicket(request.ticketId);

        Comment comment = new Comment();
        comment.setTimestamp(System.currentTimeMillis());
        comment.setCreatorId(user.getId());
        comment.setTicketId(request.ticketId);
        comment.setText(request.text);
        return dal.createOrUpdateComment(comment);
    }

    private Ticket getExistingTicket(int ticketId) {
        Ticket ticket = dal.findTicket(ticketId);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ticket;
    }
}
