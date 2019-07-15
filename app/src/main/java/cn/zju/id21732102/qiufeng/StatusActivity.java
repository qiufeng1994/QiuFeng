package cn.zju.id21732102.qiufeng;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.iipc.android.tweetlib.SubmitProgram;
import cn.iipc.android.tweetlib.YambaClient;
import cn.iipc.android.tweetlib.YambaClientException;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher{
    private EditText editStatus;
    private Button btnPost;
    private TextView txtCount,pkgName;
    static final String TAG = "StatusActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        editStatus = (EditText) findViewById(R.id.editStatus);
        btnPost = (Button) findViewById(R.id.btnPost);
        txtCount = (TextView)findViewById(R.id.txtCount);
        pkgName = (TextView)findViewById(R.id.txtPkg);
        pkgName.setText(this.getPackageName());

        editStatus.addTextChangedListener(this);
        btnPost.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            new SubmitProgram().doSubmit(this, "D1");
            return true;
        }
        if (id == R.id.action_close){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    @Override
    public void onClick(View v) {
        String status = editStatus.getText().toString();
        Log.d(TAG, "Be clicked with status: " + status);
        new PostTask().execute(status);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    private final class PostTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params){

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StatusActivity.this);
            String username = prefs.getString("username","");
            String password = prefs.getString("password", "");
//            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
//                startActivity(new Intent(StatusActivity.this, SettingsActivity.class));
//                return "Please update your username and password";
//            }
            YambaClient yambaCloud = new YambaClient("qiufeng1994", "950216");
//            YambaClient yambaCloud = new YambaClient(username, password);
            try{
                yambaCloud.postStatus(params[0]);
                return "Successfully posted: " + params[0].length() + " chars";
            } catch (YambaClientException e){
                e.printStackTrace();
                return "Failed to post to yamba service";
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
            pkgName.setText(result + " BY<" + getPackageName() + ">");
            if (result.startsWith("Successfully")){
                editStatus.setText("");
            }
        }
    }


}
