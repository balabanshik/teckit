package com.example.teckit.comments;

import com.example.teckit.dao.*;
import com.example.teckit.tickets.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    @Mock
    DAL dal;

    @InjectMocks
    CommentController commentController;

    @Test
    public void addCommentNonexistingUser() {
        AddCommentRequest req = new AddCommentRequest();
        req.userId = 234;
        req.ticketId = 111;
        req.text = "foo bar";

        try {
            commentController.addComment(req);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void addCommentNonexistingTicket() {
        User u = generateUser(123,false);
        when(dal.findUser(123)).thenReturn(u);
        AddCommentRequest req = new AddCommentRequest();
        req.userId = 123;
        req.ticketId = 111;
        req.text = "foo bar";

        try {
            commentController.addComment(req);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void addCommentPassesToDAL() {
        User u = generateUser(123,false);
        when(dal.findUser(123)).thenReturn(u);
        AddCommentRequest req = new AddCommentRequest();
        req.userId = 123;
        req.ticketId = 111;
        req.text = "foo bar";
        Ticket t = generateTicket(111);
        when(dal.findTicket(111)).thenReturn(t);
        when(dal.createOrUpdateComment(any(Comment.class))).thenReturn(17L);

        long r = commentController.addComment(req);
        var captor = ArgumentCaptor.forClass(Comment.class);
        verify(dal, times(1)).createOrUpdateComment(captor.capture());
        assertEquals(17L, r);
        Comment rc = captor.getValue();
        assertEquals(123, rc.getCreatorId());
        assertEquals(111, rc.getTicketId());
        assertEquals("foo bar", rc.getText());
        assertTrue(Math.abs(System.currentTimeMillis() - rc.getTimestamp()) < 100);
    }

    //region Generators
    private User generateUser(int id, boolean isStaff) {
        User u = new User();
        u.setId(id);
        u.setName("foo" + id);
        u.setStaff(isStaff);

        return u;
    }

    private Ticket generateTicket(int id) {
        User u1 = new User();
        u1.setName("foo");
        User u2 = new User();
        u2.setName("baz");
        Ticket src = new Ticket();
        src.setId(id);
        src.setCreated(23);
        src.setCreatorId(314);
        src.setCreator(u1);
        src.setSubject("zyx");
        src.setStatus(TicketStatus.OPEN);
        src.setDescription("bar");
        src.setPriority(2);
        src.setTicketType(TicketType.LEAK);
        Comment c1 = new Comment();
        c1.setCreatorId(1);
        c1.setCreator(u1);
        c1.setTimestamp(11);
        c1.setText("aaa");
        Comment c2 = new Comment();
        c2.setCreatorId(2);
        c2.setCreator(u2);
        c2.setTimestamp(22);
        c2.setText("bbb");
        List<Comment> cl = new ArrayList<>();
        cl.add(c1);
        cl.add(c2);
        src.setComments(cl);

        return src;
    }
    //endregion
}