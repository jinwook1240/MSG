package com.example.clp.msg;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by clp on 16. 7. 16.
 */
public class Timethread extends Thread {
    String filename;
    public Timethread(String filename){
        this.filename = filename;
    }
    @Override
    public void run() {
        String file = readfile(filename);

    }
    public String readfile(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            Log.v("filecheck", filename + "not exist");
        }

        StringBuffer sb = new StringBuffer();
        Log.v("fileWriter", filename + " reading started");
        try {// 저장일시 읽어들이기
            FileInputStream fis = new FileInputStream(filename);
            int n;
            while ((n = fis.available()) > 0) {
                byte b[] = new byte[n];
                if (fis.read(b) == -1)
                    break;
                sb.append(new String(b));
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Could not find file" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
