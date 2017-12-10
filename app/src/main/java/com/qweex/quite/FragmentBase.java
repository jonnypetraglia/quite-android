package com.qweex.quite;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public abstract class FragmentBase extends Fragment {

    Context context;
    public String currentPath;

    public FragmentBase() {}

    public static FragmentBase newInstance(Class<?> cls, String filepath) throws IllegalAccessException, java.lang.InstantiationException {
        FragmentBase frag = (FragmentBase) cls.newInstance();
        frag.currentPath = filepath;
        Bundle args = new Bundle();
        args.putString("file", filepath);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        Log.d("Context:", context + "!");
        View v = initView();
        if(v.getParent()!=null)
            return (View) v.getParent();
        RelativeLayout rl = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        rl.addView(v, lp);

        rl.setId(R.id.frag_view);
        rl.setBackgroundColor(0xff000000);
        v.setBackgroundColor(0x00000000);

        Log.d("onCreateView", currentPath);
        return rl;
    }

    protected abstract View initView();
    public void pause() {};
    public void unPause() {};
}
