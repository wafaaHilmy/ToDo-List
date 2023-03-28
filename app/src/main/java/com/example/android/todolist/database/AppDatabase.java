package com.example.android.todolist.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;



import java.util.List;

@Database(entities = TaskEntry.class, version = 2,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG=AppDatabase.class.getSimpleName();

    private static AppDatabase sInstanceAppDatabase ;
    private static final Object syncObject=new Object();
    private static final String DATABASE_NAME = "todolist";

// method to creat Database abd return it
public static AppDatabase getInstanceAppDatabase(Context context){
    //at first creation of db
    if(sInstanceAppDatabase==null){

        // allow one operation in thread
        synchronized (syncObject){
            Log.d(LOG_TAG, "Creating new database instance...............");
            sInstanceAppDatabase= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,DATABASE_NAME)
                    .build();

        }
    }
    Log.d(LOG_TAG, "Getting the database instance..................");
    return  sInstanceAppDatabase;
}
//get instance from task Dao
public abstract TaskDAO taskDAO() ;
}
