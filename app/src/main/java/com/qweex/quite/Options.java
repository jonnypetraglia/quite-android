package com.qweex.quite;

import java.io.File;
import java.util.Comparator;

public class Options {

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
    typeAsc = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return f1.getName().substring(f1.getName().lastIndexOf(".")+1).compareTo(
                   f2.getName().substring(f1.getName().lastIndexOf(".")+1));
        }
    };

    public static Comparator<File> sortOrder = typeAsc;

}
