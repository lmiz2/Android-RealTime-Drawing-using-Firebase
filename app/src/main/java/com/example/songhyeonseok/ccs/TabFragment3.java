package com.example.songhyeonseok.ccs;// 설정 탭을 담당하는 프래그먼트

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.songhyeonseok.ccs.GONG.text_size;
import static com.example.songhyeonseok.ccs.Start.Global_FontSize;


public class TabFragment3 extends GlobalFrag {

    tab3Listener mCallback;
    private Button logout;
    private SeekBar seekbar_text_size;
    private CheckBox checkBox_notify;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference refDB;
    TextView ts ;
    boolean isServiceRun = false;
    ViewGroup root;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isServiceRun = isServiceRunningCheck();
        View v = inflater.inflate(R.layout.activity_settings, container, false);
        logout = (Button)v.findViewById(R.id.btn_log_out);

        root = (ViewGroup)v.findViewById(android.R.id.content);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        refDB = firebaseDatabase.getReference("Members").child(mFirebaseUser.getUid()).child("connecting");

        ts = (TextView)v.findViewById(R.id.ts);
        seekbar_text_size = (SeekBar)v.findViewById(R.id.set_text_size);
        checkBox_notify = (CheckBox)v.findViewById(R.id.checkbox_notify);
        checkBox_notify.setChecked(isServiceRun);

        setGlobalFont(container,Global_FontSize);


        checkBox_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("check","check12"+isChecked);
                    Intent intent = new Intent(getActivity(),//현재제어권자
                            MyService.class); // 이동할 컴포넌트
                    getActivity().startService(intent);
                }else {
                    Intent intent = new Intent(getActivity(),MyService.class);
                    getActivity().stopService(intent);
                }

            }
        });



        seekbar_text_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<1){
                    setAllTextsSize(10f);
                    setAppFontSize(10);
                    mCallback.communication3(10);
                    Global_FontSize = 10;
                }else if(progress>=1 && progress < 2){
                    setAllTextsSize(15f);
                    setAppFontSize(15);
                    mCallback.communication3(15);
                    Global_FontSize = 15;
                }else if(progress >= 2){
                    setAllTextsSize(20f);
                    setAppFontSize(20);
                    mCallback.communication3(20);
                    Global_FontSize = 20;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(),GONG.class);
                Intent intent2 = new Intent(getActivity(),MyService.class);
                boolean logout = true;
                intent.putExtra("logout",logout);
                startActivity(intent);
                refDB.setValue(false);
                getActivity().stopService(intent2);
                getActivity().finish();
            }
        });

        return v;
    }

    public void setAllTextsSize(float f){
        ts.setTextSize(f);
        checkBox_notify.setTextSize(f);
        logout.setTextSize(f);
        text_size = f;
    }

    public boolean isServiceRunningCheck() {
        Log.d("abdc2233","서비스 체크 실행됨");
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("check","check2233 "+service.service.getClassName());
            if ("com.example.songhyeonseok.ccs.MyService".equals(service.service.getClassName())) {
                Log.d("check","check2233 "+"true!!");
                return true;
            }
        }
        return false;
    }


    public interface tab3Listener{
        void communication3(int size);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (tab3Listener) getActivity();
        }catch(ClassCastException e){
            System.out.println("에러 : "+ e.toString());
            Log.d("에러 : ",getActivity().toString()+" 는 반드시 tab1Listener를 implements 해야 합니다.");
        }
    }

}