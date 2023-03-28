package com.example.android.todolist.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

public class AddTaskViewModel extends ViewModel {

   LiveData<TaskEntry> selectedTask;

   public AddTaskViewModel(AppDatabase database,int mId ) {

      selectedTask=database.taskDAO().loadTaskById(mId);
   }

   public LiveData<TaskEntry> getSelectedTask() {
      return selectedTask;
   }
}
