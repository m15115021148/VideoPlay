package com.geek.videoplay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.geek.videoplay.util.FileNames;
import com.geek.videoplay.util.FileUtil;
import com.geek.videoplay.util.ImageUtil;
import com.geek.videoplay.util.SystemFunUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,CustomPopView.OnItemListener,ImageGridViewAdapter.OnCallBack{
    private MainActivity mContext;
    private GridView mGv;
    private ImageView mAdd;
    private CustomPopView popView;
    private String[] key = {"拍照","相册","视频","录音"};
    private SystemFunUtil util;//工具类
    private int FLAG_PICTURE = 0x001;
    private int FLAG_PHOTO = 0x002;
    private int FLAG_VIDEO = 0x003;
    private int FLAG_VOICE = 0x004;
    private File imgFile = null;//保存图片的文件
    private List<TypeModel> mList = new ArrayList<>();
    private ImageGridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mGv = (GridView) findViewById(R.id.gridView);
        mAdd = (ImageView) findViewById(R.id.add);
        mAdd.setOnClickListener(this);
        popView = new CustomPopView(this, Arrays.asList(key),this);
        util = new SystemFunUtil(this);
        initImg();
        adapter = new ImageGridViewAdapter(this,mList,this);
        mGv.setAdapter(adapter);
    }

    private void initImg(){
        imgFile = util.createRootDirectory(SystemFunUtil.APP_FILE_NAMES);
        if (imgFile==null){
            return;
        }
        List<String> list = FileUtil.getListFiles(imgFile);
        if (list == null || list.size()<=0) {
            return;
        }
        for (String path : list) {
            if (!TextUtils.isEmpty(path)) {
                if (path.contains(".jpg")) {
                    TypeModel model = new TypeModel();
                    model.setPath(path);
                    model.setType(0);
                    mList.add(model);
                }
                if (path.contains(".mp4")) {
                    TypeModel model = new TypeModel();
                    model.setPath(path);
                    model.setType(1);
                    mList.add(model);
                }
                if (path.contains(".mp3")) {
                    TypeModel model = new TypeModel();
                    model.setPath(path);
                    model.setType(2);
                    mList.add(model);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mAdd){
            popView.showBottom();
        }
    }

    @Override
    public void onClick(String values) {
        if (values.equals(key[0])){
            util.openCamera(SystemFunUtil.SYSTEM_IMAGE,FLAG_PICTURE);
        }
        if (values.equals(key[1])){
            util.openCamera(SystemFunUtil.SYSTEM_IMAGE_PHONE,FLAG_PHOTO);
        }
        if (values.equals(key[2])){
            util.openCamera(SystemFunUtil.SYSTEM_VIDEO,FLAG_VIDEO);
        }
        if (values.equals(key[3])){
            util.openCamera(SystemFunUtil.SYSTEM_VOICE,FLAG_VOICE);
        }
        popView.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK){
            if (requestCode==FLAG_PICTURE){//拍照
                try{
                    imgFile = util.getImgFile();
                    TypeModel model = new TypeModel();
                    model.setPath(imgFile.getPath());
                    model.setType(0);
                    mList.add(model);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (requestCode==FLAG_PHOTO){//本地相册
                Uri uri = data.getData();
                String path = util.getImageAbsolutePath(uri);
                TypeModel model = new TypeModel();
                model.setPath(path);
                model.setType(3);
                mList.add(model);
                adapter.notifyDataSetChanged();
            }
            if (requestCode==FLAG_VIDEO){//视频
                Uri uriVideo = data.getData();
                String path = util.getImageAbsolutePath(uriVideo);
                if (TextUtils.isEmpty(path)){
                    return;
                }
                //重命名
                FileNames name = new FileNames();
                File newFile = new File(imgFile.getPath(),name.getVideoName());//视频保存的文件
                boolean isSuccess = false;//是否成功
                try {
                    //拷贝
                    isSuccess = FileUtil.copyFileTo(new File(path), newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("result","录制视频失败");
                    return;
                }
                if (isSuccess){
                    //删除系统存放的
                    File del = new File(path);
                    del.delete();
                    TypeModel model = new TypeModel();
                    model.setPath(newFile.getPath());
                    model.setType(1);
                    mList.add(model);
                    adapter.notifyDataSetChanged();
                }else{
                    Log.e("result","录制视频失败");
                }
            }
            if (requestCode==FLAG_VOICE){//音频
                Uri uriVideo = data.getData();
                String path = util.UriToPath(uriVideo,SystemFunUtil.SYSTEM_VOICE);
                if (TextUtils.isEmpty(path)){
                    return;
                }
                //重命名
                FileNames name = new FileNames();
                File newFile = new File(imgFile.getPath(),name.getVoiceName());
                boolean isSuccess = false;//是否成功
                try {
                    //拷贝
                    isSuccess = FileUtil.copyFileTo(new File(path), newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("result","录音失败");
                    return;
                }
                if (isSuccess){
                    //删除系统存放的
                    File del = new File(path);
                    del.delete();
                    TypeModel model = new TypeModel();
                    model.setPath(newFile.getPath());
                    model.setType(2);
                    mList.add(model);
                    adapter.notifyDataSetChanged();
                }else{
                    Log.e("result","录音失败");
                }
            }
        }
    }

    /**
     * 删除
     * @param pos
     */
    @Override
    public void onDelete(final int pos) {
        AlertDialog.Builder normalDia = new AlertDialog.Builder(mContext);
        normalDia.setTitle("提示");
        normalDia.setMessage("确定删除吗？");
        normalDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mList.get(pos).getType()==3){//本地相册 不删除

                }else{
                    //删除图片
                    ImageUtil.deleteFilePath(mList.get(pos).getPath());
                }
                mList.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });
        normalDia.setNegativeButton("取消",null);
        normalDia.create().show();
    }

    /**
     * 播放
     * @param pos
     */
    @Override
    public void onPlayMedia(int pos) {
        if (mList.get(pos).getType()==0||mList.get(pos).getType()==3){
            util.playerMedia(mList.get(pos).getPath(),SystemFunUtil.SYSTEM_IMAGE);
        }else if (mList.get(pos).getType()==1){//视频 播放
            Intent intent = new Intent(mContext, VideoPlayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("path",mList.get(pos).getPath());
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }else if (mList.get(pos).getType()==2){
            util.playerMedia(mList.get(pos).getPath(),SystemFunUtil.SYSTEM_VOICE);
        }
    }
}
