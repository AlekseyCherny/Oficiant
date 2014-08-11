package ua.yalta.oficiant.forms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ua.yalta.oficiant.R;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.db.MetaDatabase;

/**
 * Created by aleks on 11.05.14.
 */
public class PodborListForm extends Activity {
    public static final String EXTRA_TBL="DB_TABLE_NAME";
    ListView mList;
    ListAdapter listAdapter;
    Intent intent;
    private Cursor mCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.podborlist);

        intent=getIntent();
        String tblName=intent.getStringExtra(EXTRA_TBL);

        mCursor= DBConnector.instance(this).getRefCodeNameList(tblName);
        initAdapter();



        mList=(ListView)findViewById(R.id.podborListView);
        mList.setAdapter(listAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor)listAdapter.getItem(position);
                intent.putExtra("oCode", c.getString(1));
                intent.putExtra("oName", c.getString(2));
                setResult(RESULT_OK, intent);
                finish();
            }
        } );

       mList.setAdapter(listAdapter);



    }

    public void initAdapter() {
        listAdapter=new SimpleCursorAdapter(this,R.layout.podborlistitem,mCursor,
                new String[]{"name"},
                new int[]{R.id.simplepodboritem},0);
    }

    @Override
    protected  void onDestroy(){
        if(!mCursor.isClosed()){
            mCursor.close();
        }
        super.onDestroy();
    }
}
