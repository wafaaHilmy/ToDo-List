package com.example.android.todolist;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final String TAG = "AppExecutors";
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    public AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }
 //create one instance from that class
    public static AppExecutors getExecutorInstance(){
        if(sInstance==null){
            synchronized (new Object()){
                sInstance=new AppExecutors(Executors.newSingleThreadExecutor()
                        ,Executors.newScheduledThreadPool(3),new MainTreadExecutor());
            }

        }
        return sInstance;
    }

    public Executor getDiskIO() {
        return diskIO;
    }


    public Executor getNetworkIO() {
        return networkIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }


  //use that executor to make task on main thread by calling handler of mainthread looper and post on ui thread

    public static class MainTreadExecutor implements Executor {
        private Handler handler=new android.os.Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable runnable) {
            handler.post(runnable);

        }
    }



}
