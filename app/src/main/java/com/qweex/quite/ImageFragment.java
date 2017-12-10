package com.qweex.quite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

public class ImageFragment extends FragmentBase {
    static final String[] filesHandled = new String[] {".jpg", ".jpeg", ".png"};
    ImageView v;

    @Override
    public View initView() {
        if(v==null) {
            v = new ImageView(context);
            v.setId(R.id.content_view);
        }
        Bitmap myBitmap = BitmapFactory.decodeFile(currentPath);
        v.setImageBitmap(myBitmap);
        return v;
    }
}
