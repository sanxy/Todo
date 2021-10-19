package com.sanxynet.todo.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sanxynet.todo.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category")
    List<Category> getAllCategories();


    @Insert
    void newCat(Category category);
}
