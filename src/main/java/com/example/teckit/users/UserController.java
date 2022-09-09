package com.example.teckit.users;

import com.example.teckit.dao.StudentData;
import com.example.teckit.dao.User;
import com.example.teckit.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/add")
    public AddResponse addUser(@RequestParam(value = "name") String name,
                               @RequestParam(value = "staff", defaultValue = "false") boolean isStaff,
                               @RequestParam(value = "bldg", defaultValue = "") String building,
                               @RequestParam(value = "room", defaultValue = "") String room,
                               @RequestParam(value = "bed", defaultValue = "") String bed) {

        User newUser = new User();
        newUser.setName(name);
        newUser.setStaff(isStaff);
        userRepository.save(newUser);

        if (!isStaff) {
            StudentData sd = new StudentData();

        }


        return new AddResponse(newUser.getId(), newUser.getName());
    }
}
