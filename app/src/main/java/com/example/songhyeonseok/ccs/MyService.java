package com.example.songhyeonseok.ccs;//알림 기능을 담당하는 안드로이드 서비스 컴포넌트. 설정탭에서 알림을 체크하면 호출된다.

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.songhyeonseok.ccs.R;
import com.example.songhyeonseok.ccs.Start;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service {
    NotificationManager Notifi_M;
    Notification Notifi;
    private FirebaseDatabase db;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference ref;
    String myUID;
    int showCnt = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        myUID = mFirebaseUser.getUid();
        ref = db.getReference().child("Members").child(myUID).child("RoomList");
//    ref.onDisconnect().
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ListItemRoom tmp_item = dataSnapshot.getValue(ListItemRoom.class);
                showNotifi(tmp_item.getNames(), tmp_item.getRoomID());
                Log.d("알림 로그", "onChildAdded: "+s+" "+
                        dataSnapshot.exists());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }


    public void showNotifi(String roomname,String roomId){


        Intent intent = new Intent(MyService.this, ChattRoom.class);
        intent.putExtra("titlename",roomname);
        intent.putExtra("roomkey",roomId);

        PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notifi = new Notification.Builder(getApplicationContext())
                .setContentTitle("방이 생겼습니다")
                .setContentText("방 이름 : "+roomname)
                .setSmallIcon(R.mipmap.css_icon)
                .setTicker("알림!!!")
                .setContentIntent(pendingIntent)
                .build();

        //소리추가
        Notifi.defaults =Notification.DEFAULT_SOUND;

        //알림 소리를 한번만 내도록
        Notifi.flags =Notification.FLAG_ONLY_ALERT_ONCE;

        //확인하면 자동으로 알림이 제거 되도록
        Notifi.flags =Notification.FLAG_AUTO_CANCEL;
        Notifi_M.notify(777,Notifi);

    }
}
