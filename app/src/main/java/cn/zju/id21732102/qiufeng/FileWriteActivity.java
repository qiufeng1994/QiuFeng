package cn.zju.id21732102.qiufeng;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import cn.iipc.android.tweetlib.SubmitProgram;

public class FileWriteActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button1, button2, button3;
    private TextView textMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_write);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        textMsg = (TextView) findViewById(R.id.textMsg);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if(v==button1){
            fileTestWrite(getFilesDir().getPath());
//        }
//        else if(v==button2){
//                    fileTestWrite(Environment.getExternalStorageDirectory().getPath());
//            if (ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
//            } else {
//                fileTestWrite(Environment.getExternalStorageDirectory().getPath());
//            }
        }else if(v==button2){
            fileTestWrite(Environment.getExternalStorageDirectory().getPath());
        }else if(v==button3){
            fileTestWrite(getExternalFilesDir(null).getPath());
        }
    }
    private void fileTestWrite(String dir){
        String fn = dir + "/hello.txt";
        try{
            textMsg.setText(textMsg.getText() + "\n Write to: " + fn);
            PrintWriter o = new PrintWriter(new BufferedWriter(new FileWriter(fn)));
            o.println(fn);
            o.close();
        }catch (Exception e){
            textMsg.setText(textMsg.getText() + "\n Write to: " + e.toString());
        }
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = new MenuInflater(this);
//        inflater.inflate(R.menu.menu,menu);
//        MainActivity main_object = new MainActivity();
//        main_object.setIconEnable(menu,true);  //  就是这一句使图标能显示
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            new SubmitProgram().doSubmit(this, "E1");
            return true;
        }
        if (id == R.id.action_close){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
