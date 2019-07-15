package cn.zju.id21732102.qiufeng;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.nio.channels.InterruptedByTimeoutException;

import cn.iipc.android.tweetlib.Status;
import cn.iipc.android.tweetlib.SubmitProgram;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private TextView pkgName;
    private TextView text1;
    SQLiteDatabase db;
    Cursor cursor;
    DbHelper dbhlp;
    SimpleCursorAdapter adapter;
    ListView listStatus;
    private static final String[] FROM = { StatusContract.Column.USER,
            StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT};
    private static final int[] TO = {R.id.txtUser, R.id.txtMsg, R.id.txtTime};
    TimelineReceiver receiver;
    IntentFilter filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiver = new TimelineReceiver();
        filter = new IntentFilter(StatusContract.NEW_STATUSES);
        pkgName = (TextView)findViewById(R.id.pkgName);
        pkgName.setText("Context: " + this.getPackageName());
        listStatus = (ListView) findViewById(R.id.listStatus);

        dbhlp = new DbHelper(this);
        db = dbhlp.getReadableDatabase();
        cursor = db.query(StatusContract.TABLE, null, null, null, null, null,StatusContract.DEFAULT_SORT);
        startManagingCursor(cursor);
        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
        adapter.setViewBinder(new TimelineViewBinder());
        listStatus.setAdapter(adapter);
        listStatus.setOnItemClickListener(this);
    }

    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() == R.id.txtUser) return false;
//            if (view.getId() == R.id.txtMsg) {
//                String msg = cursor.getString(columnIndex);
//                if (msg.length() > 50) {
//                    msg = msg.substring(0, 50) + "......";
//                }
//                ((TextView) view).setText(msg);
//                return true;
//            } else if (view.getId() == R.id.image1) {
//                String url = cursor.getString(columnIndex);
//                if (url.length() > 0) Picasso.with(MainActivity.this)
//                        .load(url)
//                        .placeholder(R.mipmap.ic_launcher)
//                        .into((ImageView) view);
//                return true;
//            }
            long timestamp = cursor.getLong(columnIndex);
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(timestamp);
            ((TextView) view).setText(relativeTime);
            return true;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) listStatus.getItemAtPosition(position);
        String user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
        String msg = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));

        new AlertDialog.Builder(this).setTitle(user)
                .setMessage(msg)
                .setNegativeButton("关闭", null)
                .show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private boolean serviceRunning = true;

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        super.onMenuOpened(featureId, menu);
        if (menu == null) return true;

        MenuItem toggleItem = menu.findItem(R.id.action_start);
        toggleItem.setCheckable(serviceRunning);
        if (serviceRunning) {
            toggleItem.setTitle(R.string.stopservice);
            toggleItem.setIcon(android.R.drawable.ic_media_pause);
        } else {
            toggleItem.setTitle(R.string.startservice);
            toggleItem.setIcon(android.R.drawable.ic_media_play);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_calc:
                startActivity(new Intent(this, CalcActivity.class));
                return true;
            case R.id.action_filetest:
                startActivity(new Intent(this, FileWriteActivity.class));
                return true;
            case R.id.action_post:
                startActivity(new Intent(this, StatusActivity.class));
                return true;
            case R.id.action_homework:
                new SubmitProgram().doSubmit(this, "F2");
                return true;
//            case R.id.action_submit:
//                startActivity(new Intent(this, SettingsActivity.class));
            case R.id.action_start:
                if(serviceRunning){
                    stopService(new Intent(this, UpdateService.class));
                    serviceRunning = false;
                } else {
                    startService(new Intent(this, UpdateService.class));
                    serviceRunning = true;
                }
                return true;
            case R.id.action_music:
                startActivity(new Intent(this, MusicActivity.class));
                return true;
//            case R.id.action_tweet:
//                startActivity(new Intent("cn.zju.id.21732102.tweet"));
//                return true;
            case R.id.action_stop:
                stopService(new Intent(this, UpdateService.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_close:
                finish();
                return true;
            case R.id.action_delete:
                SQLiteDatabase dbw = dbhlp.getWritableDatabase();
                dbw.delete(StatusContract.TABLE, null, null);
                cursor.requery();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "所有数据已被删除！", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    class TimelineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TimelineReceiver", "onReceived");
            int count = intent.getIntExtra("count", 0);
            if (count > 0) {
                cursor.requery();
                adapter.notifyDataSetChanged();
            }
            Toast.makeText(MainActivity.this,
                    "更新了" + count + "条记录。", Toast.LENGTH_LONG).show();
        }
    }
}

