package com.example.songhyeonseok.ccs; //친구목록,방목록,설정 탭을 모두 포함하는 탭 레이아웃을 품고있는 액티비티.

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


public class Start extends BaseActivity implements TabFragment1.tab1Listener,TabFragment2.tab2Listener,TabFragment3.tab3Listener{



    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;
    private FirebaseDatabase db;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference refDB,DB_friendList;
    TextView titleview;
    FloatingActionButton fab;
    int nowTab;
    Animation animdisappear,animappear;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start);
            setAppFontSize(Global_FontSize);

            // Adding Toolbar to the activity
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//고정
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            titleview = (TextView)findViewById(R.id.start_title);

            // Initializing the TabLayout
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            tabLayout.addTab(tabLayout.newTab().setText("친구").setIcon(R.drawable.user));
            tabLayout.addTab(tabLayout.newTab().setText("대화방").setIcon(R.drawable.chatting));
            tabLayout.addTab(tabLayout.newTab().setText("설정").setIcon(R.drawable.settings));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            // Initializing ViewPager
            viewPager = (ViewPager) findViewById(R.id.pager);

            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            db = FirebaseDatabase.getInstance();
            refDB = db.getReference("Members").child(mFirebaseUser.getUid()).child("connecting");
            refDB.onDisconnect().setValue(false);
            DB_friendList = db.getReference("Members");
//            DB_friendList = db.getReference("FriendList").child("FL_"+mFirebaseUser.getUid());
            FirebaseInstanceId.getInstance().getToken();
            refreshMyFL();

            titleview.setText(mFirebaseUser.getEmail());
            titleview.setTextColor(Color.DKGRAY);

            animdisappear = AnimationUtils.loadAnimation(this,R.anim.gone_to_right);
            animappear = AnimationUtils.loadAnimation(this,R.anim.from_right);

            // Creating TabPagerAdapter adapter
            pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            // Set TabSelectedListener
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    nowTab = tab.getPosition();
                    if(nowTab==0) {
                        if(fab.getVisibility()==View.GONE){
                            fab.setVisibility(View.VISIBLE);
                            fab.startAnimation(animappear);
                        }
                        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.people));
                    }else if(nowTab==1){
                        if(fab.getVisibility()==View.GONE){
                            fab.setVisibility(View.VISIBLE);
                            fab.startAnimation(animappear);
                        }
                        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.plus));
                    }else{
                        fab.startAnimation(animdisappear);
                        fab.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });



           fab = (FloatingActionButton) findViewById(R.id.fab);//초기화

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    if(nowTab==0) {
                       // DB_friendList.push().setValue("1");
                        intent = new Intent(getApplicationContext(), addFriend.class);
                    }else if(nowTab == 1){
                        intent = new Intent(getApplicationContext(), addRoom.class);
                    }else{
                        intent = new Intent(getApplicationContext(), Settings.class);
                    }

                    startActivityForResult(intent,0);//액티비티 새로 여는 부분
                }
            });
            pagerAdapter.notifyDataSetChanged();//액티비티에 실제로 나타나는 부분

        }


        public void refreshMyFL(){
            DB_friendList.child(mFirebaseUser.getUid()).child("FriendList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        final String tmp = dataSnapshot1.child("user_id").getValue(String.class);
                        DB_friendList.child(tmp).child("connecting").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DB_friendList.child(mFirebaseUser.getUid()).child("FriendList").child(tmp).child("connecting").setValue(dataSnapshot.getValue(boolean.class));
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    @Override
    public void onResume() {
        super.onResume();
        pagerAdapter.notifyDataSetChanged();
        refDB.setValue(true);
    }




    @Override
    public void communication(String a) {
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public String communication2(String a) {

        return "ok";
    }


    @Override
    public void communication3(int size) {
        setAppFontSize(size);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //FirebaseDatabase.getInstance().goOffline();
        refDB.setValue(false);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        refDB.setValue(false);
    }
}

