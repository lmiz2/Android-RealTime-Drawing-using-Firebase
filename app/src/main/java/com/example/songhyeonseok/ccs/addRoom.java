package com.example.songhyeonseok.ccs;// 방 만들기 버튼을 눌렀을때 나타나는 창을 담당하는 액티비티

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class addRoom extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private CheckableFriendAdapter mAdapter;
    private FirebaseDatabase db;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference ref;
    EditText roomTitle;
    String myUID;
    ListItemFriend item;
    long now;
    Date date;
    SimpleDateFormat sdf;
    String getTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_room);
        setAppFontSize(Global_FontSize);

        Button chatt_start = (Button)findViewById(R.id.chatt_start);
        now = System.currentTimeMillis();
        date = new Date(now);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        getTime = sdf.format(date);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        item = new ListItemFriend();
        myUID = mFirebaseUser.getUid();
        ref = db.getReference().child("Members").child(myUID).child("FriendList");

        roomTitle = (EditText)findViewById(R.id.roomtitle);
        mListView = (ListView)findViewById(R.id.Friendlist_for_makeRoom);
        mAdapter = new CheckableFriendAdapter(getApplicationContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAdapter.clear_item();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mAdapter.additem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user), user.getUser_name(), user.getUser_email(),user.getUser_id());

                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomkey = db.getReference().child("Rooms").push().getKey();
                ListItemRoom tmpitem_room = new ListItemRoom();
                tmpitem_room.setRoomID(roomkey);
                tmpitem_room.setNames(roomTitle.getText().toString());
                tmpitem_room.setDate(getTime);
                db.getReference().child("Rooms").child(roomkey).setValue(tmpitem_room);
                List<ListItemFriend> tmp = mAdapter.getCheckedItems();
                for(int i = 0; i < tmp.size(); i++){
                    db.getReference().child("Members").child(tmp.get(i).getUid()).child("RoomList").child(roomkey).setValue(tmpitem_room);
                    db.getReference().child("Rooms").child(roomkey).child("RoomMember").child(tmp.get(i).getUid()).setValue(tmp.get(i));
                }
                Intent intent = new Intent(getApplicationContext(),ChattRoom.class);
                intent.putExtra("roomkey",roomkey);
                intent.putExtra("titlename",roomTitle.getText().toString());
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
