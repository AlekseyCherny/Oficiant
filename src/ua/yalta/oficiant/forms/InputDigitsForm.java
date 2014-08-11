package ua.yalta.oficiant.forms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ua.yalta.oficiant.OrderWorkActivity;
import ua.yalta.oficiant.R;
import ua.yalta.oficiant.db.MetaDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 31.10.12
 * Time: 9:37
 * To change this template use File | Settings | File Templates.
 */
public class InputDigitsForm extends Activity {
    TextView input;
    TextView valueText;
    TextView helpText;
    TextView bazedText;
    TextView priceText;
    TextView mInLine;
    Button btnPoint;
    Intent intent;


    String goodsName;
    String goodsCode;
    String goodsPrice;
    String goodsQty;
    String goodsPrinter;
    String goodsBaseEd;
    int request;
    boolean justOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputdigitsform);
        // Установим CANCELED если юзер нажмет назад
        setResult(Activity.RESULT_CANCELED);
        btnPoint = (Button) findViewById(R.id.btnPoint);
        input = (TextView) findViewById(R.id.inputText);
        helpText = (TextView) findViewById(R.id.helpText);
        valueText = (TextView) findViewById(R.id.infoText);

        priceText=(TextView)findViewById(R.id.price);
        bazedText=(TextView)findViewById(R.id.bazed);

        intent = getIntent();

        valueText.setText(intent.getStringExtra("input_value"));
        justOpened=false;//default

        request = intent.getIntExtra("req", 1);
        ClearInput();

        //В зависимости от запроса в вызывающей форме
        if (request == 1) { //Tovar
            justOpened=true;
            goodsCode=intent.getStringExtra(MetaDatabase.Goods.CODE);
            goodsPrice=intent.getStringExtra(MetaDatabase.Goods.PRICE);
            goodsName=intent.getStringExtra(MetaDatabase.Goods.NAME);
            goodsPrinter=intent.getStringExtra(MetaDatabase.Goods.PRINTERCODE);
            goodsBaseEd=intent.getStringExtra(MetaDatabase.Goods.BAZED);
            bazedText.setText(goodsBaseEd);
            priceText.setText(goodsPrice);
            if(goodsBaseEd.contains("кг")){
                input.setText("1.000");
            }else{
                input.setText("1");
            }

        } else if (request == OrderWorkActivity.REQ_ORDER_ROW) {
            justOpened=true;
            goodsCode=intent.getStringExtra(MetaDatabase.OrdersRows.GOODS_CODE);
            goodsPrice=intent.getStringExtra(MetaDatabase.OrdersRows.PRICE);
            goodsName=intent.getStringExtra(MetaDatabase.OrdersRows.GOODS_NAME);
            goodsQty=intent.getStringExtra(MetaDatabase.OrdersRows.QTY);
            goodsPrinter=intent.getStringExtra(MetaDatabase.OrdersRows.PRINTER_CODE);
            goodsBaseEd=intent.getStringExtra(MetaDatabase.OrdersRows.BAZED);
            bazedText.setText(goodsBaseEd);
            priceText.setText(goodsPrice);
            input.setText(goodsQty);
        }




    }


    //Clickers
    public void NumBtnClick(View target) {
        if(justOpened){
            ClearInput();
            justOpened=false;
        }
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
        intent.putExtra("output", out);

        intent.putExtra(MetaDatabase.OrdersRows.GOODS_CODE,goodsCode);
        intent.putExtra(MetaDatabase.OrdersRows.GOODS_NAME,goodsName);
        intent.putExtra(MetaDatabase.OrdersRows.QTY,out);
        intent.putExtra(MetaDatabase.OrdersRows.PRICE,goodsPrice);
        intent.putExtra(MetaDatabase.OrdersRows.PRINTER_CODE,goodsPrinter);
       // intent.putExtra(MetaDatabase.OrdersRows._ID,orderRowId);
        intent.putExtra("OLD_QTY",goodsQty);

        setResult(RESULT_OK, intent);
        finish();
    }
    public void CanselBtnClick(View target){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }



    public void ClearBtnClick(View target) {
        justOpened=false;
        ClearInput();
    }

    public void ClearInput() {
        input.setText(null);
    }
}
