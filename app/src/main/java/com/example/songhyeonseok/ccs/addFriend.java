package com.example.songhyeonseok.ccs; // 친구추가 버튼을 누를 때 나타나는 친구추가 창을 담당하는 액티비티

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addFriend extends BaseActivity {
    EditText edt;
    Button btn2,btn3;

    private FirebaseDatabase db;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference refDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friend);
        setLayout();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        refDB = db.getReference().child("Members");
        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.button2){
                    findForEmail(edt.getText().toString());
                }else{
                    finish();
                }
            }
        };
        btn2.setOnClickListener(btnListener);
        btn3.setOnClickListener(btnListener);


    }

    public void findForEmail(final String email){
        if(email.equals(mFirebaseUser.getEmail())){
            Toast.makeText(getApplicationContext(),"자신을 친구로 추가 할 수 없습니다.",Toast.LENGTH_SHORT).show();
            edt.setText("");
            return;
        }

        refDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User tmp = snapshot.getValue(User.class);
                    final String tmpkey = snapshot.getKey();
                    if(tmp.getUser_email().equals(email)){
                        Toast.makeText(getApplicationContext(),""+email.toString()+"님을 친구로 추가합니다.",Toast.LENGTH_SHORT).show();
                        refDB.child(mFirebaseUser.getUid()).child("FriendList").child(tmpkey).setValue(tmp);
                        refDB.child(tmpkey).child("connecting").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                refDB.child(mFirebaseUser.getUid()).child("FriendList").child(tmpkey).child("connecting").setValue(dataSnapshot.getValue(boolean.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        edt.setText("");

                        return;
                    }
                }
                Toast.makeText(getApplicationContext(),"존재하지 않는 사용자 이메일 입니다.",Toast.LENGTH_SHORT).show();
                edt.setText("");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void refreshMyFL(){
        refDB.child(mFirebaseUser.getUid()).child("FriendList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    final String tmp = dataSnapshot1.child("user_id").getValue(String.class);
                    refDB.child(tmp).child("connecting").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            refDB.child(mFirebaseUser.getUid()).child("FriendList").child(tmp).child("connecting").setValue(dataSnapshot.getValue(boolean.class));
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

    public void setLayout(){
        edt = (EditText)findViewById(R.id.findforEmail);
        btn2 = (Button)findViewById(R.id.button2);
        btn3 = (Button)findViewById(R.id.button3);
    }
}
