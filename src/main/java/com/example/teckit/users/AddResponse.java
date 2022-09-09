package com.example.teckit.users;

public class AddResponse {
    private final long id;
    private final String content;

    public AddResponse(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
