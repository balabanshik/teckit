package com.example.teckit.comments;

import com.example.teckit.dao.*;
import com.example.teckit.external.NotificationSender;
import com.example.teckit.tickets.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    @Mock
    DAL dal;

    @Mock
    NotificationSender notificationSender;

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

    @Test
    public void addCommentSendsNotifications() {
        User u = generateUser(2,false);
        when(dal.findUser(2)).thenReturn(u);
        AddCommentRequest req = new AddCommentRequest();
        req.userId = 2;
        req.ticketId = 111;
        req.text = "foo bar";
        Ticket t = generateTicket(111);
        when(dal.findTicket(111)).thenReturn(t);
        when(dal.createOrUpdateComment(any(Comment.class))).thenReturn(17L);

        long r = commentController.addComment(req);
        var cu = ArgumentCaptor.forClass(User.class);
        var ct = ArgumentCaptor.forClass(Ticket.class);
        var cc = ArgumentCaptor.forClass(Comment.class);
        // Should be called 2 times, for users 314 (creator) and 1 (another commenter); 2 is not called
        verify(notificationSender, times(2)).onCommentAddedToTrackedTicket(cu.capture(), ct.capture(), cc.capture());
        assertTrue(ct.getAllValues().stream().allMatch(i -> t == i));
        assertTrue(cc.getAllValues().stream().allMatch(c -> c.getText().equals("foo bar")));
        var idSet = cu.getAllValues().stream().map(User::getId).collect(Collectors.toSet());
        assertTrue(idSet.contains(314));
        assertTrue(idSet.contains(1));
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
        User u0 = new User();
        u0.setId(314);
        u0.setName("creator");
        User u1 = new User();
        u1.setId(1);
        u1.setName("foo");
        User u2 = new User();
        u2.setId(2);
        u2.setName("baz");
        Ticket src = new Ticket();
        src.setId(id);
        src.setCreated(23);
        src.setCreatorId(314);
        src.setCreator(u0);
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