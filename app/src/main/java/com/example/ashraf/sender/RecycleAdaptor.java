package com.example.ashraf.sender;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Model.ChatMessage;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ashraf on 11/1/2016.
 */

public class RecycleAdaptor
        extends RecyclerView.Adapter<RecycleAdaptor.ItemViewHolder> {
    List<ChatMessage> chatMessageList;
    Context context;
    View v;

    RecycleAdaptor(Context context) {

        chatMessageList = new ArrayList<>();
        this.context = context;
    }


    @Override
    public int getItemCount() {

        return chatMessageList.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {

        if (chatMessageList.get(i).getPhotoUrl() != null)
            Picasso.with(context).load(chatMessageList.get(i).getPhotoUrl()).placeholder(R.drawable.user).into(itemViewHolder.senderPhotoCircleImageview);
        if (chatMessageList.get(i).getPhotoType() != null)
            Picasso.with(context).load(chatMessageList.get(i).getPhotoType()).placeholder(R.drawable.member).into(itemViewHolder.photoTypeCircleImageview);
        itemViewHolder.contentTextview.setText(chatMessageList.get(i).getContent());
        itemViewHolder.dateTextview.setText(
                DateUtils.getRelativeTimeSpanString(  Long.valueOf(chatMessageList.get(i).getDate()), new Date().getTime(),0L));
        itemViewHolder.senderNameTextview.setText(chatMessageList.get(i).getNameSender());
        v.setOnClickListener(clickListener);
        v.setTag(itemViewHolder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        CircleImageView senderPhotoCircleImageview, photoTypeCircleImageview;
        TextView contentTextview, dateTextview, senderNameTextview;


        ItemViewHolder(View itemView) {
            super(itemView);
            senderPhotoCircleImageview = (CircleImageView) itemView.findViewById(R.id.senderPhotocirleImageview);
            photoTypeCircleImageview = (CircleImageView) itemView.findViewById(R.id.photoTypeCirleImageview);
            contentTextview = (TextView) itemView.findViewById(R.id.contentTextview);
            dateTextview = (TextView) itemView.findViewById(R.id.dateTextview);
            senderNameTextview = (TextView) itemView.findViewById(R.id.senderNameTextview);
        }

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ItemViewHolder holder = (ItemViewHolder) view.getTag();
            int position = holder.getPosition();

        }


    };
}