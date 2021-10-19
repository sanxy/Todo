package com.sanxynet.todo.helper;

import android.view.View;

public interface ItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
