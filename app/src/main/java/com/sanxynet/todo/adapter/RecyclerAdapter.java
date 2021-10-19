package com.sanxynet.todo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sanxynet.todo.helper.ItemClickListener;
import com.sanxynet.todo.R;
import com.sanxynet.todo.helper.Utils;
import com.sanxynet.todo.model.Todo;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.JournalViewHolder> {
    private Context mContext;
    private ArrayList<Todo> mTodoArrayList;
    private ItemClickListener mListener;
    private static View mItemView;


    public RecyclerAdapter(Context mContext, ArrayList<Todo> mTodoArrayList, ItemClickListener listener) {
        this.mContext = mContext;
        this.mTodoArrayList = mTodoArrayList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_todo_item,parent,false);
        return new JournalViewHolder(itemView,mListener);
    }


    @Override
    public void onBindViewHolder(JournalViewHolder holder, int position) {
        Todo todo = mTodoArrayList.get(position);
        holder.mJournalIconTextView.setText(todo.getIcon());
        holder.mJournalNameTextView.setText(todo.getJournalName());
        holder.mJournalCategoryTextView.setText(todo.getJournalCategory().getCategory());
        holder.mJournalCardView.setBackgroundColor(setItemBgColor(todo.getJournalPriority()));

    }

    private int setItemBgColor(int journalPriority) {
        switch (journalPriority){
            case 0: return (ContextCompat.getColor(mContext,R.color.priority0));
            case 1: return (ContextCompat.getColor(mContext,R.color.priority1));
            case 2: return (ContextCompat.getColor(mContext,R.color.priority2));
            case 3: return (ContextCompat.getColor(mContext,R.color.priority3));
            case 4: return (ContextCompat.getColor(mContext,R.color.priority4));
            case 5: return (ContextCompat.getColor(mContext,R.color.priority5));
            default: return (ContextCompat.getColor(mContext,R.color.priority0));
        }
    }

    @Override
    public int getItemCount() {
        return mTodoArrayList.size();
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        LinearLayout layout;
        TextView mJournalIconTextView;
        TextView mJournalNameTextView;
        TextView mJournalCategoryTextView;
        ItemClickListener itemClickListener;
        CardView mJournalCardView;

        public JournalViewHolder(View itemView,ItemClickListener itemClickListener) {
            super(itemView);
            RecyclerAdapter.mItemView = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.itemClickListener = itemClickListener;

            layout = itemView.findViewById(R.id.journal_layout);
            layout.getLayoutParams().width = (int) (Utils.getScreenWidth(itemView.getContext()) / 3.15);
            mJournalIconTextView = itemView.findViewById(R.id.journal_icon_textview);
            mJournalNameTextView = itemView.findViewById(R.id.journal_name_textview);
            mJournalCategoryTextView = itemView.findViewById(R.id.journal_category_textview);
            mJournalCardView = itemView.findViewById(R.id.journal_item_card);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();

            if(position!=RecyclerView.NO_POSITION){

                if(id == R.id.journal_layout){
                    itemClickListener.onItemClick(view, position);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int id = v.getId();
            int position = getAdapterPosition();

            if(position != RecyclerView.NO_POSITION){

                if(id == R.id.journal_layout){
                    itemClickListener.onItemLongClick(v,position);
                }
            }
            return true;
        }
    }
}

