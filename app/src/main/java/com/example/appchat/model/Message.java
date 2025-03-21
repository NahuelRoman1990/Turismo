package com.example.appchat.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {

    public Message() {
        // Constructor vacío necesario para Parse
    }

    public String getContent() {
        return getString("content");
    }

    public void setContent(String content) {
        put("content", content);
    }

    public ParseUser getSender() {
        return getParseUser("sender");
    }

    public void setSender(ParseUser sender) {
        put("sender", sender);
    }

    public String getTimestamp() {
        return getString("timestamp");
    }

    public void setTimestamp(String timestamp) {
        put("timestamp", timestamp);
    }
}