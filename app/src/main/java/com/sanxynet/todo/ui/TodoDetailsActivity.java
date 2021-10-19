package com.sanxynet.todo.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.sanxynet.todo.model.Category;
import com.sanxynet.todo.data.CategoryDao;
import com.sanxynet.todo.helper.DbConstants;
import com.sanxynet.todo.helper.IntentConstants;
import com.sanxynet.todo.R;
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

public class TodoDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    final static int ADD_TODO = 0;
    final static int EDIT_TODO = 1;

    ArrayAdapter<String> adapter;
    List<Category> categoryList;
    List<String> categoryStringList;
    TodoDao todoDao;
    CategoryDao categoryDao;
    Todo caseTodo;
    int reqCode;

    private int yearTemp = 0;
    private int monthTemp = -1;
    private int dateTemp = -1;
    Date today = Calendar.getInstance().getTime();

    EditText journalNameEditText;
    Spinner journalCategorySpinner;
    EditText journalDateEditText;
    EditText journalTimeEditText;
    SwitchCompat journalAlarmSwitch;
    EditText journalDescEditText;
    RatingBar journalPriorityRatingBar;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);

        Intent intent = getIntent();
        reqCode = intent.getIntExtra(IntentConstants.REQ_CODE, -1);

        journalNameEditText = findViewById(R.id.journal_name_edit_text);
        journalCategorySpinner = findViewById(R.id.journal_category_spinner);
        journalDateEditText = findViewById(R.id.journal_date_edit_text);
        journalTimeEditText = findViewById(R.id.journal_time_edit_text);
        journalAlarmSwitch = findViewById(R.id.journal_alarm_switch);
        journalDescEditText = findViewById(R.id.journal_desc_edit_text);
        journalPriorityRatingBar = findViewById(R.id.journal_priority_rating_bar);
        submitButton = findViewById(R.id.journal_submit_button);

        categoryList = new ArrayList<>();
        categoryStringList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryStringList);
        TodoDatabase database = TodoDatabase.getInstance(this);
        categoryDao = database.categoryDao();

        Executors.newSingleThreadExecutor().execute(() -> {
            categoryList.clear();
            categoryList.addAll(categoryDao.getAllCategories());

            for (int i = 0; i < categoryList.size(); i++) {
                adapter.add(categoryList.get(i).getCategory());
            }
            adapter.notifyDataSetChanged();
        });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        journalCategorySpinner.setAdapter(adapter);

        if (reqCode == EDIT_TODO) {
            setTitle(getString(R.string.edit_journal));

            final long id = intent.getLongExtra(IntentConstants.JOURNAL, -1);
            TodoDatabase todoDatabase = TodoDatabase.getInstance(this);
            todoDao = todoDatabase.todoDao();

            Executors.newSingleThreadExecutor().execute(() -> {
                caseTodo = todoDao.getTodoById(id);

                journalNameEditText.setText(caseTodo.getJournalName());
                //got caseJournal category position from category list
                int index = categoryList.indexOf(caseTodo.getJournalCategory());
                journalCategorySpinner.setSelection(index);

                DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(caseTodo.getJournalDate());
                yearTemp = calendar.get(Calendar.YEAR);
                monthTemp = calendar.get(Calendar.MONTH);
                dateTemp = calendar.get(Calendar.DATE);
                String dateString = dateFormat.format(caseTodo.getJournalDate());
                journalDateEditText.setText(dateString);

                DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                String timeString = timeFormat.format(new Date(caseTodo.getJournalTime()));
                journalTimeEditText.setText(timeString);

                journalAlarmSwitch.setChecked(caseTodo.isJournalSetAlarm());
                journalDescEditText.setText(caseTodo.getJournalDesc());
                journalPriorityRatingBar.setRating(caseTodo.getJournalPriority());

            });


        } else if (reqCode == ADD_TODO) {
            setTitle(getString(R.string.add_journal));

            caseTodo = new Todo();
        }

        journalDateEditText.setOnClickListener(this);
        journalTimeEditText.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.journal_date_edit_text) {
            journalDateEditText.setError(null);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            showDatePicker(this, year, month, date);

        } else if (id == R.id.journal_time_edit_text) {
            if ((yearTemp == 0 || monthTemp == -1 || dateTemp == -1) || journalDateEditText.getText().toString().equals("")) {
                journalDateEditText.setError(getString(R.string.required_field));
                return;
            }

            Calendar newCalendar = Calendar.getInstance();
            int initialHour = newCalendar.get(Calendar.HOUR_OF_DAY);
            int initialMin = newCalendar.get(Calendar.MINUTE);
            showTimePicker(TodoDetailsActivity.this, initialHour, initialMin);

        } else if (id == R.id.journal_submit_button) {
            String name = journalNameEditText.getText().toString();
            if (name.equals("")) {
                journalNameEditText.setError(getString(R.string.required_field));
                return;
            }

            if (journalDateEditText.getText().toString().equals("")) {
                journalDateEditText.requestFocus();
                journalDateEditText.setError(getString(R.string.required_field));
                return;
            }

            if (journalTimeEditText.getText().toString().equals("")) {
                setTime(0, 0);
            }
            caseTodo.setJournalName(name);

            int categoryPosition = journalCategorySpinner.getSelectedItemPosition();
            if (categoryList.get(categoryPosition).getCategory().equals(DbConstants.CATEGORY_CHOOSE)) {
                int index = categoryStringList.indexOf(DbConstants.CATEGORY_OTHERS);
                caseTodo.setJournalCategory(categoryList.get(index));
            } else {
                caseTodo.setJournalCategory(categoryList.get(categoryPosition));
            }
            caseTodo.setJournalDesc(journalDescEditText.getText().toString());
            caseTodo.setJournalSetAlarm(journalAlarmSwitch.isChecked());
            caseTodo.setJournalPriority((int) journalPriorityRatingBar.getRating());

            if (reqCode == ADD_TODO) {
                TodoDatabase database = TodoDatabase.getInstance(this);
                todoDao = database.todoDao();

                Executors.newSingleThreadExecutor().execute(() -> {
                    todoDao.insertInDb(caseTodo);

                    setResult(RESULT_OK);
                    finish();
                });

            } else if (reqCode == EDIT_TODO) {
                TodoDatabase database = TodoDatabase.getInstance(this);
                todoDao = database.todoDao();

                Executors.newSingleThreadExecutor().execute(() -> {
                    todoDao.updateDb(caseTodo);

                    setResult(RESULT_OK);
                    finish();
                });

            }
        }
    }

    private void showDatePicker(Context context, int initialYear, int initialMonth,
                                int initialDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            yearTemp = year;
            monthTemp = month;
            dateTemp = dayOfMonth;

            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date selectedDate = calendar.getTime();
            if (selectedDate.before(today)) {
                Toast.makeText(TodoDetailsActivity.this, "Invalid Date", Toast.LENGTH_SHORT).show();
                return;
            }
            String dateString = dateFormat.format(selectedDate);
            journalDateEditText.setText(dateString);
        }, initialYear, initialMonth, initialDate);
        datePickerDialog.show();
    }

    private void showTimePicker(Context context, int initialHour, int initialMin) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> setTime(hourOfDay, minute), initialHour, initialMin, false);

        timePickerDialog.show();
    }

    public void setTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearTemp, monthTemp, dateTemp, hourOfDay, minute);
        caseTodo.setJournalDate(calendar.getTime());
        caseTodo.setJournalTime(calendar.getTimeInMillis());
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeString = timeFormat.format(new Date(caseTodo.getJournalTime()));
        journalTimeEditText.setText(timeString);
    }
}
