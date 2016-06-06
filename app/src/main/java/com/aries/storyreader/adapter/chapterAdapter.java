package com.aries.storyreader.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aries.storyreader.R;
import com.aries.storyreader.bean.ChapterItem;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/5/26.
 */
public class chapterAdapter extends RecyclerView.Adapter<chapterAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ChapterItem> chapterItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClicked(ChapterItem item);
    }

    public void setData(ArrayList<ChapterItem> items){
        this.chapterItems = items;
        notifyDataSetChanged();
    }

    public chapterAdapter(Context context,OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return null == chapterItems ? 0:chapterItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != chapterItems && !chapterItems.isEmpty()){
            final ChapterItem item = chapterItems.get(position);
            holder.index.setText(context.getString(R.string.chapterIndex,item.getIndex()));
            holder.title.setText(item.getDescribe());
            if (null != listener){
                holder.itemView.setTag(item);
                holder.itemView.setClickable(true);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(item);
                    }
                });
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public AppCompatTextView index;
        public AppCompatTextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            index = (AppCompatTextView) itemView.findViewById(R.id.index);
            title = (AppCompatTextView) itemView.findViewById(R.id.title);
        }
    }
}
