package cn.zju.id21732102.qiufeng;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.iipc.android.tweetlib.SubmitProgram;

public class MusicActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private TextView text1, txtPkg_music, pkgName_Music;
    private ListView listMusic;
    private ArrayList<String> list = new ArrayList<>();
//    private ArrayList<Music> list = new ArrayList<>();
    private MediaPlayer player = new MediaPlayer();
    public Context context;

//    public MusicActivity(Context context) {
//        this.context = context;
//    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    private void getList(){

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            ContentResolver provider=getContentResolver();

//            Cursor cursor = provider.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    new String[]{"artist", "title", "duration"}, null, null, null);
            Cursor cursor = provider.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{"artist", "title", "duration", MediaStore.Audio.Media.DATA},null,null,null);
            String s;
            while (cursor.moveToNext()) {
//                int t = Integer.parseInt(cursor.getString(1))/1000;
//                s = String.format("%d:%d",t/60,t%60);
//                list.add(new Music(cursor.getString(0),s,
//                        cursor.getString(2), cursor.getString(3)));
                s = String.format("作者：%s    曲名：%s    时间：%d（秒）\n%s",
                        cursor.getString(0),
                        cursor.getString(1),
                        Integer.parseInt(cursor.getString(2)) / 1000,
                        cursor.getString(3));
                list.add(s);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
//        text1 = (TextView) findViewById(R.id.textMusic);
//
        txtPkg_music = (TextView) findViewById(R.id.pkgname_music);
        txtPkg_music.setText(getPackageName());
        listMusic = (ListView)findViewById(R.id.listMusic);
        getList();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
//        MusicAdapter adapter=new MusicAdapter(this, R.layout.music_item,list);
        listMusic.setAdapter(adapter);
        listMusic.setOnItemClickListener(this);
//}
//

//        text1 = (TextView) findViewById(R.id.textMusic);
//        pkgName_Music = (TextView) findViewById(R.id.pkgname_music);
//        pkgName_Music.setText(getPackageName());
//            ContentResolver provider = getContentResolver();

//        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
//            Cursor cursor = provider.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            new String[]{"artist", "title", "duration"}, null, null, null);
//            String s = "";
//            while (cursor.moveToNext()) {
//                s += String.format("作者：%s    曲名：%s    时间：%d（秒）\n \n",
//                        cursor.getString(0),
//                        cursor.getString(1),
//                        Integer.parseInt(cursor.getString(2)) / 1000);
//        }
//
//            text1.setText(s);
//        }

    }



    @Override
    protected void onDestroy() {

        if(player.isPlaying())
            player.stop();
        player.release();
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }


    public class Music{
        public String name, duration, author, uri;
        public Music(String name, String duration, String author, String uri){
            this.name=name;
            this.duration=duration;
            this.author=author;
            this.uri=uri;
        }
    }

    public class MusicAdapter extends ArrayAdapter<Music> {
        private int rowResource;
        public MusicAdapter(Context context, int rowResource, List<Music> objs){
            super(context, rowResource, objs);
            this.rowResource=rowResource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Music music = getItem(position);
            View view;
            if (convertView==null){
                view = LayoutInflater.from(getContext()).inflate(rowResource, parent, false);
            } else view=convertView;
            TextView txtName, txtDuration, txtAuthor;
            txtName=(TextView) view.findViewById(R.id.txtName);
            txtDuration =  (TextView) view.findViewById(R.id.txtDuration);
            txtAuthor = (TextView) view.findViewById(R.id.textMsg);

            txtName.setText(music.name);
            txtDuration.setText(music.duration);
            txtAuthor.setText(music.author);
            return view;
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_submit) {
            new SubmitProgram().doSubmit(this,"F1" );
            return true;
        }
        if (id == R.id.action_close){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        player.reset();
        try {
            String a[] = (list.get(position)).split("\\n");
            player.setDataSource(a[1]);
//            player.setDataSource(list.get(position).uri);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
