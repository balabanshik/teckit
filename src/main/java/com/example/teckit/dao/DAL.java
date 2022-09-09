package com.example.teckit.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Component
@ParametersAreNonnullByDefault
public class DAL {
    private UserRepository userRepository;

    @Autowired
    public DAL(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(int id) {
        return null;
    }

    public Request findRequest(int id) {
        return null;
    }

    public List<Request> listRequests() {
        return null;
    }
}
