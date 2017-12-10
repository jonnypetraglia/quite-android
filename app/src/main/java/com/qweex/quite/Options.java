package com.qweex.quite;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;
import java.util.Comparator;

public class Options {
    private AlertDialog popupDialog;

    boolean reverseSort;
    QuiteAdapter quiteAdapter;



    public Options(final MainActivity act, QuiteAdapter qAdapter) {
        this.quiteAdapter = qAdapter;

        LinearLayout ll = new LinearLayout(act);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.VERTICAL);

        String[] sorts = new String[] {"Name", "Date", "Size", "Type", "Random"};

        Spinner sortOrderSpinner = new Spinner(act);
        ArrayAdapter<String> adap = new ArrayAdapter<String>(act, android.R.layout.simple_list_item_1, sorts);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOrderSpinner.setAdapter(adap);
        int sel = 0;
        if(sortOrder==dateDesc|| sortOrder==dateAsc)
            sel = 1;
        else if(sortOrder==sizeDesc|| sortOrder==sizeAsc)
            sel = 2;
        else if(sortOrder==typeDesc|| sortOrder==typeAsc)
            sel = 3;
        else if(sortOrder==RANDOM)
            sel = 4;
        sortOrderSpinner.setSelection(sel);
        sortOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Comparator<File> oldSortOrder = sortOrder;
                switch(position) {
                    case 0:
                        sortOrder = reverseSort ? nameDesc : nameAsc;
                        return;
                    case 1:
                        sortOrder = reverseSort ? dateDesc : dateAsc;
                        return;
                    case 2:
                        sortOrder = reverseSort ? sizeDesc : sizeAsc;
                        return;
                    case 3:
                        sortOrder = reverseSort ? typeDesc : typeAsc;
                        return;
                    case 4:
                        sortOrder = RANDOM;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ll.addView(sortOrderSpinner, lp);


        CheckBox reverseCheckbox = new CheckBox(act);
        reverseCheckbox.setChecked(reverseSort);
        reverseCheckbox.setText("Reverse");
        reverseCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reverseSort = isChecked;
            }
        });
        ll.addView(reverseCheckbox, lp);


        Button confirmBtn = new Button(act);
        confirmBtn.setText("Apply");
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Options", "re-initializing adapter");
                act.initializeAdapter();
                popupDialog.hide();
            }
        });
        ll.addView(confirmBtn, lp);


        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setView(ll);
        popupDialog = builder.show();
        popupDialog.hide();

        // Recurse
        // Filetypes
        // Start slideshow w/ seconds | Stop slideshow

    }

    public void show() {
        popupDialog.show();
    }

    public static Comparator<File> nameAsc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return f1.getPath().compareTo(f2.getPath());
        }
    },
    nameDesc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return f2.getPath().compareTo(f1.getPath());
        }
    },
    dateAsc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return (int) (f1.lastModified() - f2.lastModified());
        }
    },
    dateDesc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return (int) (f2.lastModified() - f1.lastModified());
        }
    },
    sizeAsc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return (int) (f1.getTotalSpace() - f2.getTotalSpace());
        }
    },
    sizeDesc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return (int) (f2.getTotalSpace() - f1.getTotalSpace());
        }
    },
    typeAsc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return f1.getName().substring(f1.getName().lastIndexOf(".")+1).compareTo(
                   f2.getName().substring(f2.getName().lastIndexOf(".")+1));
        }
    },
    typeDesc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return f2.getName().substring(f2.getName().lastIndexOf(".")+1).compareTo(
                    f1.getName().substring(f1.getName().lastIndexOf(".")+1));
        }
    },
    RANDOM = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return 0;
        }
    };

    public static Comparator<File> sortOrder = nameAsc;

}
