package com.example.songhyeonseok.ccs;//방 목록 탭을 담당하는 프래그먼트

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.songhyeonseok.ccs.Start.Global_FontSize;

public class TabFragment2 extends GlobalFrag implements TabFragment3.tab3Listener{

    private tab2Listener mCallback;
    private FirebaseDatabase db;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference ref;
    ListItemRoom item;
    private ListView mListView;
    private RoomListAdapter mAdapter;
    String roomname ="ggggg";
    String roomKey;
    String myUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment_2, container, false);
        db = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        myUID = mFirebaseUser.getUid();
        ref = db.getReference("Members").child(myUID).child("RoomList");
        item = new ListItemRoom();
        setGlobalFont(container,Global_FontSize);


        mListView = (ListView) v.findViewById(R.id.list_view_room);
        mAdapter = new RoomListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
       //mAdapter.additem(ContextCompat.getDrawable(getActivity(), R.drawable.user),roomname,"2017-09-11");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAdapter.clear_item();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ListItemRoom tmpitem = snapshot.getValue(ListItemRoom.class);
                    Log.d("findme",tmpitem.getNames());
                    mAdapter.additem(null,tmpitem.getNames(),tmpitem.getDate(),tmpitem.getRoomID());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }



    public interface tab2Listener{
        String communication2(String a);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (TabFragment2.tab2Listener)getActivity();
        }catch(ClassCastException e){
            System.out.println("에러 : "+ e.toString());
            Log.d("에러 : ",getActivity().toString()+" 는 반드시 tab1Listener를 implements 해야 합니다.");
        }
    }

    @Override
    public void communication3(int size) {
        setAppFontSize(size);
    }
}