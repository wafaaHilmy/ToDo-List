package com.example.android.todolist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";

  private   AppDatabase database;
   private LiveData<List<TaskEntry>> allTasks;


    public MainViewModel(@NonNull Application application) {
        super(application);
        // Context context=this.getApplication();
    database= AppDatabase.getInstanceAppDatabase(application.getBaseContext());
        database= AppDatabase.getInstanceAppDatabase(this.getApplication());

    allTasks=database.taskDAO().loadAllTasks();

    }

    public LiveData<List<TaskEntry>> getAllTasks() {
        return allTasks;
    }
}
