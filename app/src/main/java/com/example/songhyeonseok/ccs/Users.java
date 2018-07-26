package com.example.songhyeonseok.ccs;//안쓰는놈

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmiz2 on 2017-09-18.
 */

public class Users {

    public List<User> users;


    public Users() {
        this.users = new ArrayList<User>();
    }

    public boolean findUsers(User u) {
        return users.contains(u);
    }

    public boolean findUserForUID(String s){
        for(User user : users) {

            String element = user.getUser_id();
            Log.d("배열 확인중 ",element+" & "+s);
            if(element.equals(s)){
                Log.d("배열 확인중 ",s + "는 "+element.equals(s));
                return true;
            }
        }
        return false;
    }

    public void setFrienditemByUID(String uid, ListItemFriend item){
        for(int i = 0; i < users.size(); i++){
            User u = users.get(i);
            String tmp = u.getUser_id();
            if(tmp.equals(uid)){
                u.addListItemFriend(item);
            }
        }
    }

    public void setRoomitemByUID(String uid, ListItemRoom item){
        for(int i = 0; i < users.size(); i++){
            User u = users.get(i);
            String tmp = u.getUser_id();
            if(tmp.equals(uid)){
                u.addListItemRoom(item);
            }
        }
    }

    public List<ListItemFriend> getFriendList(String uid){
        for(int i = 0; i < users.size(); i++){
            User u = users.get(i);
            String tmp = u.getUser_id();
            Log.d("@@@@@@@",i+"확인"+tmp);
            if(tmp.equals(uid)){
                Log.d("@@@@@@@","hit!");
                return u.getFriends();
            }
        }
        Log.d("@@@@@@@","null을 반환");
        return null;
    }

    public List<ListItemRoom> getRoomList(String uid){
        for(int i = 0; i < users.size(); i++){
            User u = users.get(i);
            String tmp = u.getUser_id();
            if(tmp.equals(uid)){
                return u.getParticipate_room();
            }
        }
        return null;
    }

    public void addUser(String name, String uid, String email) {
        User user = new User();
        user.setUser_name(name);
        user.setUser_id(uid);
        user.setUser_email(email);
        this.users.add(user);
    }

    public void addUser(User user){
        this.users.add(user);
    }

    public void del_user(User u){
        users.remove(u);
    }
}
