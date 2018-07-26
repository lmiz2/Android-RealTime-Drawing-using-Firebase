package com.example.songhyeonseok.ccs;// 방 목록 탭에서, 방 하나하나를 나타내는 데이터의 구조를 정의하는 클래스.

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmiz2 on 2017-09-11.
 */

public class ListItemRoom {
    Drawable allplayer;
    String names;
    String date;
    String roomID;
    List<ListItemFriend> roomMembers;

    public ListItemRoom() {
        roomMembers = new ArrayList<ListItemFriend>();
    }

    public void addMember(ListItemFriend item){
        roomMembers.add(item);
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }


    public Drawable getAllplayer() {
        return allplayer;
    }

    public void setAllplayer(Drawable allplayer) {
        this.allplayer = allplayer;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
