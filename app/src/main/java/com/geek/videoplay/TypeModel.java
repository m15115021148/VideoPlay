package com.geek.videoplay;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/3/9.
 */

public class TypeModel {
    private Bitmap bt;
    private int type;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getBt() {
        return bt;
    }

    public void setBt(Bitmap bt) {
        this.bt = bt;
    }
}
