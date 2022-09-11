package com.example.teckit.tickets;

import com.example.teckit.dao.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {
    @Mock
    DAL dal;

    @InjectMocks
    TicketController ticketController;

    //region List by id
    @Test
    public void listByIdListsOwn() {
        List<Ticket> li = new ArrayList<>();
        li.add(generateTicket(10));
        li.add(generateTicket(11));
        when(dal.listTicketsByCreator(314)).thenReturn(li);

        ListTicketsResponse r = ticketController.listByCreator(314, "");
        assertEquals(2, r.totalCount);
        assertEquals(2, r.tickets.size());
        assertEquals(10, r.tickets.get(0).getId());
        assertEquals(11, r.tickets.get(1).getId());
    }

    @Test
    public void listByIdProhibitsOthers() {
        User u = generateUser(666,false);
        when(dal.findUser(666)).thenReturn(u);

        try {
            ticketController.listByCreator(666, "123");
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void listByIdListsOthersForAdmin() {
        User u = generateUser(555,true);
        when(dal.findUser(555)).thenReturn(u);
        List<Ticket> li = new ArrayList<>();
        li.add(generateTicket(10));
        li.add(generateTicket(11));
        when(dal.listTicketsByCreator(314)).thenReturn(li);

        ListTicketsResponse r = ticketController.listByCreator(555, "314");
        assertEquals(2, r.totalCount);
        assertEquals(2, r.tickets.size());
        assertEquals(10, r.tickets.get(0).getId());
        assertEquals(11, r.tickets.get(1).getId());
    }
    //endregion

    //region List all
    @Test
    public void listAllNotForStudents() {
        User u = generateUser(666,false);
        when(dal.findUser(666)).thenReturn(u);

        try {
            ticketController.listAll(666, 3, "");
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void listAllPassesParamsAndReturnsResult() {
        User u = generateUser(555,true);
        when(dal.findUser(555)).thenReturn(u);
        Page<Ticket> p = generatePage();
        when(dal.listAllTickets(7, 50, TicketSort.PRIORITY)).thenReturn(p);

        ListTicketsResponse r = ticketController.listAll(555, 7, "priority");
        assertEquals(17, r.totalCount);
        assertEquals(5, r.pageCount);
        assertEquals(47, r.pageNo);
        assertEquals(2, r.tickets.size());
        assertEquals(10, r.tickets.get(0).getId());
        assertEquals(11, r.tickets.get(1).getId());
    }

    //endregion

    //region Add Ticket
    @Test
    public void addRequestInvalidUser() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.userId = 1234;
        request.ticketType = "LEAK";
        request.priority = 4;
        request.subject = "foo";
        request.description = "bar";

        try {
            ticketController.createTicket(request);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void addRequestPriorityCappedTo5() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.userId = 123;
        request.ticketType = "LEAK";
        request.priority = 7;  // has to be 1-5
        request.subject = "foo";
        request.description = "bar";
        User u = generateUser(123, false);
        when(dal.findUser(123)).thenReturn(u);
        when(dal.createOrUpdateTicket(any(Ticket.class))).thenReturn(1);

        var captor = ArgumentCaptor.forClass(Ticket.class);
        ticketController.createTicket(request);
        verify(dal, times(1)).createOrUpdateTicket(captor.capture());
        Ticket rc = captor.getValue();
        assertEquals(5, rc.getPriority());
    }

    @Test
    public void addRequestInvalidRequestType() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.userId = 123;
        request.ticketType = "BAD_TYPE";
        request.priority = 3;
        request.subject = "foo";
        request.description = "bar";
        User u = generateUser(123, false);
        when(dal.findUser(123)).thenReturn(u);

        try {
            ticketController.createTicket(request);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void addRequestAdds() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.userId = 123;
        request.ticketType = "LEAK";
        request.priority = 2;
        request.subject = "foo";
        request.description = "bar";
        User u = generateUser(123, false);
        when(dal.findUser(123)).thenReturn(u);
        when(dal.createOrUpdateTicket(any(Ticket.class))).thenReturn(17);

        var captor = ArgumentCaptor.forClass(Ticket.class);
        int r = ticketController.createTicket(request);
        verify(dal, times(1)).createOrUpdateTicket(captor.capture());
        Ticket rc = captor.getValue();
        assertEquals(123, rc.getCreatorId());
        assertEquals(TicketType.LEAK, rc.getTicketType());
        assertEquals(2, rc.getPriority());
        assertEquals("foo", rc.getSubject());
        assertEquals("bar", rc.getDescription());
        assertEquals(TicketStatus.OPEN, rc.getStatus());
        assertEquals(17, r);
    }
    //endregion

    //region Read Ticket
    @Test
    public void readRequestCallsDALForRequest() {
        Ticket src = generateTicket(123);
        when(dal.findTicket(123)).thenReturn(src);

        ReadTicketResponse r = ticketController.readTicket(314, 123);
        verify(dal, times(1)).findTicket(123);
        assertEquals(23, r.getTimestamp());
    }

    @Test
    public void readRequestAllowsForOwnerAndAdmin() {
        Ticket src = generateTicket(234);
        User u = generateUser(314,false);
        User ua = generateUser(456,true);
        when(dal.findTicket(234)).thenReturn(src);
        when(dal.findUser(456)).thenReturn(ua);

        ReadTicketResponse r = ticketController.readTicket(314, 234);
        assertEquals(234, r.getId());

        r = ticketController.readTicket(456, 234);
        verify(dal, times(1)).findUser(456);
        assertEquals(234, r.getId());
    }

    @Test
    public void readRequestFailsNotOwner() {
        Ticket src = generateTicket(123);
        User u = generateUser(666,false);
        when(dal.findTicket(123)).thenReturn(src);
        when(dal.findUser(666)).thenReturn(u);

        try {
            ticketController.readTicket(666, 123);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void readFailsIfNoTicket() {
        try {
            ticketController.readTicket(123, 123);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            return;
        }

        fail();
    }

    //endregion

    //region Update ticket

    @Test
    public void updateFailsForNotStaff() {
        User u = generateUser(666,false);
        when(dal.findUser(666)).thenReturn(u);

        try {
            ticketController.updateTicket(666, 123, 3);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void updateFailsIfNoTicket() {
        User u = generateUser(123,true);
        when(dal.findUser(123)).thenReturn(u);
        try {
            ticketController.updateTicket(123, 123, 0);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void updateChangesPriorityAndSaves() {
        User u = generateUser(555,true);
        when(dal.findUser(555)).thenReturn(u);
        Ticket t = generateTicket(123);
        when(dal.findTicket(123)).thenReturn(t);

        ticketController.updateTicket(555, 123, 1);
        var captor = ArgumentCaptor.forClass(Ticket.class);
        verify(dal, times(1)).createOrUpdateTicket(captor.capture());
        Ticket rc = captor.getValue();
        assertEquals(t, rc);
        assertEquals(1, rc.getPriority());
    }
    //endregion

    //region Close
    @Test
    public void closeFailsForNotStaff() {
        User u = generateUser(666,false);
        when(dal.findUser(666)).thenReturn(u);

        try {
            ticketController.closeTicket(666, 123, true);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void closeFailsIfNoTicket() {
        User u = generateUser(123,true);
        when(dal.findUser(123)).thenReturn(u);
        try {
            ticketController.closeTicket(123, 123, true);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void closeChangesStatusAndSaves() {
        User u = generateUser(555,true);
        when(dal.findUser(555)).thenReturn(u);
        Ticket t = generateTicket(123);
        when(dal.findTicket(123)).thenReturn(t);

        ticketController.closeTicket(555, 123, true);
        var captor = ArgumentCaptor.forClass(Ticket.class);
        verify(dal, times(1)).createOrUpdateTicket(captor.capture());
        Ticket rc = captor.getValue();
        assertEquals(t, rc);
        assertEquals(TicketStatus.COMPLETE, rc.getStatus());
    }
    //endregion

    //region Delete ticket
    @Test
    public void deleteFailsForNotAdmin() {
        User u = generateUser(666,true);
        when(dal.findUser(666)).thenReturn(u);

        try {
            ticketController.deleteTicket(666, 123);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void deleteFailsIfNoTicket() {
        User u = generateUser(123,true);
        u.setAdmin(true);
        when(dal.findUser(123)).thenReturn(u);
        try {
            ticketController.deleteTicket(123, 123);
        } catch(ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            return;
        }

        fail();
    }

    @Test
    public void deleteDeletesTicket() {
        User u = generateUser(555,true);
        u.setAdmin(true);
        when(dal.findUser(555)).thenReturn(u);
        Ticket t = generateTicket(123);
        when(dal.findTicket(123)).thenReturn(t);

        ticketController.deleteTicket(555, 123);
        verify(dal, times(1)).deleteTicket(123);
    }
    //endregion

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

    private Page<Ticket> generatePage() {
        List<Ticket> li = new ArrayList<>();
        li.add(generateTicket(10));
        li.add(generateTicket(11));
        return new Page<>() {
            @Override
            public int getTotalPages() {
                return 5;
            }

            @Override
            public long getTotalElements() {
                return 17;
            }

            @Override
            public <U> Page<U> map(Function<? super Ticket, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 47;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Ticket> getContent() {
                return li;
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Ticket> iterator() {
                return null;
            }
        };
    }

    //endregion
}