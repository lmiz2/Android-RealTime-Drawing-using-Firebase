package com.example.songhyeonseok.ccs;//친구 목록 탭을 담당하는 프래그먼트.

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.songhyeonseok.ccs.Start.Global_FontSize;


public class TabFragment1 extends GlobalFrag implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private MyListViewAdapter mAdapter;
    tab1Listener mCallback;
    private FirebaseDatabase db;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference ref;
    String myUID;
    ListItemFriend item;
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView","");
        context = getActivity();
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        item = new ListItemFriend();
        myUID = mFirebaseUser.getUid();
        ref = db.getReference().child("Members").child(myUID).child("FriendList");

        mListView = (ListView) view.findViewById(R.id.list_view1);
        mAdapter = new MyListViewAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAdapter.clear_item();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if(user.isConnecting()){
                        mAdapter.additem(ContextCompat.getDrawable(context, R.drawable.user_on), user.getUser_name(), user.getUser_email());
                    }else{
                        mAdapter.additem(ContextCompat.getDrawable(context, R.drawable.user), user.getUser_name(), user.getUser_email());
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        return view;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }


    public interface tab1Listener{
        void communication(String a);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (tab1Listener)getActivity();
        }catch(ClassCastException e){
            System.out.println("에러 : "+ e.toString());
            Log.d("에러 : ",getActivity().toString()+" 는 반드시 tab1Listener를 implements 해야 합니다.");
        }
    }

}
