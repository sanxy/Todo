package com.sanxynet.todo.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sanxynet.todo.helper.Converters;
import com.sanxynet.todo.model.Category;
import com.sanxynet.todo.model.Todo;


@Database(entities = {Todo.class, Category.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TodoDatabase extends RoomDatabase {

    public static final String DB_NAME = "todo_db";
    private static TodoDatabase instance;
    private static Object LOCK = new Object();

    public static TodoDatabase getInstance(Context context){
        if (instance == null){
//            synchronized (LOCK){
                if (instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(), TodoDatabase.class,DB_NAME)
                            .build();
                }
//            }
        }
        return instance;
    }

    public abstract TodoDao todoDao();

    public abstract CategoryDao categoryDao();
}
