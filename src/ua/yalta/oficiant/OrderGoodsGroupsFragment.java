package ua.yalta.oficiant;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.db.MetaDatabase;
import ua.yalta.oficiant.forms.InputDigitsForm;

import java.util.Stack;

public class OrderGoodsGroupsFragment extends Fragment {
    ListView groupsLW;
    ListView goodsLW;
    Button btnRootMenu;
    Button btnUpGrp;
    TextView statusTW;
    private SimpleCursorAdapter groupsAdapter;
    private SimpleCursorAdapter goodsAdapter;
   // private Cursor groupsCursor;
   // private Cursor goodsCursor;

    private String currentGroupCode;
    // стеке храним код родителя выбранной группы
    Stack grpSelectedStack=new Stack();


    OnPaneLTopListener mCallback;


    public static final String PRINT_ORDER="PRINT_ORDER";

    // Container Activity must implement this interface
    public interface OnPaneLTopListener {
        public void onButtonPanelClicked(String action);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnPaneLTopListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPaneLTopListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentGroupCode = "-1";
        initGroupsAdapter();
        initGoodsAdapter();
        setCursors();
        setRetainInstance(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewThis = inflater.inflate(R.layout.groupsgoodsfragment, container, false);
        groupsLW = (ListView) viewThis.findViewById(R.id.Groupslist) ;
        goodsLW = (ListView) viewThis.findViewById(R.id.Goodslist);

        groupsLW.setAdapter(groupsAdapter);
        goodsLW.setAdapter(goodsAdapter);

        groupsLW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Cursor c = (Cursor) groupsAdapter.getItem(pos);   //код группы становится родителем
                if (c != null) {
                    currentGroupCode = c.getString(c.getColumnIndex(MetaDatabase.Groups.CODE));
                    //noinspection unchecked
                    grpSelectedStack.add(c.getString(c.getColumnIndex(MetaDatabase.Groups.PARENTCODE)));
                    setCursors();
                }
            }
        });

        goodsLW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Cursor c = (Cursor) goodsAdapter.getItem(pos);   //код элемента становится родителем

                if (!(c == null)){
                    Intent digIntent = new Intent(Config.context, InputDigitsForm.class);
                    digIntent.putExtra("input_value", c.getString(c.getColumnIndex(MetaDatabase.Goods.NAME)));
                    digIntent.putExtra(MetaDatabase.Goods.CODE, c.getString(c.getColumnIndex(MetaDatabase.Goods.CODE)));
                    digIntent.putExtra(MetaDatabase.Goods.PRICE, c.getString(c.getColumnIndex(MetaDatabase.Goods.PRICE)));
                    digIntent.putExtra(MetaDatabase.Goods.NAME, c.getString(c.getColumnIndex(MetaDatabase.Goods.NAME)));
                    digIntent.putExtra(MetaDatabase.Goods.PRINTERCODE, c.getString(c.getColumnIndex(MetaDatabase.Goods.PRINTERCODE)));
                    digIntent.putExtra(MetaDatabase.Goods.BAZED, c.getString(c.getColumnIndex(MetaDatabase.Goods.BAZED)));
                    digIntent.putExtra(MetaDatabase.Goods.SKLADCODE, c.getString(c.getColumnIndex(MetaDatabase.Goods.SKLADCODE)));
                    digIntent.putExtra("req", 1);

                    getActivity().startActivityForResult(digIntent, OrderWorkActivity.REQ_TOVAR);
                }

            }
        });

        btnRootMenu = (Button) viewThis.findViewById(R.id.btn_root);
        btnRootMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentGroupCode.equals("-1")){
                    if(!grpSelectedStack.isEmpty())
                        grpSelectedStack.clear();
                    return;
                }

                currentGroupCode = "-1";
                grpSelectedStack.clear();
                setCursors();
            }
        });

        btnUpGrp = (Button) viewThis.findViewById(R.id.btn_grp_up);
        btnUpGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentGroupCode.equals("-1")){
                    if(!grpSelectedStack.isEmpty())
                        grpSelectedStack.clear();
                    return;
                }

                if(!grpSelectedStack.isEmpty()){
                    currentGroupCode= (String) grpSelectedStack.pop();
                }else{
                    currentGroupCode="-1";
                }

                setCursors();
            }
        });


        Button prnOrder=(Button)viewThis.findViewById(R.id.printOrderBtn);
        prnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // prepareForPrintOrder(adapter.getCursor(), orderID,strTotal);
             mCallback.onButtonPanelClicked(PRINT_ORDER);

            }
        });
        statusTW = (TextView)viewThis.findViewById(R.id.statusMes);
        Button backBtn=(Button)viewThis.findViewById(R.id.btn_backtoorders);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return viewThis;
    }

    public void initGroupsAdapter() {
        groupsAdapter = new SimpleCursorAdapter(Config.context, R.layout.simpleitemblack, null,
                new String[]{MetaDatabase.Groups.NAME},
                new int[]{R.id.simpleitem2},0);

    }

    public void initGoodsAdapter() {
        goodsAdapter = new SimpleCursorAdapter(Config.context, R.layout.simpleitem, null,
                new String[]{MetaDatabase.Goods.NAME},
                new int[]{R.id.simpleitem1},0);

    }

    public void setCursors() {
       Cursor groupsCursor = DBConnector.instance(Config.context).getGroupsByParent(currentGroupCode);
       Cursor goodsCursor = DBConnector.instance(Config.context).getGoodsByParent(currentGroupCode);
        groupsAdapter.changeCursor(groupsCursor);
        goodsAdapter.changeCursor(goodsCursor);
    }


    public void setStatusMes(String mes){
        statusTW.setText(mes);
    }

}
