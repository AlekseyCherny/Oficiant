package ua.yalta.oficiant.forms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ua.yalta.oficiant.Config;
import ua.yalta.oficiant.R;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.db.MetaDatabase;

/**
 * Created by aleks on 24.05.14.
 */
public class ShapkaOrderForm extends Activity {
    TextView input;
    Intent intent;
    ListView mList;
    ListAdapter listAdapter;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shapkaorder);
        setResult(Activity.RESULT_CANCELED);
        input = (TextView) findViewById(R.id.inputTextStol);
        intent = getIntent();

        mCursor= DBConnector.instance(this).getZals();
        initAdapter();

        mList=(ListView)findViewById(R.id.zalListView);
        mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mList.setAdapter(listAdapter);

    }


    public void initAdapter() {
        listAdapter=new SimpleCursorAdapter(this,android.R.layout.simple_list_item_single_choice,mCursor,
                new String[]{"name"},
                new int[]{android.R.id.text1},0);
    }
    //Clickers
    public void NumBtnClick(View target) {
        TextView c = (TextView) target;
        CharSequence c2 = c.getText();
        String old = input.getText().toString();
        String newS = old + c2.toString();
        input.setText(newS);
    }

    public void OKBtnClick(View target) {
        String out = input.getText().toString();
        if (out.length() == 0) {
            return;
        }

        int checkedPos=mList.getCheckedItemPosition();
        if(checkedPos==AbsListView.INVALID_POSITION){
            return;
        }

        Cursor c= (Cursor) listAdapter.getItem(checkedPos);
        intent.putExtra("zal", c.getString(c.getColumnIndex(MetaDatabase.Zals.NAME)));
        intent.putExtra("stol", "Стол "+out);




        setResult(RESULT_OK, intent);
        finish();
    }
    public void CanselBtnClick(View target){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }



    public void ClearBtnClick(View target) {
        ClearInput();
    }

    public void ClearInput() {
        input.setText(null);
    }

    @Override
    protected  void onDestroy(){
        if(!mCursor.isClosed()){
            mCursor.close();
        }
        super.onDestroy();
    }
}
