package cn.zju.id21732102.qiufeng;

import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cn.iipc.android.tweetlib.*;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }



    public static class SettingsFragment extends PreferenceFragment {
        public SettingsFragment(){
        }
        @Override
        public void onCreate(@Nullable  Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
    public void onClick(View view)
    {

    }

    public void afterTextChanged(Editable s){

    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after){
    }

    public void onTextChanged(CharSequence s,int start, int before, int count){

    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            new SubmitProgram().doSubmit(this, "E2");
            return true;
        }
        if (id == R.id.action_close){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

