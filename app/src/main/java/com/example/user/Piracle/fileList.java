package com.example.user.Piracle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class fileList extends AppCompatActivity {
    public static int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String ROOT = "/";
    private static final String PRE_LEVEL = "..";
    public static final int FIRST_ITEM = 0;
    public static final int SECOND_ITEM = 1;
    private String IMG_ITEM = "image";
    private String NAME_ITEM = "name";
    private List<Map<String, Object>> filesList;
    private List<String> names;
    private List<String> paths;
    private File[] files;
    private Map<String, Object> filesMap;
    private int[] fileImg = {
            R.drawable.directory,
            R.drawable.file};
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    private String nowPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        int permission = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[] {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }else{
            initData();
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限，進行檔案存取
                    initData();
                    initView();
                } else {
                    //使用者拒絕權限，停用檔案存取功能
                    //初始化Intent物件
                    Intent intent = new Intent();
                    //從MainActivity 到Main2Activity
                    intent.setClass(fileList.this , Piracle.class);
                    //開啟Activity
                    startActivity(intent);
                }
        }
    }

    private void initView() {
        simpleAdapter = new SimpleAdapter(this,
                filesList, R.layout.simple_adapter, new String[]{IMG_ITEM, NAME_ITEM},
                new int[]{R.id.image, R.id.text});
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String target = paths.get(position);
                if(target.equals(ROOT)){
                    nowPath = paths.get(position);
                    getFileDirectory(ROOT);
                    simpleAdapter.notifyDataSetChanged();
                } else if(target.equals(PRE_LEVEL)){
                    nowPath = paths.get(position);
                    getFileDirectory(new File(nowPath).getParent());
                    simpleAdapter.notifyDataSetChanged();
                } else {
                    File file = new File(target);
                    if (file.canRead()) {
                        if (file.isDirectory()) {
                            nowPath = paths.get(position);
                            getFileDirectory(paths.get(position));
                            simpleAdapter.notifyDataSetChanged();
                        } else{
                            //初始化Intent物件
                            Intent intent = new Intent();
                            //從MainActivity 到Main2Activity
                            intent.setClass(fileList.this , openppt.class);
                            //開啟Activity
                            startActivity(intent);
                            Toast.makeText(fileList.this, "已選取檔案", Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(fileList.this, "無法讀取", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initData() {
        filesList = new ArrayList<>();
        names = new ArrayList<>();
        paths = new ArrayList<>();
        getFileDirectory("/storage/emulated/0");
    }

    private void getFileDirectory(String path){
        filesList.clear();
        paths.clear();
        if(!path.equals(ROOT)){
            //回根目錄
            filesMap = new HashMap<>();
            names.add(ROOT);
            paths.add(FIRST_ITEM, ROOT);
            filesMap.put(IMG_ITEM, fileImg[0]);
            filesMap.put(NAME_ITEM, ROOT);
            filesList.add(filesMap);
            //回上一層
            filesMap = new HashMap<>();
            names.add(PRE_LEVEL);
            paths.add(SECOND_ITEM, new File(path).getParent());
            filesMap.put(IMG_ITEM, fileImg[0]);
            filesMap.put(NAME_ITEM, PRE_LEVEL);
            filesList.add(filesMap);
        }
        files = new File(path).listFiles();

        for (int i = 0; i < files.length; i++) {
            filesMap = new HashMap<>();
            names.add(files[i].getName());
            paths.add(files[i].getPath());
            if (files[i].isDirectory()) {
                filesMap.put(IMG_ITEM, fileImg[0]);
            } else {
                filesMap.put(IMG_ITEM, fileImg[1]);
            }
            filesMap.put(NAME_ITEM, files[i].getName());
            filesList.add(filesMap);
        }
    }
}