package com.example.teckit.requests;

import com.example.teckit.dao.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    @Mock
    DAL dal;

    @InjectMocks
    RequestController requestController;

    @Test
    public void readRequestCallsDAL() {
        Request src = generateRequest();
        when(dal.findRequest(123)).thenReturn(src);

        ReadResponse r = requestController.readRequest(314, 123);
        assertEquals(23, r.getTimestamp());
    }

    private Request generateRequest() {
        User u1 = new User();
        u1.setName("foo");
        User u2 = new User();
        u2.setName("baz");
        Request src = new Request();
        src.setId(123);
        src.setCreated(23);
        src.setCreatorId(314);
        src.setCreator(u1);
        src.setDescription("bar");
        src.setPriority(2);
        src.setRequestType(RequestType.LEAK);
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
}