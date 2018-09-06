package com.qweex.quite;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

// GIF support? https://gist.github.com/felipecsl/6289457

public class MainActivity extends AppCompatActivity {

    final int BLACK = 0xff000000;
    final int PERM_CHECK = 9001;
    final int REQUEST_APP_SETTINGS = 1337;

    ViewPager pager;
    QuiteAdapter qAdapter;
    int lastSelected;
    Options optionsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); //TODO: Config

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d("permissionCheck", permissionCheck + "!");
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            initializeView();
            initializeAdapter();
        }
        else {
            Log.d("permissionCheck", "requesting");
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_CHECK);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            initializeView();
            initializeAdapter();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("You must allow access to local files")
                    .setPositiveButton("Open Settings", goToSettings)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && pager!=null) {
            pager.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            View.SYSTEM_UI_FLAG_FULLSCREEN
                             View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );}
    }

    protected void initializeView() {
        pager = new ViewPager(this);
        pager.setBackgroundColor(BLACK);
        pager.setId(R.id.pager);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(pager, lp);
        ((View)pager.getParent()).setBackgroundColor(BLACK);


        pager.setForegroundGravity(Gravity.CENTER);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((FragmentBase)qAdapter.getItem(lastSelected)).pause();
                lastSelected = position; //or if this don't work, pager.getCurrentItem();
                ((FragmentBase)qAdapter.getItem(lastSelected)).unPause();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //*/
    }

    protected void initializeAdapter() {
        Uri uri;
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action!=null && action.compareTo(Intent.ACTION_VIEW) == 0)
            uri = intent.getData();
        else
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/.system")); //TODO Config

        Log.d("URI", uri.getPath() + "!");
        pager.setAdapter(null);
        qAdapter = new QuiteAdapter(uri, getSupportFragmentManager(), this);
        if(qAdapter.getCount()==0) {
            Toast.makeText(this, "No images in directory", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pager.setAdapter(qAdapter);
        pager.setCurrentItem(
                lastSelected = qAdapter.getIndexOfStart()
        );
        Log.d("Starting", "at " + lastSelected);

        optionsDialog = new Options(this);
    }

    DialogInterface.OnClickListener goToSettings = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
        }
    };

    public View.OnLongClickListener showOptions = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.d("showOptions", "onClickListener");
            optionsDialog.show((FragmentBase)qAdapter.getItem(pager.getCurrentItem()));
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_APP_SETTINGS) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck == PermissionChecker.PERMISSION_GRANTED)
                initializeAdapter();
            else
                finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
