package com.example.android.todolist.database;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class AddTaskFactoryViewModel extends ViewModelProvider.NewInstanceFactory {
   private final int mId;
  private final   AppDatabase database;

    public AddTaskFactoryViewModel(int mId, AppDatabase database) {
        this.mId = mId;
        this.database = database;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTaskViewModel(database,mId);
    }
}
