package com.sanxynet.todo.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanxynet.todo.receiver.AlarmReceiver;
import com.sanxynet.todo.helper.IntentConstants;
import com.sanxynet.todo.helper.ItemClickListener;
import com.sanxynet.todo.R;
import com.sanxynet.todo.adapter.RecyclerAdapter;
import com.sanxynet.todo.model.Todo;
import com.sanxynet.todo.data.TodoDao;
import com.sanxynet.todo.data.TodoDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public final static int EDIT_TODO = 1;
    public final static int ADD_TODO = 0;
    public static Date todayDate;
    public static Date tomDate;

    TodoDao todoDao;

    RecyclerView mTodayRecyclerView;
    RecyclerView mUpComingRecyclerView;
    RecyclerView mDoneRecyclerView;

    List<Todo> todoList;
    ArrayList<Todo> todayTodoArrayList;
    ArrayList<Todo> upcomingTodoArrayList;
    ArrayList<Todo> doneTodoArrayList;

    RecyclerAdapter mTodayRecyclerAdapter;
    RecyclerAdapter mUpcomingRecyclerAdapter;
    RecyclerAdapter mDoneRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TodoDetailsActivity.class);
            intent.putExtra(IntentConstants.REQ_CODE, ADD_TODO);
            startActivityForResult(intent, ADD_TODO);
        });

        mTodayRecyclerView = findViewById(R.id.today_recycler_view);
        mUpComingRecyclerView = findViewById(R.id.upcoming_recycler_view);
        mDoneRecyclerView = findViewById(R.id.done_recycler_view);

        todoList = new ArrayList<>();
        todayTodoArrayList = new ArrayList<>();
        upcomingTodoArrayList = new ArrayList<>();
        doneTodoArrayList = new ArrayList<>();

        mTodayRecyclerAdapter = new RecyclerAdapter(this, todayTodoArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editTodayTodo(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeTodayTodo(position);

            }
        });
        mUpcomingRecyclerAdapter = new RecyclerAdapter(this, upcomingTodoArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editUpcomingTodo(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeUpcomingTodo(position);
            }
        });
        mDoneRecyclerAdapter = new RecyclerAdapter(this, doneTodoArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editDoneTodo(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeDoneTodo(position);
            }
        });

        mTodayRecyclerView.setAdapter(mTodayRecyclerAdapter);
        mUpComingRecyclerView.setAdapter(mUpcomingRecyclerAdapter);
        mDoneRecyclerView.setAdapter(mDoneRecyclerAdapter);

        mTodayRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUpComingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mDoneRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mTodayRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mUpComingRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mDoneRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mTodayRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mUpComingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDoneRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper todayItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.UP) {
                            editTodayTodo(viewHolder.getAdapterPosition());
                        } else if (direction == ItemTouchHelper.DOWN) {
                            removeTodayTodo(viewHolder.getAdapterPosition());
                        }
                    }
                });

        ItemTouchHelper upcomingItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.UP) {
                            editUpcomingTodo(viewHolder.getAdapterPosition());
                        } else if (direction == ItemTouchHelper.DOWN) {
                            removeUpcomingTodo(viewHolder.getAdapterPosition());
                        }
                    }
                });

        ItemTouchHelper doneItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.UP) {
                            editDoneTodo(viewHolder.getAdapterPosition());
                        } else if (direction == ItemTouchHelper.DOWN) {
                            removeDoneTodo(viewHolder.getAdapterPosition());
                        }
                    }
                });

        todayItemTouchHelper.attachToRecyclerView(mTodayRecyclerView);
        upcomingItemTouchHelper.attachToRecyclerView(mUpComingRecyclerView);
        doneItemTouchHelper.attachToRecyclerView(mDoneRecyclerView);

        updateLists();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        } else {
            updateLists();
        }
    }

    private void updateLists() {
        setTodayTomDate();
        todoList.clear();
        todayTodoArrayList.clear();
        mTodayRecyclerAdapter.notifyDataSetChanged();
        upcomingTodoArrayList.clear();
        mUpcomingRecyclerAdapter.notifyDataSetChanged();
        doneTodoArrayList.clear();
        mDoneRecyclerAdapter.notifyDataSetChanged();

        TodoDatabase database = TodoDatabase.getInstance(this);
        todoDao = database.todoDao();

        Executors.newSingleThreadExecutor().execute(() -> {
            todoList = todoDao.getAllTodo();

            for (int i = 0; i < todoList.size(); i++) {
                Todo currentTodo = todoList.get(i);
                Date currentTodoDate = currentTodo.getJournalDate();

                if (currentTodoDate.before(todayDate)) {
                    cancelAlarm(currentTodo);
                    int size = upcomingTodoArrayList.size();
                    doneTodoArrayList.add(currentTodo);
                    mDoneRecyclerAdapter.notifyItemInserted(size);

                } else if (currentTodoDate.after(tomDate)) {
                    if (currentTodo.isJournalSetAlarm()) {
                        setAlarm(currentTodo);
                    } else {
                        cancelAlarm(currentTodo);
                    }
                    int size = upcomingTodoArrayList.size();
                    upcomingTodoArrayList.add(currentTodo);
                    mUpcomingRecyclerAdapter.notifyItemInserted(size);

                } else {
                    if (currentTodo.isJournalSetAlarm()) {
                        setAlarm(currentTodo);
                    } else {
                        cancelAlarm(currentTodo);
                    }
                    int size = todayTodoArrayList.size();
                    todayTodoArrayList.add(currentTodo);
                    mTodayRecyclerAdapter.notifyItemInserted(size);
                }
            }
        });

    }

    private void cancelAlarm(Todo todo) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra(IntentConstants.JOURNAL, todo.getJournalId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) todo.getJournalId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    private void setAlarm(Todo todo) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra(IntentConstants.JOURNAL, todo.getJournalId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) todo.getJournalId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, todo.getJournalTime(), pendingIntent);
    }

    private void setTodayTomDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        calendar.set(year, month, date, hour, min, 0);
        todayDate = calendar.getTime();
        calendar.set(year, month, date + 1, 0, 0, 0);
        tomDate = calendar.getTime();
    }

    private void removeDoneTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.remove_journal));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.are_you_sure));

        builder.setPositiveButton(getString(R.string.no), (dialog, which) -> {
            mDoneRecyclerAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.yes), (dialog, which) -> {
            TodoDatabase database = TodoDatabase.getInstance(MainActivity.this);
            todoDao = database.todoDao();

            Executors.newSingleThreadExecutor().execute(() -> {
                Todo todo = doneTodoArrayList.get(position);
                todoDao.deleteFromDb(todo);

                doneTodoArrayList.remove(position);
                mDoneRecyclerAdapter.notifyItemRemoved(position);

            });

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeUpcomingTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.remove_journal));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.are_you_sure));

        builder.setPositiveButton(getString(R.string.no), (dialog, which) -> {
            mUpcomingRecyclerAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.yes), (dialog, which) -> {
            TodoDatabase database = TodoDatabase.getInstance(MainActivity.this);
            todoDao = database.todoDao();

            Executors.newSingleThreadExecutor().execute(() -> {
                Todo todo = upcomingTodoArrayList.get(position);
                todoDao.deleteFromDb(todo);

                upcomingTodoArrayList.remove(position);
                mUpcomingRecyclerAdapter.notifyItemRemoved(position);
            });

        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void removeTodayTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.remove_journal));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.are_you_sure));

        builder.setPositiveButton(getString(R.string.no), (dialog, which) -> {
            mTodayRecyclerAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.yes), (dialog, which) -> {
            TodoDatabase database = TodoDatabase.getInstance(MainActivity.this);
            todoDao = database.todoDao();

            Executors.newSingleThreadExecutor().execute(() -> {
                Todo todo = todayTodoArrayList.get(position);
                todoDao.deleteFromDb(todo);

                todayTodoArrayList.remove(position);
                mTodayRecyclerAdapter.notifyItemRemoved(position);

            });

        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void editTodayTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Todo todo = todayTodoArrayList.get(position);
        builder.setTitle(todo.getJournalName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view, null);
        TextView dialogCat = view.findViewById(R.id.dialog_category);
        TextView dialogDesc = view.findViewById(R.id.dialog_desc);
        TextView dialogDate = view.findViewById(R.id.dialog_date);
        TextView dialogTime = view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    " + todo.getJournalCategory().getCategory());
        dialogDesc.setText(todo.getJournalDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String dateString = dateFormat.format(todo.getJournalDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeString = timeFormat.format(new Date(todo.getJournalTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(todo.getJournalPriority());
        dialogSwitch.setChecked(todo.isJournalSetAlarm());

        builder.setView(view);

        builder.setPositiveButton(getString(R.string.cancel), (dialog, which) -> {
            mTodayRecyclerAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.edit), (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this, TodoDetailsActivity.class);
            intent.putExtra(IntentConstants.REQ_CODE, EDIT_TODO);
            intent.putExtra(IntentConstants.JOURNAL, todo.getJournalId());
            startActivityForResult(intent, EDIT_TODO);
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void editUpcomingTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final Todo todo = upcomingTodoArrayList.get(position);
        builder.setTitle(todo.getJournalName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view, null);
        TextView dialogCat = view.findViewById(R.id.dialog_category);
        TextView dialogDesc = view.findViewById(R.id.dialog_desc);
        TextView dialogDate = view.findViewById(R.id.dialog_date);
        TextView dialogTime = view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    " + todo.getJournalCategory().getCategory());
        dialogDesc.setText(todo.getJournalDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String dateString = dateFormat.format(todo.getJournalDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeString = timeFormat.format(new Date(todo.getJournalTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(todo.getJournalPriority());
        dialogSwitch.setChecked(todo.isJournalSetAlarm());

        builder.setView(view);

        builder.setPositiveButton(getString(R.string.cancel), (dialog, which) -> {
            mUpcomingRecyclerAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.edit), (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this, TodoDetailsActivity.class);
            intent.putExtra(IntentConstants.REQ_CODE, EDIT_TODO);
            intent.putExtra(IntentConstants.JOURNAL, todo.getJournalId());
            startActivityForResult(intent, EDIT_TODO);
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void editDoneTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Todo todo = doneTodoArrayList.get(position);
        builder.setTitle(todo.getJournalName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view, null);
        TextView dialogCat = view.findViewById(R.id.dialog_category);
        TextView dialogDesc = view.findViewById(R.id.dialog_desc);
        TextView dialogDate = view.findViewById(R.id.dialog_date);
        TextView dialogTime = view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    " + todo.getJournalCategory().getCategory());
        dialogDesc.setText(todo.getJournalDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String dateString = dateFormat.format(todo.getJournalDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeString = timeFormat.format(new Date(todo.getJournalTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(todo.getJournalPriority());
        dialogSwitch.setChecked(todo.isJournalSetAlarm());

        builder.setView(view);

        builder.setPositiveButton(getString(R.string.cancel), (dialog, which) -> {
            mDoneRecyclerAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.edit), (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this, TodoDetailsActivity.class);
            intent.putExtra(IntentConstants.REQ_CODE, EDIT_TODO);
            intent.putExtra(IntentConstants.JOURNAL, todo.getJournalId());
            startActivityForResult(intent, EDIT_TODO);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}