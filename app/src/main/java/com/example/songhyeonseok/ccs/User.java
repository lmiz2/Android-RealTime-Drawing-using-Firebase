package com.example.songhyeonseok.ccs;// firebase에 올려질 유저 데이터의 구조를 정의하는 클래스.

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmiz2 on 2017-09-18.
 */

public class User {

    String user_name;
    String user_id;
    String user_email;
    List<ListItemFriend> friends;
    List<ListItemRoom> participate_room;
    boolean connecting = false;

    public boolean isConnecting() {
        return connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    public User() {
        friends = new ArrayList<ListItemFriend>();
        participate_room = new ArrayList<ListItemRoom>();
    }

    public void addListItemFriend(ListItemFriend item){
        friends.add(item);
    }

    public void addListItemRoom(ListItemRoom item){
        participate_room.add(item);
    }

    public List<ListItemFriend> getFriends() {
        return friends;
    }

    public List<ListItemRoom> getParticipate_room() {
        return participate_room;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
