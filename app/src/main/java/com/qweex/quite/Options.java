package com.qweex.quite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class Options {
    private AlertDialog popupDialog, filetypesSelector;

    boolean reverseSort;
    TextView aboutText, filetypesText;


    public static Comparator<File> sortOrder;
    public static boolean recurse;
    public static String[] filetypes = new String[
            ImageFragment.filesHandled.length +
                    GifFragment.filesHandled.length +
                    VideoFragment.filesHandled.length];
    public static String[] filetypesSelected = new String[filetypes.length];

    public void updateFiletypesText() {
        int c = 0;
        for(String s : filetypesSelected)
            c += s.equals(".") ? 0 : 1;
        filetypesText.setText("Select filetypes (" + Integer.toString(c) + ")");
    }


    public Options(final MainActivity act) {

        LinearLayout ll = new LinearLayout(act);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpM = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpM.setMargins(40,40,40,40);

        aboutText = new TextView(act);
        CheckBox reverseCheckbox = new CheckBox(act),
                 recurseCheckbox = new CheckBox(act);
        Spinner sortOrderSpinner = new Spinner(act);
        View filetypesItem = act.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        filetypesText = ((TextView)filetypesItem.findViewById(android.R.id.text1));
        ll.addView(aboutText, lp);
        ll.addView(sortOrderSpinner, lp);
        ll.addView(reverseCheckbox, lpM);
        ll.addView(recurseCheckbox, lpM);
        ll.addView(filetypesItem, lp);

        aboutText.setPadding(40, 40, 40, 40);
        aboutText.setBackgroundColor(Color.parseColor("#FDFDF0"));

        String[] sorts = new String[] {"Order by Name", "Order by Date", "Order by Size", "Order by Type", "Order Random"};
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

        reverseCheckbox.setChecked(reverseSort);
        reverseCheckbox.setText("Reverse Order");
        reverseCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reverseSort = isChecked;
            }
        });

        recurseCheckbox.setChecked(recurse);
        recurseCheckbox.setText("Recurse");

        //fileTypes
        updateFiletypesText();
        boolean[] checkedFiletypes = new boolean[filetypesSelected.length];
        for(int i=0; i<checkedFiletypes.length; i++)
            checkedFiletypes[i] = !filetypesSelected[i].equals(".");
        AlertDialog.Builder filetypesBuilder = new AlertDialog.Builder(act);
        filetypesBuilder.setMultiChoiceItems(filetypes, checkedFiletypes, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                filetypesSelected[which] = isChecked ? filetypes[which] : ".";
            }
        });
        filetypesBuilder.setTitle("Select Filetypes");
        filetypesBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFiletypesText();
            }
        });
        filetypesSelector = filetypesBuilder.create();
        filetypesItem.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.d("filetypesText", "showSelector");
                 filetypesSelector.show();
             }
         });

        ScrollView sv = new ScrollView(act);
        sv.addView(ll, lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setView(sv);
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri currentUri = Uri.parse(
                        "file:" + act.qAdapter.files[
                                act.pager.getCurrentItem()
                                ].getPath());
                Log.d("Options", "re-initializing adapter : " + currentUri.getPath());

                //act.getIntent().setData(currentUri);
                //act.initializeView();
                //act.initializeAdapter();

                act.qAdapter.rescan();
                /*
                if(quiteAdapter.fragments!=null) {
                    FragmentTransaction ft = quiteAdapter.fragmentManager.beginTransaction();
                    for (FragmentBase fb : quiteAdapter.fragments)
                        ft.remove(fb);
                    ft.commit();
                }//*/
                act.qAdapter.fragments = new FragmentBase[0];
                act.qAdapter.notifyDataSetChanged();
                act.pager.setAdapter(null);
                act.pager.destroyDrawingCache();
                act.qAdapter.rescan();
                act.qAdapter.notifyDataSetChanged();

                act.qAdapter = new QuiteAdapter(currentUri, act.getSupportFragmentManager(), act);
                if(act.qAdapter.getCount()==0) {
                    Toast.makeText(act, "No images in directory", Toast.LENGTH_SHORT).show();
                    act.finish();
                    return;
                }
                act.pager.setAdapter(act.qAdapter);
                 //*/
                        /*
                Log.d("New Current", quiteAdapter.files[
                        act.pager.getCurrentItem()
                        ].getPath() + "!");
                act.pager.setAdapter(quiteAdapter);
                act.pager.setCurrentItem(
                        act.lastSelected = quiteAdapter.getIndexOfStart()
                );*/

                popupDialog.hide();
               // */
            }
        });
        popupDialog = builder.create();

        // Start slideshow w/ seconds | Stop slideshow

    }

    public void show(FragmentBase fragment) {
        aboutText.setText(fragment.getAbout());
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

    static {
        sortOrder = nameAsc;
        int i=0;
        for(String s : ImageFragment.filesHandled)
            filetypes[i++] = s;
        for(String s : GifFragment.filesHandled)
            filetypes[i++] = s;
        for(String s : VideoFragment.filesHandled)
            filetypes[i++] = s;
        Arrays.sort(filetypes);
        for(int j=0; j<filetypes.length; j++)
            filetypesSelected[j] = filetypes[j];
    }

}
