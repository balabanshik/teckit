package com.example.teckit.dao;

import com.example.teckit.tickets.TicketSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Component
@ParametersAreNonnullByDefault
public class DAL {
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final StudentDataRepository studentDataRepository;

    @Autowired
    public DAL(UserRepository userRepository,
               TicketRepository ticketRepository,
               CommentRepository commentRepository,
               StudentDataRepository studentDataRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.studentDataRepository = studentDataRepository;
    }

    public User findUser(int id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return null;
        }

        if (!user.isStaff()) {
            StudentData sd = studentDataRepository.findById(id).orElseThrow();
            user.setData(sd);
        }

        return user;
    }

    //region Tickets
    public int createOrUpdateTicket(Ticket ticket) {
        ticketRepository.save(ticket);

        return ticket.getId();
    }

    public void deleteTicket(int id) {
        ticketRepository.deleteById(id);
        commentRepository.deleteByTicketId(id);
    }

    public Ticket findTicket(int id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            return null;
        }

        ticket.setComments(commentRepository.findByTicketId(id));
        User creator = userRepository.findById(ticket.getCreatorId()).orElseThrow();
        ticket.setCreator(creator);

        return ticket;
    }

    public Page<Ticket> listAllTickets(int page, int pageSize, TicketSort sortOrder) {
        Sort sort = switch (sortOrder) {
            case PRIORITY -> Sort.by("priority");
            default -> Sort.by("created");
        };

        Pageable pageRequest = PageRequest.of(page, pageSize).withSort(sort);

        Page<Ticket> result = ticketRepository.findAll(pageRequest);

        for(Ticket ticket : result.getContent()) {
            ticket.setCreator(userRepository.findById(ticket.getCreatorId()).orElse(null));
        }

        return result;
    }

    public List<Ticket> listTicketsByCreator(int id) {
        List<Ticket> result = ticketRepository.findByCreatorId(id, Sort.by("created"));

        for(Ticket ticket : result) {
            ticket.setCreator(userRepository.findById(ticket.getCreatorId()).orElse(null));
        }

        return result;
    }
    //endregion

    public long createOrUpdateComment(Comment comment) {
        commentRepository.save(comment);
        return comment.getId();
    }
}
