package com.example.teckit.tickets;

import com.example.teckit.dao.Comment;
import com.example.teckit.dao.Ticket;
import com.example.teckit.dao.TicketType;
import com.example.teckit.dao.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReadTicketResponseTest {
    @Test
    public void createsFromDAO() {
        User u1 = new User();
        u1.setName("foo");
        User u2 = new User();
        u2.setName("baz");
        Ticket src = new Ticket();
        src.setId(123);
        src.setCreated(23);
        src.setCreatorId(314);
        src.setCreator(u1);
        src.setDescription("bar");
        src.setPriority(2);
        src.setSubject("plugh");
        src.setStatus(TicketStatus.COMPLETE);
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

        ReadTicketResponse r = new ReadTicketResponse(src);
        assertEquals(123, r.getId());
        assertEquals(23, r.getTimestamp());
        assertEquals("foo", r.getCreator());
        assertEquals("bar", r.getDescription());
        assertEquals(2, r.getPriority());
        assertEquals("plugh", r.getSubject());
        assertEquals("COMPLETE", r.getStatus());
        assertEquals("LEAK", r.getRequestType());
        assertEquals(2, r.getComments().size());
        assertEquals(11, r.getComments().get(0).getTimestamp());
        assertEquals("foo", r.getComments().get(0).getCreator());
        assertEquals("aaa", r.getComments().get(0).getText());
        assertEquals(22, r.getComments().get(1).getTimestamp());
        assertEquals("baz", r.getComments().get(1).getCreator());
        assertEquals("bbb", r.getComments().get(1).getText());
    }
}