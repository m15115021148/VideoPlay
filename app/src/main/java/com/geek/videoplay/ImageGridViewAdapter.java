package com.geek.videoplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.geek.videoplay.util.ImageUtil;

import java.util.List;

/**
 * @dese 图片布局适配器
 * Created by chenmeng on 2016/11/30.
 */

public class ImageGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<TypeModel> mList;
    private Holder holder;
    private OnCallBack mCallBack;

    public ImageGridViewAdapter(Context context, List<TypeModel> list,OnCallBack callBack) {
        this.mContext = context;
        this.mList = list;
        this.mCallBack = callBack;
    }

    public interface OnCallBack{
        void onDelete(int pos);
        void onPlayMedia(int pos);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.image_grid_view_item, null);
            holder = new Holder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.del = (LinearLayout) convertView.findViewById(R.id.del);
            holder.play = (ImageView) convertView.findViewById(R.id.play);
            holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.sel_check);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        TypeModel model = mList.get(position);
        if (model.getType()==0||model.getType()==3){//照片
            holder.play.setVisibility(View.GONE);
            Bitmap bt = ImageUtil.getSmallBitmap(model.getPath());
            holder.img.setImageBitmap(bt);
        }else if (model.getType()==1){//视频 去视频的第一帧显示
            holder.play.setVisibility(View.VISIBLE);
            Bitmap bmp = ThumbnailUtils.createVideoThumbnail(model.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
            holder.img.setImageBitmap(bmp);
        }else if (model.getType()==2){//录音
            holder.play.setVisibility(View.GONE);
            holder.img.setImageResource(R.drawable.voice);
        }

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack!=null){
                    mCallBack.onDelete(position);
                }
            }
        });
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack!=null){
                    mCallBack.onDelete(position);
                }
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack!=null){
                    mCallBack.onPlayMedia(position);
                }
            }
        });

        return convertView;
    }

    private class Holder {
        ImageView img;
        LinearLayout del;
        ImageView play;
        RelativeLayout mLayout;
    }
}
