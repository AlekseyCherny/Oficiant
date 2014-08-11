package ua.yalta.oficiant.forms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.*;
import ua.yalta.oficiant.R;
import ua.yalta.oficiant.db.DBConnector;

/**
 * Created by aleks on 24.05.14.
 */
public class MultiSelectListForm extends Activity {
    public static final String EXTRA_TBL="DB_TABLE_NAME";
    ListView mList;
    ListAdapter listAdapter;
    Intent intent;
    private Cursor mCursor;

    TextView selection;
    //String selectionCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multipodborlistform);

        setResult(Activity.RESULT_CANCELED);

        intent=getIntent();
        String tblName=intent.getStringExtra(EXTRA_TBL);
        String selNames=intent.getStringExtra("RESULT");

        mCursor= DBConnector.instance(this).getRefCodeNameList(tblName);
        initAdapter();

        selection=(TextView)findViewById(R.id.selection);

        mList=(ListView)findViewById(R.id.multipodborListView);
        mList.setAdapter(listAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Очистим TextView
                selection.setText("");
                //selectionCodes="";
                // получим булев массив для каждой позиции списка
                // Объект SparseBooleanArray содержит массив значений, к которым можно получить доступ
                // через valueAt(index) и keyAt(index)
                SparseBooleanArray chosen = ((ListView) parent).getCheckedItemPositions();
                for (int i = 0; i < chosen.size(); i++) {

                    int mPosition=chosen.keyAt(i);
                    // если пользователь выбрал пункт списка,
                    // то выводим его в TextView.
                    if (chosen.valueAt(i)) {
                        Cursor c = (Cursor) listAdapter.getItem(mPosition);
                        String name=c.getString(c.getColumnIndex("name"));
                        //String code=c.getString(c.getColumnIndex("code"));
                        selection.append(name+ "\n");
                        //selectionCodes=selectionCodes+code+ ";";
                    }
                }

            }
        } );

        mList.setAdapter(listAdapter);

        if(selNames!=null) {
            String[] partsNames = selNames.split(";");
            if(partsNames.length>0)
              setCheckedByNames(partsNames);
        }


        Button btnOK=(Button)findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strRes=selection.getText().toString();
                String resSplited=strRes.replace("\n",";");
                intent.putExtra("RESULT",resSplited);
                //intent.putExtra("RESULT_CODES",selectionCodes);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button btnCancel=(Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private void setCheckedByNames(String[] partsNames){
        selection.setText("");
        //selectionCodes="";

        for (String cur:partsNames) {
            if(cur==null || cur.length()==0)
                continue;

            for (int i = 0; i < listAdapter.getCount(); i++) {
                Cursor c = (Cursor) listAdapter.getItem(i);
                //String code = c.getString(c.getColumnIndex("code"));
                String name=c.getString(c.getColumnIndex("name"));
                if(name.equals(cur)){
                    mList.setItemChecked(i,true);
                    selection.append(name+ "\n");
                    //selectionCodes=selectionCodes+code+ ";";
                }

            }
        }
    }

    public void initAdapter() {
        listAdapter=new SimpleCursorAdapter(this,android.R.layout.simple_list_item_multiple_choice,mCursor,
                new String[]{"name"},
                new int[]{android.R.id.text1},0);
    }

    @Override
    protected  void onDestroy(){
        if(!mCursor.isClosed()){
            mCursor.close();
        }
        super.onDestroy();
    }
}
