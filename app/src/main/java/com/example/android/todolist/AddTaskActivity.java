/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.android.todolist.database.AddTaskFactoryViewModel;
import com.example.android.todolist.database.AddTaskViewModel;
import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

import java.util.Date;


public class AddTaskActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    // Fields for views
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;
    AppDatabase appDatabase;
    TaskEntry selectedTask;

    AddTaskViewModel addTaskViewModel;


    private int mTaskId = DEFAULT_TASK_ID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

      appDatabase  =AppDatabase.getInstanceAppDatabase(getApplicationContext());

        initViews();
// to handle rotation save id
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText(R.string.update_button);
            getSupportActionBar().setTitle(R.string.update_button);

            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId= intent.getIntExtra(EXTRA_TASK_ID,DEFAULT_TASK_ID);


//creat factory that can have inputs.passing to view model..then create view model
                AddTaskFactoryViewModel VMFactory=new AddTaskFactoryViewModel(mTaskId,appDatabase);
                addTaskViewModel= ViewModelProviders.of(this,VMFactory).get(AddTaskViewModel.class);

                // final LiveData<TaskEntry> selectedTask=appDatabase.taskDAO().loadTaskById(mTaskId);
               final LiveData<TaskEntry> selectedTask=addTaskViewModel.getSelectedTask();
                selectedTask.observe(AddTaskActivity.this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(@Nullable TaskEntry taskEntry) {
                        // populate the UI
                        populateUI(taskEntry);
                        selectedTask.removeObserver(this);
                    }
                });




            }
        }
    }
/*---------------------------------------------------------------------------------------------------------*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //save  current task id
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);
        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        mEditText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
       String description= mEditText.getText().toString().trim();
       int priority = getPriorityFromViews();
        Date date= new Date();
     final   TaskEntry currentEntry= new TaskEntry(description,priority,date);
     //at insert mode id=-1 no intent found

            AppExecutors.getExecutorInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mTaskId==DEFAULT_TASK_ID) {
                    appDatabase.taskDAO().insertTask(currentEntry);
                    Log.e(TAG, "onSaveButtonClicked: /////////////////////////////////// insert data");
                    }else {
                        //update mode mid not equal to default
                        currentEntry.setId(mTaskId);
                        appDatabase.taskDAO().updateTask(currentEntry);
                        Log.e(TAG, "onSaveButtonClicked: /////////////////////////////////// update data");
                    }
                    finish();
                }
            });



    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }
}
