package com.aries.storyreader.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aries.storyreader.MyApplication;
import com.aries.storyreader.R;
import com.aries.storyreader.bean.Node;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/5/26.
 */
public class nodeAdapter extends RecyclerView.Adapter<nodeAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Node> nodeItems;
    private OnItemClickListener listener;
    private int choiseNodeIndex = -1;

    private ImageLoader loader;
    private DisplayImageOptions options;

    public interface OnItemClickListener {
        void onItemClicked(int position,Node item);
    }

    public void setData(ArrayList<Node> items) {
        this.nodeItems = items;
        notifyDataSetChanged();
    }

    public void setChoiseNodeId(long id) {
        setChoiseNodeIndex(getPositionById(id));

    }

    public void setChoiseNodeIndex(int index){
        if (index != choiseNodeIndex){
            int lastIndex = choiseNodeIndex;
            notifyItemChanged(lastIndex);
            if (-1 < choiseNodeIndex) {
                notifyItemChanged(choiseNodeIndex);
            }
        }
    }



    public nodeAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return null == nodeItems ? 0 : nodeItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_node, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (null != nodeItems && !nodeItems.isEmpty()) {
            final Node item = nodeItems.get(position);
            if (null != item.getRole()) {
                if (null == loader){
                    loader = ImageLoader.getInstance();
                    options = MyApplication.instence.getCircleOptions();
                }
                holder.name.setText(item.getRole().getName() + ",index=" + position);
                if (null != item.getRole().getPortrait()){
                    loader.displayImage(item.getRole().getPortrait(),holder.portrait,options);
                }
                holder.portrait.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);
            } else {
                holder.name.setVisibility(View.GONE);
                holder.portrait.setVisibility(View.INVISIBLE);
            }
            holder.content.setText(item.getContent());

            if (null != listener) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(position,item);
                    }
                });
            }

            if (position == choiseNodeIndex){
                holder.name.setTextColor(context.getResources().getColor(R.color.colorNameTextChoiced));
                holder.content.setTextColor(context.getResources().getColor(R.color.colorTextChoiced));
                holder.itemView.setBackgroundResource(R.drawable.bg_item_choiced);
            } else {
                holder.name.setTextColor(context.getResources().getColor(R.color.colorNameTextUnChoiced));
                holder.content.setTextColor(context.getResources().getColor(R.color.colorTextUnChoiced));
                holder.itemView.setBackgroundResource(R.drawable.bg_item_unchoiced);
            }

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView portrait;
        public AppCompatTextView name;
        public AppCompatTextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            portrait = (AppCompatImageView) itemView.findViewById(R.id.portrait);
            name = (AppCompatTextView) itemView.findViewById(R.id.name);
            content = (AppCompatTextView) itemView.findViewById(R.id.content);
        }
    }

    private int getPositionById(long id){
        if (0 > id){
            return -1;
        }
        int position = 0;
        for (Node node:nodeItems){
            if (node.getId() == id){
                return position;
            }
            position++;
        }
        return -1;
    }

}
