package com.example.teckit.tickets;

import com.example.teckit.dao.DAL;
import com.example.teckit.dao.Ticket;
import com.example.teckit.dao.TicketType;
import com.example.teckit.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
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
        try {
            newTicket.setRequestType(TicketType.valueOf(request.ticketType));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (request.priority <= 0 || request.priority > 5) {
            request.priority = 5;
        }

        newTicket.setPriority(request.priority);

        return dal.createTicket(newTicket);
    }

    @GetMapping("/read")
    public @ResponseBody
    ReadTicketResponse readTicket(@RequestParam(value = "user") int userId,
                                  @RequestParam(value = "request") int requestId) {
        Ticket ticket = dal.findTicket(requestId);

        if (ticket.getCreatorId() != userId) {
            User caller = dal.findUser(userId);
            if (!caller.isStaff()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        return new ReadTicketResponse(ticket);
    }

    @GetMapping("/listby")
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
            User caller = dal.findUser(userId);
            if (caller == null || !caller.isStaff()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        return new ListTicketsResponse(dal.listTicketsByCreator(filterId));
    }

    @GetMapping("/listall")
    public @ResponseBody
    ListTicketsResponse listAll(@RequestParam(value = "user") int userId,
                                @RequestParam(value = "page", defaultValue = "0") int page) {


        return null;
    }
}
