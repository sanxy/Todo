package com.sanxynet.todo;

import android.app.Application;
import android.content.SharedPreferences;

import com.sanxynet.todo.data.CategoryDao;
import com.sanxynet.todo.data.TodoDatabase;
import com.sanxynet.todo.helper.DbConstants;
import com.sanxynet.todo.model.Category;

import java.util.concurrent.Executors;

public class App extends Application {

    CategoryDao categoryDao;

    @Override
    public void onCreate() {
        super.onCreate();


        SharedPreferences appData = getSharedPreferences(getString(R.string.journal_shared_pref), MODE_PRIVATE);
        boolean isFirstTime = appData.getBoolean(getString(R.string.is_first_time), true);

        if (isFirstTime) {
            appData.edit().putBoolean(getString(R.string.is_first_time), false).apply();

            Executors.newSingleThreadExecutor().execute(() ->{
                TodoDatabase database = TodoDatabase.getInstance(this);
                categoryDao = database.categoryDao();

                categoryDao.newCat(new Category(DbConstants.CATEGORY_CHOOSE));
                categoryDao.newCat(new Category(DbConstants.CATEGORY_PERSONAL));
                categoryDao.newCat(new Category(DbConstants.CATEGORY_WORK));
                categoryDao.newCat(new Category(DbConstants.CATEGORY_TECH));
                categoryDao.newCat(new Category(DbConstants.CATEGORY_FINANCE));
                categoryDao.newCat(new Category(DbConstants.CATEGORY_OTHERS));
                    });

        }

    }
}
