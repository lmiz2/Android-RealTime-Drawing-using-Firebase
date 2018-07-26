package com.example.songhyeonseok.ccs;// 방 목록탭에서 방 하나하나를 추가,삭제하고 화면을 초기화시켜 실시간으로 목록이 갱신되게하는 어댑터.

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by lmiz2 on 2017-09-11.
 */

public class RoomListAdapter extends BaseAdapter {
    private ArrayList<ListItemRoom> roomsArrayList = new ArrayList<ListItemRoom>();
    private Context con;

    public RoomListAdapter(Context con) {
        this.con = con;
    }

    @Override
    public int getCount() {
        return roomsArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_room,parent,false);

        }

        ImageView img = (ImageView) convertView.findViewById(R.id.imageView);
        TextView names = (TextView) convertView.findViewById(R.id.names);
        TextView date = (TextView)convertView.findViewById(R.id.create_date);

        ListItemRoom listItemRoom = roomsArrayList.get(position);

        img.setImageDrawable(listItemRoom.getAllplayer());
        names.setText(listItemRoom.getNames());
        date.setText(listItemRoom.getDate());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ChattRoom.class);
                intent.putExtra("titlename",roomsArrayList.get(pos).getNames());
                intent.putExtra("roomkey",roomsArrayList.get(pos).getRoomID());
                context.startActivity(intent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder
                        .setMessage("이 방을 목록에서 삭제할까요?")
                        .setCancelable(false).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("삭제",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        FirebaseDatabase.getInstance().getReference().child("Members").child(uid).child("RoomList").child(roomsArrayList.get(pos).getRoomID()).removeValue();
                                        FirebaseDatabase.getInstance().getReference().child("Rooms").child(roomsArrayList.get(pos).getRoomID()).child("RoomMember").child(uid).removeValue();
                                        Log.d("r uid : ",roomsArrayList.get(pos).getRoomID());
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
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
        return roomsArrayList.get(position);
    }

    public void additem(Drawable picture, String name, String date, String roomId){
        ListItemRoom lir = new ListItemRoom();

        lir.setAllplayer(picture);
        lir.setNames(name);
        lir.setDate(date);
        lir.setRoomID(roomId);

        roomsArrayList.add(lir);
        notifyDataSetChanged();
    }

    public void clear_item(){
        roomsArrayList.clear();
        notifyDataSetChanged();
    }
}
