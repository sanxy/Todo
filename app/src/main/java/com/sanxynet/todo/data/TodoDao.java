package com.sanxynet.todo.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sanxynet.todo.model.Todo;

import java.util.List;

@Dao
public interface TodoDao {

    @Query("SELECT * from Todo ORDER BY priority DESC")
    List<Todo> getAllTodo();

    @Query("SELECT * FROM Todo WHERE ID = :id")
    Todo getTodoById(long id);

    @Insert
    void insertInDb(Todo todo);

    @Update
    void updateDb(Todo todo);

    @Delete
    void deleteFromDb(Todo todo);
}
