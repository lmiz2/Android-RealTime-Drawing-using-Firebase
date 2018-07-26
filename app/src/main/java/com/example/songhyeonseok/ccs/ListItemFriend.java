package com.example.songhyeonseok.ccs;//첫번째 탭인 친구탭에서 나타내어지는 친구 한줄한줄을 나타내는 데이터구조를 정의하는 클래스.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by SongHyeonSeok on 2017-08-17.
 */

public class ListItemFriend {
    private Drawable picture;
    private String userName;
    private String uid;
    private String notice;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public Drawable getPicture() {
        return picture;
    }


    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public void setPictureBitmap(Bitmap picture) {
        this.picture = new BitmapDrawable(picture);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}
