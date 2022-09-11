package com.example.teckit.tickets;

import com.example.teckit.dao.DAL;
import com.example.teckit.dao.Ticket;
import com.example.teckit.dao.TicketType;
import com.example.teckit.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.ParametersAreNonnullByDefault;

@RestController
@RequestMapping(path="/tickets")
@ParametersAreNonnullByDefault
public class TicketController {
    private static final int PAGE_SIZE = 50;

    @Autowired
    private DAL dal;

    @PostMapping("/add")
    public @ResponseBody int createTicket(@RequestBody CreateTicketRequest request) {
        User creator = dal.findUser(request.userId);
        if (creator == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Ticket newTicket = new Ticket();
        newTicket.setCreatorId(creator.getId());
        newTicket.setCreated(System.currentTimeMillis());
        newTicket.setSubject(request.subject);
        newTicket.setDescription(request.description);
        newTicket.setStatus(TicketStatus.OPEN);
        try {
            newTicket.setTicketType(TicketType.valueOf(request.ticketType));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        newTicket.setPriority(normalizePriority(request.priority));

        return dal.createOrUpdateTicket(newTicket);
    }

    @GetMapping("/read")
    public @ResponseBody
    ReadTicketResponse readTicket(@RequestParam(value = "user") int userId,
                                  @RequestParam(value = "ticket") int ticketId) {
        Ticket ticket = getExistingTicket(ticketId);

        if (ticket.getCreatorId() != userId) {
            User caller = dal.findUser(userId);
            if (!caller.isStaff()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        return new ReadTicketResponse(ticket);
    }

    @GetMapping("/list")
    public @ResponseBody
    ListTicketsResponse listByCreator(@RequestParam(value = "user") int userId,
                                      @RequestParam(value = "by", defaultValue = "") String creatorStr) {
        int filterId = userId;
        if (creatorStr.length() > 0) {
            try {
                filterId = Integer.parseInt(creatorStr);
            } catch (NumberFormatException ignored) {}
        }

        if (filterId != userId) {
            verifyStaff(userId);
        }

        return new ListTicketsResponse(dal.listTicketsByCreator(filterId));
    }

    @GetMapping("/listall")
    public @ResponseBody
    ListTicketsResponse listAll(@RequestParam(value = "user") int userId,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "sort", defaultValue = "time") String sort) {
        TicketSort sortOrder = switch(sort.toLowerCase()) {
            case "priority" -> TicketSort.PRIORITY;
            default -> TicketSort.TIME;
        };

        verifyStaff(userId);
        Page<Ticket> resultPage = dal.listAllTickets(page, PAGE_SIZE, sortOrder);
        return new ListTicketsResponse(resultPage.getContent(), (int)resultPage.getTotalElements(), resultPage.getNumber(), resultPage.getTotalPages());
    }

    @PostMapping("/update")
    public @ResponseBody int updateTicket(@RequestParam(value = "user") int userId,
                                          @RequestParam(value = "ticket") int ticketId,
                                          @RequestParam(value = "pri") int priority) {
        verifyStaff(userId);
        Ticket ticket = getExistingTicket(ticketId);

        int newPriority = normalizePriority(priority);
        if (newPriority != ticket.getPriority()) {
            ticket.setPriority(newPriority);
            dal.createOrUpdateTicket(ticket);
        }

        return ticket.getId();
    }

    @PostMapping("/close")
    public @ResponseBody int closeTicket(@RequestParam(value = "user") int userId,
                                          @RequestParam(value = "ticket") int ticketId,
                                          @RequestParam(value = "complete") boolean complete) {
        verifyStaff(userId);
        Ticket ticket = getExistingTicket(ticketId);

        TicketStatus newStatus = complete ? TicketStatus.COMPLETE : TicketStatus.REJECTED;
        if (newStatus != ticket.getStatus()) {
            ticket.setStatus(newStatus);
            dal.createOrUpdateTicket(ticket);
        }

        return ticket.getId();
    }

    @DeleteMapping("/delete")
    public @ResponseBody int deleteTicket(@RequestParam(value = "user") int userId,
                                          @RequestParam(value = "ticket") int ticketId) {
        User user = dal.findUser(userId);
        if (user == null || !user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        getExistingTicket(ticketId);

        dal.deleteTicket(ticketId);

        return ticketId;
    }

    private Ticket getExistingTicket(int ticketId) {
        Ticket ticket = dal.findTicket(ticketId);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ticket;
    }

    private User verifyStaff(int userId) {
        User user = dal.findUser(userId);
        if (user == null || !user.isStaff()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return user;
    }

    private int normalizePriority(int priority) {
        return (priority <= 0 || priority > 5) ? 5 : priority;
    }
}
