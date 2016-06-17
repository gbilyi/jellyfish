package com.gordbilyi.jellyfish.im.domain;

/**
 * Created by gordbilyi on 4/29/16.
 */
public class Chat {

    private long id;
    private String from; // is needed in case of multi chat
    private String to;
    private String group;
    private long timestamp;

    private org.jivesoftware.smack.chat.Chat smackChat;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
//        return "\nChat{" +
//                "id=" + id +
//                ", from='" + from + '\'' +
//                ", to='" + to + '\'' +
//                ", group='" + group + '\'' +
//                ", timestamp=" + timestamp +
//                '}';
        return "\nChat with " + getTo();
    }

    public org.jivesoftware.smack.chat.Chat getSmackChat() {
        return smackChat;
    }

    public void setSmackChat(org.jivesoftware.smack.chat.Chat smackChat) {
        this.smackChat = smackChat;
    }
}
