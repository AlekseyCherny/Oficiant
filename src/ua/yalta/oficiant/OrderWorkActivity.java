package ua.yalta.oficiant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import ua.yalta.oficiant.db.MetaDatabase;


public class OrderWorkActivity extends FragmentActivity implements OrderGoodsGroupsFragment.OnPaneLTopListener,OrderFragment.OnStatusListener {
    public static final int REQ_TOVAR=1;
    public static final int REQ_ORDER_ROW=4;

   // public static final int REQ_ORDER_PAY=9;

    public long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.orderworkactivity);

        Intent intent=getIntent();
        orderId=intent.getLongExtra("orderId",0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       // Log.d("ON_ACTIVITY RES",String.valueOf(resultCode));
      //  Log.d("ON_ACTIVITY REQ",String.valueOf(requestCode));
        if(resultCode==RESULT_OK && requestCode==REQ_TOVAR){
            OrderFragment orderFragment= (OrderFragment) getSupportFragmentManager().findFragmentById(R.id.order_fragment);
            orderFragment.doRowActions(orderFragment.ROW_ADD, data);
        }else if(resultCode==RESULT_OK && requestCode==REQ_ORDER_ROW){
            OrderFragment orderFragment= (OrderFragment) getSupportFragmentManager().findFragmentById(R.id.order_fragment);
            orderFragment.doRowActions(orderFragment.ROW_EDIT, data);
    //    }else if(resultCode==RESULT_OK && requestCode==REQ_ORDER_PAY){
    //        OrderFragment orderFragment= (OrderFragment) getSupportFragmentManager().findFragmentById(R.id.order_fragment);

        }

    }


    @Override
    public void onButtonPanelClicked(String action) {
       if(action.equals(OrderGoodsGroupsFragment.PRINT_ORDER)){
           OrderFragment orderFragment= (OrderFragment) getSupportFragmentManager().findFragmentById(R.id.order_fragment);
           orderFragment.printOrder();
       }
    }

    @Override
    public void onStatusChanged(String action) {
        OrderGoodsGroupsFragment goodsFragment= (OrderGoodsGroupsFragment) getSupportFragmentManager().findFragmentById(R.id.goodsgroups_fragment);
        goodsFragment.setStatusMes(action);
    }
}
