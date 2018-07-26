package com.example.songhyeonseok.ccs;//addRoom 액티비티에서, 친구를 체크하여 추가할 때 체크가 되었거나 안된 친구를 카운트하여 정보를 넘기는 어댑터.

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmiz2 on 2017-10-15.
 */

public class CheckableFriendAdapter extends BaseAdapter {
    private ArrayList<ListItemFriend> friendArrayList = new ArrayList<ListItemFriend>();
    private Context con;
    List<ListItemFriend> results ;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    int cnt = 0;

    public CheckableFriendAdapter(Context context) {
        con = context;
        results = new ArrayList<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        ListItemFriend tmpitem = new ListItemFriend();
        tmpitem.setPicture(null);
        tmpitem.setUserName(mFirebaseUser.getDisplayName());
        tmpitem.setNotice(mFirebaseUser.getEmail());
        tmpitem.setUid(mFirebaseUser.getUid());
        results.add(tmpitem);
    }

    @Override
    public int getCount(){
        return friendArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.checkable_item_friend,parent,false);

        }

        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox_frd);
        TextView name = (TextView) convertView.findViewById(R.id.user_name2);
        //TextView notice = (TextView)convertView.findViewById(R.id.state_notice2);


        final ListItemFriend listItemFriend = friendArrayList.get(position);
        name.setText(listItemFriend.getUserName());
        //notice.setText(listItemFriend.getNotice());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    results.add(listItemFriend);
                }else{
                    results.remove(listItemFriend);
                }
            }
        });

        return convertView;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public Object getItem(int position) {
        return friendArrayList.get(position);
    }

    public void additem(Drawable picture, String name, String notice, String Uid){
        ListItemFriend lif = new ListItemFriend();

        lif.setPicture(null);
        lif.setUserName(name);
        lif.setNotice(notice);
        lif.setUid(Uid);

        friendArrayList.add(lif);
        notifyDataSetChanged();

    }

    public void clear_item(){
        friendArrayList.clear();
        notifyDataSetChanged();
    }

    public List<ListItemFriend> getCheckedItems(){
        return results;
    }
}
