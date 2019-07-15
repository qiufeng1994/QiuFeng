package cn.zju.id21732102.qiufeng;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import cn.iipc.android.tweetlib.Status;
import cn.iipc.android.tweetlib.YambaClient;
import cn.iipc.android.tweetlib.YambaClientException;

public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    static long DELAY = 60000;
    private boolean runFlag = false;
    private Updater updater;
    static String username;
    static String password;

    public UpdateService() {
    }
    @Override
    public void onCreate(){
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        username = prefs.getString("username","student");
        password = prefs.getString("password","password");
        DELAY = Long.parseLong(prefs.getString("interval","60"))*1000;

        this.updater = new Updater();
        Log.d(TAG,"onCreated");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!runFlag ){
            this.runFlag = true;
            this.updater.start();
        }
        Log.d(TAG,"onStarted");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.runFlag = false;
        this.updater.interrupt();
        this.updater = null;
        Log.d(TAG,"onDestroyed");
    }

    private class Updater extends Thread{
        public Updater(){
            super("UpdaterService-Thread");
        }
        @Override
        public void run(){
            DbHelper dbHelper =new DbHelper(UpdateService.this);
            while(runFlag){
                Log.d(TAG, "Running background thread");
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try{
//                    Thread.sleep(DELAY);
                    YambaClient cloud = new YambaClient("qiufeng1994", "950216");
                    List<Status> timeline = cloud.getTimeline(20);
                    ContentValues values = new ContentValues();

                    Log.d(TAG, "获取记录数：" + timeline.size());
                    int count = 0;
                    long rowID = 0;
                    for (Status status : timeline){
                        String usr = status.getUser();
                        String msg = status.getMessage();

                        values.clear();
                        values.put(StatusContract.Column.ID,status.getId());
                        values.put(StatusContract.Column.USER,usr);
                        values.put(StatusContract.Column.MESSAGE,msg);
                        values.put(StatusContract.Column.CREATED_AT,status.getCreatedAt().getTime());
                        rowID = db.insertWithOnConflict(StatusContract.TABLE,null,values, SQLiteDatabase.CONFLICT_IGNORE);
                        if(rowID != -1){
                            count++;
                            Log.d(TAG, String.format("%s: %s",usr,msg));
                        }
                    }
                    if (count >= 0){
                        Intent bcast = new Intent(StatusContract.NEW_STATUSES);
                        bcast.putExtra("count", count);
                        sendBroadcast(bcast);
                    }

                }catch (YambaClientException e){
//                    runFlag = false;
                    Log.e(TAG, "failed to tetch the timeline", e);
                    e.printStackTrace();
                }finally {
                    db.close();
                }
                try{
                    Thread.sleep(DELAY);
                }catch (InterruptedException e){
                    runFlag = false;
                }            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
