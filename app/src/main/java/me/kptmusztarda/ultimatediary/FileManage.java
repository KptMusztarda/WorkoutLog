//package me.kptmusztarda.ultimatediary;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.os.Environment;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//
///**
// * Created by KptMusztarda on 2018-01-26.
// */
//
//public class FileManage extends MainActivity{
//
//    private static File root;
//
////    private void checkExternalStorage(){
////        boolean mExternalStorageAvailable = false;
////        boolean mExternalStorageWriteable = false;
////        String state = Environment.getExternalStorageState();
////
////        if (Environment.MEDIA_MOUNTED.equals(state)) {
////            // Can read and write the media
////            mExternalStorageAvailable = mExternalStorageWriteable = true;
////        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
////            // Can only read the media
////            mExternalStorageAvailable = true;
////            mExternalStorageWriteable = false;
////        } else {
////            // Can't read or write
////            mExternalStorageAvailable = mExternalStorageWriteable = false;
////        }
////        System.out.println("External Media: readable="+mExternalStorageAvailable+" writable="+mExternalStorageWriteable);
////    }
//
//
//
