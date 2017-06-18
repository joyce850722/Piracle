package com.example.user.piracle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class fileList extends AppCompatActivity {
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
        initData();
        initView();
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
        getFileDirectory(ROOT);
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
        for(int i = 0; i < files.length; i++){
            filesMap = new HashMap<>();
            names.add(files[i].getName());
            paths.add(files[i].getPath());
            if(files[i].isDirectory()){
                filesMap.put(IMG_ITEM, fileImg[0]);
            }
            else {
                filesMap.put(IMG_ITEM, fileImg[1]);
            }
            filesMap.put(NAME_ITEM, files[i].getName());
            filesList.add(filesMap);
        }
    }
}