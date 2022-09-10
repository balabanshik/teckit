package com.example.teckit.tickets;

import com.example.teckit.dao.Ticket;

import java.util.List;
import java.util.stream.Collectors;

public class ListTicketsResponse {
    public int totalCount;
    public int pageCount;
    public int pageNo;
    public List<ResponseEntry> tickets;

    public ListTicketsResponse(List<Ticket> tickets) {
        this.totalCount = tickets.size();
        this.pageCount = this.totalCount > 0 ? 1 : 0;
        this.pageNo = 0;

        this.tickets = tickets.stream().map(ResponseEntry::new).collect(Collectors.toList());
    }

    public ListTicketsResponse(List<Ticket> tickets, int totalCount, int pageNo, int pageCount) {
        this(tickets);
        this.totalCount = totalCount;
        this.pageCount = pageCount;
        this.pageNo = pageNo;
    }
}
