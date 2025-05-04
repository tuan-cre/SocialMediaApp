package com.example.socialmediaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> lstMessage;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    public MessageAdapter(List<Message> lstMessage) {
        this.lstMessage = lstMessage;
    }
    public void setData(List<Message> lst){
        this.lstMessage = lst;
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        if (lstMessage.get(position).isChu_tin_nhan()) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receiving, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = lstMessage.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).txtMessage.setText(message.getContent());
        } else {
            ((ReceivedMessageViewHolder) holder).txtMessage.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        if(lstMessage!=null)
            return lstMessage.size();
        return 0;
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private TextView txtMessage;
        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtmessage);
        }
    }
    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtmessage_receiving);
        }
    }

}
