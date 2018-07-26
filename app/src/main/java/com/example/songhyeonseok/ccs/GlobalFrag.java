package com.example.songhyeonseok.ccs;//모든 탭 프래그먼트의 부모로, 이 역시 글로벌 폰트 조절을 위해 작성되었다.

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lmiz2 on 2017-10-16.
 */
public class GlobalFrag extends Fragment {
    ViewGroup root;

    public void setAppFontSize(int size){
        setGlobalFont(root,size);
        Log.d("globalFrag.","setAppFontSize"+size);
    }

    protected void setGlobalFont(ViewGroup root,int size) {
        this.root = root;
        Log.d("globalFrag.","setGlobalFontSize"+size);

        for (int i = 0; i < root.getChildCount(); i++) {

            View child = root.getChildAt(i);

            if (child instanceof TextView)
                ((TextView)child).setTextSize(size);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child,size);
        }
    }
}

