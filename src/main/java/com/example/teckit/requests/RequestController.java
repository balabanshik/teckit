package com.example.teckit.requests;

import com.example.teckit.users.AddResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path="/requests")
public class RequestController {
    @PostMapping("/add")
    public @ResponseBody AddResponse addRequest(@RequestParam(value = "user") int creatorId,
                                @RequestParam(value = "subject") String subject,
                                @RequestParam(value = "type") String issueType,
                                @RequestParam(value = "pri") int priority,
                                @RequestParam(value = "desc") String description
                                ) {
        return null;
    }

    @GetMapping("/read")
    public @ResponseBody ReadResponse readRequest(@RequestParam(value = "user") int userId,
                                    @RequestParam(value = "request") int requestId) {
//        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return new ReadResponse(null);

    }
}
