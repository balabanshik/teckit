package com.example.teckit.comments;

import com.example.teckit.dao.DAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.ParametersAreNonnullByDefault;

@RestController
@RequestMapping(path="/comments")
@ParametersAreNonnullByDefault
public class CommentController {
    @Autowired
    private DAL dal;


}
