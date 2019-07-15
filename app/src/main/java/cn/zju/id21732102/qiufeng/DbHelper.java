package cn.zju.id21732102.qiufeng;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 63136 on 2019/6/14.
 */

public class DbHelper extends SQLiteOpenHelper  {
    private static final String TAG = DbHelper.class.getSimpleName();
    public DbHelper(Context context){
        super(context,StatusContract.DB_NAME, null,2);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = String.format(
                "create table %s (%s int primary key,%s text,%s text,%s int,%s text)",
                StatusContract.TABLE,
                StatusContract.Column.ID,
                StatusContract.Column.USER ,
                StatusContract.Column.MESSAGE ,
                StatusContract.Column.CREATED_AT,"avat");
        Log.d(TAG,"onCreate with SQL:" +sql);
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL(String.format("alert table %s add avat text default ''",StatusContract.TABLE));
    }
}
