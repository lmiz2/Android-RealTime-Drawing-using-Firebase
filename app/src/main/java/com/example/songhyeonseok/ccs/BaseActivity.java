package com.example.songhyeonseok.ccs;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lmiz2 on 2017-10-22.
 */




public class BaseActivity extends AppCompatActivity {//모든 액티비티의 부모 액티비티로, 글로벌 폰트 크기 조절을 위해서 만들어짐.
    public static int Global_FontSize = 15;

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
    public void setAppFontSize(int size){
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root,size);
    }

    void setGlobalFont(ViewGroup root, int size) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
            {                // 크기 조절
                ((TextView)child).setTextSize(size);
            }
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child,size);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }
}
