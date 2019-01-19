package hu.andorsebo.clock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Thread t1, t2, t3;
        final Context ctx = this;
        final ArrayList<String> params = new ArrayList<>();
        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> temp = GetParams();
                for (int i=0;i<temp.size();i++)
                    params.add(temp.get(i));
            }
        });
        t1.start();
        t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = ctx.getSharedPreferences("hu.andorsebo.clock", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                if (params.size() > 0)
                    for (int i = 0; i < params.size(); i++) {
                        editor.putString(params.get(i).split(":")[0], params.get(i).split(":")[1]);
                        editor.apply();
                    }
            }
        });
        try {
            t1.join();
        } catch (InterruptedException e) {
            Log.d("T1 ERROR", e.getMessage());
        }
        t2.start();
        t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = ctx.getSharedPreferences("hu.andorsebo.clock",Context.MODE_PRIVATE);
                String temp = prefs.getString("szamok",null);
                if (temp == null) {
                    Log.d("Loaded","Fail");
                } else {
                    Log.d("Loaded",temp);
                }
            }
        });
        try {
            t2.join();
        } catch (InterruptedException e) {
            Log.d("T2 ERROR", e.getMessage());
        }
        t3.start();
        try {
            t3.join();
        } catch (InterruptedException e) {
            Log.d("T3 ERROR",e.getMessage());
        }

    }

    private ArrayList<String> GetParams() {
        ArrayList<String> params = new ArrayList<>();
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "clock_config.txt");
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null)
                params.add(line);
            br.close();
        } catch (IOException e) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    GenerateConfigFile();
                }
            });
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e1) {
                Log.d("T1 Generate",e1.getMessage());
            }
            params = GetParams();
        }
        return params;
    }

    private void GenerateConfigFile() {
        ArrayList<String> params = new ArrayList<>();
        params.add("hatter:#000000");
        params.add("szelesseg:320");
        params.add("magassag:320");
        params.add("nagy_mutato:#FF0000");
        params.add("kis_mutato:#00FF00");
        params.add("mp_mutato:#0000FF");
        params.add("szamok:#FFFFFF");

        try {
            File root = Environment.getExternalStorageDirectory();
            File file = new File(root, "clock_config.txt");
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < params.size(); i++)
                writer.append(params.get(i)+"\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
 * hatter:#000000;
 * szelesseg:320;
 * magassag:320;
 * nagy_mutato:#FF0000;
 * kis_mutato:#00FF00;
 * mp_mutato:#0000FF;
 * szamok:#FFFFFF;
 *
 * */