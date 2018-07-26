package com.example.songhyeonseok.ccs;//시작화면 (로그인 화면) 액티비티. 애니메이션 시작후 구글 로그인을 통해 로그인 가능 여부를 검사후 Start액티비티 호출.

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class GONG extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    private SignInButton mSigninBtn;
    private GoogleApiClient mgoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private User login_user;
    private TextView icon;
    private FirebaseDatabase mFirebaseDatabase;
    LinearLayout loading_cover;
    ConstraintLayout cover;

    public static float text_size = 10f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gong);

        cover = (ConstraintLayout) findViewById(R.id.cover);// 초기화
        loading_cover = (LinearLayout)findViewById(R.id.loading_cover);
        mSigninBtn = (SignInButton)findViewById(R.id.sign_in_btn);
        icon = (TextView) findViewById(R.id.appicon);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        ref = mFirebaseDatabase.getReference().child("Members");

        icon.setTypeface(Typeface.createFromAsset(getAssets(),"Dosis-ExtraLight.ttf"));

        final Animation animtitle = AnimationUtils.loadAnimation(this,R.anim.anim_title);
        final Animation animtitle2 = AnimationUtils.loadAnimation(this,R.anim.anim_title2);
        final Animation tobigger = AnimationUtils.loadAnimation(this,R.anim.tobigger);

        icon.setVisibility(View.GONE);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                icon.startAnimation(animtitle);
                icon.setVisibility(View.VISIBLE);
            }
        },2000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                icon.setVisibility(View.VISIBLE);
                icon.startAnimation(animtitle2);
            }
        },3000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSigninBtn.startAnimation(animtitle);
                mSigninBtn.setVisibility(View.VISIBLE);
            }
        },3500);



        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();



        mSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

       //         Intent intent = new Intent(GONG.this,Start.class);
        //        startActivity(intent);
                if(getIntent().getBooleanExtra("logout",false)){
                    Log.d("LOGOUT","true");
                    mgoogleApiClient.clearDefaultAccountAndReconnect();
                }
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mgoogleApiClient);
                startActivityForResult(intent, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            //cover.setVisibility(View.GONE);
            cover.setClickable(false);
            loading_cover.setVisibility(View.VISIBLE);
            loading_cover.setClickable(true);
            GoogleSignInResult result
                    = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            if(result.isSuccess()){
                firebaseWithGoogle(account);
            }else{
                Toast.makeText(GONG.this,"인증 실패",Toast.LENGTH_LONG).show();
                loading_cover.setVisibility(View.GONE);
                cover.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        loading_cover.setVisibility(View.GONE);
        //cover.setVisibility(View.VISIBLE);
        cover.setClickable(true);
        Toast.makeText(GONG.this, "로그인 실패",Toast.LENGTH_LONG).show();
    }

    private void firebaseWithGoogle(GoogleSignInAccount account){
        AuthCredential credential
                = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        Task<AuthResult> authResultTask
                = mFirebaseAuth.signInWithCredential(credential);

        authResultTask.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                firebaseUser = authResult.getUser();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        login_user = new User();
                        login_user.setUser_id(firebaseUser.getUid());
                        login_user.setUser_name(firebaseUser.getDisplayName());
                        login_user.setUser_email(firebaseUser.getEmail());
                        login_user.setConnecting(true);
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                            if(snapshot == null){
                                ref.removeValue();
                                ref.child(firebaseUser.getUid()).setValue(login_user);
                                Intent intent = new Intent(GONG.this,Start.class);
                                startActivity(intent);
                                finish();
                                return;
                            }

                            if(login_user.getUser_id().equals(snapshot.getKey())){
                                ref.child(firebaseUser.getUid()).child("connecting").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            boolean isconn = dataSnapshot.getValue(boolean.class);
                                            Log.d("접속여부 : ", isconn + "");
                                            isLogIn(isconn);
                                        }catch (NullPointerException e){
                                            e.printStackTrace();
                                            isLogIn(false);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                return;
                            }
                        }
                        ref.child(firebaseUser.getUid()).setValue(login_user);
                        Intent intent = new Intent(GONG.this,Start.class);
                        startActivity(intent);
                        finish();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
    public void isLogIn(boolean b){
        if(b) {
            Toast.makeText(getApplicationContext(),"이미 접속중인 계정입니다.",Toast.LENGTH_SHORT).show();
            loading_cover.setVisibility(View.GONE);
            //cover.setVisibility(View.VISIBLE);
            cover.setClickable(true);
            mgoogleApiClient.clearDefaultAccountAndReconnect();
        }else{
            Log.d("회원 존재", "PASS");
            ref.child(firebaseUser.getUid()).child("connecting").setValue(true);
            Intent intent = new Intent(GONG.this, Start.class);
            startActivity(intent);
            finish();
        }
    }

}
