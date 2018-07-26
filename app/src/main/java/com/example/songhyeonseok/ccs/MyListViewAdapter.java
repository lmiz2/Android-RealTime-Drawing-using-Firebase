package com.example.songhyeonseok.ccs;// 친구 리스트를 관리하는 어댑터. ListitemFriend를 하나하나 추가하거나 삭제하고 화면을 초기화시키며, 실시간으로 화면을 갱신한다.

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.songhyeonseok.ccs.BaseActivity.Global_FontSize;

/**
 * Created by SongHyeonSeok on 2017-08-17.
 */

public class MyListViewAdapter extends BaseAdapter {
    private ArrayList<ListItemFriend> friendArrayList = new ArrayList<ListItemFriend>();
    private Context con;


    public MyListViewAdapter(Context context) {
        con = context;
    }

    @Override
    public int getCount(){
        return friendArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_friend,parent,false);
        }

        ImageView img = (ImageView) convertView.findViewById(R.id.picture);
        TextView name = (TextView) convertView.findViewById(R.id.user_name);
        name.setTextSize(Global_FontSize);
        TextView notice = (TextView)convertView.findViewById(R.id.state_notice);
        notice.setTextSize(Global_FontSize);

        ListItemFriend listItemFriend = friendArrayList.get(position);

        img.setImageDrawable(listItemFriend.getPicture());
        name.setText(listItemFriend.getUserName());
        notice.setText(listItemFriend.getNotice());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    public void additem(Drawable picture,String name, String notice){
        ListItemFriend lif = new ListItemFriend();

        lif.setPicture(picture);
        lif.setUserName(name);
      //  lif.setNotice(notice);

        friendArrayList.add(lif);
        notifyDataSetChanged();
    }

    public void clear_item(){
        friendArrayList.clear();
        notifyDataSetChanged();
    }
}
