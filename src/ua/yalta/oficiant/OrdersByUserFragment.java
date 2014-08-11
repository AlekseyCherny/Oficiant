package ua.yalta.oficiant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.db.MetaDatabase;
import ua.yalta.oficiant.forms.ShapkaOrderForm;

/**
 * Created by aleks on 27.04.14.
 */
public class OrdersByUserFragment extends Fragment {
    ListView openedOrdersLW;
    private SimpleCursorAdapter openedOrdersAdapter;
   // private Cursor openedOrdersCursor;


    Button btnAddNewOrder;

    public static final int REQ_NEW_ORDER=10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOpenedOrdersAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        //if(!openedOrdersCursor.isClosed())
         //   openedOrdersCursor.close();
        openedOrdersAdapter.changeCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCursors();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewThis = inflater.inflate(R.layout.orderslistfragment, container, false);

        openedOrdersLW=(ListView) viewThis.findViewById(R.id.listOrders);
        openedOrdersLW.setAdapter(openedOrdersAdapter);


        openedOrdersLW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) openedOrdersAdapter.getItem(position);   //код группы становится родителем
                if (c != null) {
                    long orderID = c.getLong(c.getColumnIndex(MetaDatabase.Orders._ID));
                    openOrder(orderID);
                }
            }
        });

        btnAddNewOrder=(Button) viewThis.findViewById(R.id.btn_addorder);
        btnAddNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewOrderActivity();
                //String defZal= PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_def_zal),"");
               // long orderID=DBConnector.instance(Config.context).addNewOrder(Config.glUserUUID,defZal);
               // openOrder(orderID);
            }
        });

        Button btnExit=(Button) viewThis.findViewById(R.id.btn_exitfromorders);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return viewThis;
    }

    public void openNewOrderActivity(){
        Intent intent=new Intent(Config.context,ShapkaOrderForm.class);
        startActivityForResult(intent,REQ_NEW_ORDER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==getActivity().RESULT_OK && requestCode==REQ_NEW_ORDER){
            String zal=data.getStringExtra("zal");
            String stol=data.getStringExtra("stol");
            long orderID=DBConnector.instance(Config.context).addNewOrder(Config.glUserUUID,zal,stol);
            openOrder(orderID);
        }
    }

    public void openOrder(long mOrderId){
        Intent intent=new Intent(Config.context,OrderWorkActivity.class);
        intent.putExtra("orderID",mOrderId);
        startActivity(intent);

    }


    public void initOpenedOrdersAdapter() {
        openedOrdersAdapter = new SimpleCursorAdapter(Config.context, R.layout.orderlistitem, null,
                new String[]{MetaDatabase.Orders._ID,MetaDatabase.Orders.USER,MetaDatabase.Orders.DATE_DOCS,MetaDatabase.Orders.COMMENT,MetaDatabase.Orders.ZAL,MetaDatabase.Orders.STOL,MetaDatabase.Orders.SUM},
                new int[]{R.id.orderlistNumber,R.id.orderlistUser,R.id.orderlistDate,R.id.orderlistComment,R.id.orderlistZal,R.id.orderlistStol,R.id.orderlistSum},0);

    }

    public void setCursors() {
        Cursor openedOrdersCursor;
       if(Config.glUserUUID.equals("-1") || Config.glUserUUID.equals("-777"))
           openedOrdersCursor = DBConnector.instance(Config.context).getOrdersOpenedStatusAll();
       else
           openedOrdersCursor = DBConnector.instance(Config.context).getOrdersOpenedStatusByUser(Config.glUserUUID);

       openedOrdersAdapter.changeCursor(openedOrdersCursor);

    }
}
