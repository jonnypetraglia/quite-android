package com.qweex.quite;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;

public class GifFragment extends FragmentBase {
    static final String[] filesHandled = new String[] {".gif"};
    WebView v;

    @Override
    public View initView() {
        if(v==null) {
            v = new WebView(context);
            v.setId(R.id.content_view);
            v.setOnLongClickListener(((MainActivity)getActivity()).showOptions);
        }
        String x = "<html style='margin: 0; padding: 0; width: 100%; height: 100%;'>" +
                "<body style='margin: 0; padding: 0; width: 100%; height: 100%;'>" +

                "<div style='background-image: url(\"file://" + currentPath + "\");\n" +
                "    background-size: contain;\n" +
                "    background-repeat: no-repeat;\n" +
                "    background-position: center;" +
                "    width: 100%; height: 100%;" +
                "'></div>" +
                "</body></html>";

        Log.d("HTML", x);
        v.loadDataWithBaseURL("", x, "text/html", "utf-8", "");
        return v;
    }

    @Override
    public void pause() {
        if(getView()!=null)
            ((WebView)getView().findViewById(R.id.content_view)).onPause(); //TODO: is this the best way?
    }

    @Override
    public void unPause() {
        if(getView()!=null)
            ((WebView)getView().findViewById(R.id.content_view)).onResume(); //TODO: is this the best way?
    }
}
