package ua.yalta.oficiant;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.db.MetaDatabase;
import ua.yalta.oficiant.exchange.Exporter;
import ua.yalta.oficiant.forms.InputDigitsForm;
import ua.yalta.oficiant.forms.MultiSelectListForm;
import ua.yalta.oficiant.forms.PodborListForm;
import ua.yalta.oficiant.net.NetUtils;
import ua.yalta.oficiant.net.Socket1c;
import ua.yalta.oficiant.refs.DaoPrinter;
import ua.yalta.oficiant.refs.DaoUsers;
import ua.yalta.oficiant.utils.PosPrinter;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;






public class OrderFragment extends Fragment {
    //ACTIONS WITH ROWS
    public static final int ROW_ADD=1;
    public static final int ROW_EDIT=2;
    public static final int ROW_DELETE=3;

    public static final int ROW_MODIFICATORS=9;

    public static final int ORDER_PAY=30;//Оплата ЗАКАЗА
    public static final int ORDER_CANCEL=31;//ОТМЕНА ЗАКАЗА
    public static final int ORDER_PAY_SPISANIE=33;

    ListView orderLW;
    private SimpleCursorAdapter adapter;
    long orderID;
    TextView twTotal;
    String strTotal;

    String ordUserName;
    String ordZalName;
    String ordStol;
    int ordOplachen;
    int ordPrinted;

    OnStatusListener mCallback;
    // Container Activity must implement this interface
    public interface OnStatusListener {
        public void onStatusChanged(String action);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnStatusListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPaneLTopListener");
        }

    }
   // ArrayList<String> printerCodesWithError=new ArrayList<String>();
   // ArrayList<String> printerCodesNOError=new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        Intent intent = getActivity().getIntent();
        orderID=intent.getLongExtra("orderID",0);

        initAdapter();
        setOrderShapkaData();
        setCursors();

        //TODO NOmer zakaza==0
    }

    private void setOrderShapkaData(){
        Cursor cursor=DBConnector.instance(Config.context).getOrderById(orderID);
        cursor.moveToFirst();
        String mUserCode=cursor.getString(cursor.getColumnIndex(MetaDatabase.Orders.USER));
        for (DaoUsers curUser:Config.glUsers) {
            if (curUser.getCode().equals(mUserCode)) {
                ordUserName= curUser.getName();
            }
        }


        ordZalName=cursor.getString(cursor.getColumnIndex(MetaDatabase.Orders.ZAL));
        ordOplachen=cursor.getInt(cursor.getColumnIndex(MetaDatabase.Orders.OPLACHEN));
        ordPrinted=cursor.getInt(cursor.getColumnIndex(MetaDatabase.Orders.PRINTED));
        ordStol=cursor.getString(cursor.getColumnIndex(MetaDatabase.Orders.STOL));
        cursor.close();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewThis = inflater.inflate(R.layout.orderfragment, container, false);
        orderLW = (ListView) viewThis.findViewById(R.id.listOrder);
        orderLW.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        orderLW.setAdapter(adapter);
        orderLW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                orderLW.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                openKeyPanelForQTY(position);
            }
        });

        orderLW.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                orderLW.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                showLongClickRowMenu(position,id);
                return true;
            }
        });

        twTotal=(TextView)viewThis.findViewById(R.id.twTotal);
        twTotal.setText(strTotal);


        Button prnBut=(Button)viewThis.findViewById(R.id.btn_print);
        prnBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareForPrintVstrecka(adapter.getCursor(), orderID);

            }
        });
//        Button prnOrder=(Button)viewThis.findViewById(R.id.btn_printOrder);
//        prnOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                printOrder();
//            }
//        });
        Button actButton=(Button)viewThis.findViewById(R.id.btn_orderact);
        actButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderActions();
            }
        });
        return viewThis;
    }


    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void initAdapter() {
        adapter = new SimpleCursorAdapter(Config.context,
                R.layout.orderitem,
                null, new String[]{
                MetaDatabase.OrdersRows.QTY,
                MetaDatabase.OrdersRows.GOODS_NAME,
                MetaDatabase.OrdersRows.PRICE,
                MetaDatabase.OrdersRows.SUM,
                MetaDatabase.OrdersRows.GOODS_CODE //Not visible
        }, new int[]{
                R.id.orderQTY,
                R.id.orderGoodsName,
                R.id.orderPrice,
                R.id.orderSum
        },0
        );

        // orderLW.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }



    public void setCursors() {
       Cursor rowsCursor = DBConnector.instance(Config.context).getOrderRowsById(orderID);
       adapter.changeCursor(rowsCursor);

        strTotal=calculateTotal(rowsCursor);
    }

    public void updateOrderSumInDB(){
        DBConnector.instance(Config.context).setOrderSum( orderID,strTotal);
    }

    //from host activity
    public void doRowActions(int action,Intent data){
        if(ordPrinted==1){
            mCallback.onStatusChanged("Счет был напечатан!");
            return;
        }
        orderLW.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        switch (action){
            case ROW_ADD:
                    calculateRow(data);
                    DBConnector.instance(Config.context).addRowToOrder(data, orderID);
                break;
            case ROW_EDIT:
                    //TODO PROVERKI NA IZMENENIE
                    calculateRow(data);
                    DBConnector.instance(Config.context).changeRowOrder(data, orderID);
                break;
            case ROW_DELETE:
                    long rowId = data.getLongExtra(MetaDatabase.OrdersRows._ID, 0);
                    DBConnector.instance(Config.context).deleteRowOrder(rowId, orderID);

                break;
            default:
                break;
        }

        setCursors();
        twTotal.setText(strTotal);
        updateOrderSumInDB();
    }

    public void printOrder(){
        prepareForPrintOrder(adapter.getCursor(), orderID,strTotal);
        //setCursors();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==getActivity().RESULT_OK && requestCode==ORDER_PAY){
            String mVidOplat=data.getStringExtra("oName");

            if(mVidOplat.contains("писание")){
                Intent intent = new Intent(getActivity(),PodborListForm.class);
                intent.putExtra(PodborListForm.EXTRA_TBL, MetaDatabase.Clients.TABLE_NAME);
                intent.putExtra("VID_OPLAT", mVidOplat);//remember
                startActivityForResult(intent, ORDER_PAY_SPISANIE);
            }else {
                DBConnector.instance(Config.context).payOrder(orderID, mVidOplat,"");
                Exporter exporter = new Exporter(null, 0);
                exporter.startExport();
                getActivity().finish();
            }

        }else if (resultCode==getActivity().RESULT_OK && requestCode==ROW_MODIFICATORS){
            String res=data.getStringExtra("RESULT");
            long rowID=data.getLongExtra("_ID", 0);
            DBConnector.instance(Config.context).updateRowModificators(rowID, orderID, res);
            Cursor rowsCursor = DBConnector.instance(Config.context).getOrderRowsById(orderID);
            adapter.changeCursor(rowsCursor);
        }else if (resultCode==getActivity().RESULT_OK && requestCode==ORDER_PAY_SPISANIE){
            String mClient=data.getStringExtra("oName");
            String mVidOplat=data.getStringExtra("VID_OPLAT");
            DBConnector.instance(Config.context).payOrder(orderID, mVidOplat,mClient);
            Exporter exporter = new Exporter(null, 0);
            exporter.startExport();
            getActivity().finish();
        }
    }

    public void openModificators(int position,long mID){
        Cursor c = (Cursor) adapter.getItem(position);
        if (c != null) {
            Intent mIntent = new Intent(Config.context, MultiSelectListForm.class);
            mIntent.putExtra(MultiSelectListForm.EXTRA_TBL,MetaDatabase.Modificators.TABLE_NAME);
            mIntent.putExtra("name",c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.GOODS_NAME)));
            mIntent.putExtra("RESULT",c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.MOD_NAMES)));
            mIntent.putExtra("_ID", mID);
           // mIntent.putExtra("RESULT", resModifCodes);
           // mIntent.putExtra("",""); передать выбранные модификаторы

           startActivityForResult(mIntent, ROW_MODIFICATORS);
        }
    }

    public void openKeyPanelForQTY(int position){

        if(ordPrinted==1){
            mCallback.onStatusChanged("Счет был напечатан!");
            return;
        }

        Cursor c = (Cursor) adapter.getItem(position);
        if (c != null) {
            Intent digIntent = new Intent(Config.context, InputDigitsForm.class);
            digIntent.putExtra("input_value", c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.GOODS_NAME)));
            digIntent.putExtra(MetaDatabase.OrdersRows.GOODS_CODE, c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.GOODS_CODE)));
            digIntent.putExtra(MetaDatabase.OrdersRows.PRICE, c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.PRICE)));
            digIntent.putExtra(MetaDatabase.OrdersRows.QTY, c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.QTY)));
            digIntent.putExtra(MetaDatabase.OrdersRows.QTY_PRINTED, c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.QTY_PRINTED)));
            digIntent.putExtra(MetaDatabase.OrdersRows.PRINTER_CODE, c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.PRINTER_CODE)));
            digIntent.putExtra(MetaDatabase.OrdersRows.BAZED, c.getString(c.getColumnIndex(MetaDatabase.OrdersRows.BAZED)));
            digIntent.putExtra(MetaDatabase.OrdersRows._ID, adapter.getItemId(position));
            digIntent.putExtra("req", OrderWorkActivity.REQ_ORDER_ROW);

            getActivity().startActivityForResult(digIntent, OrderWorkActivity.REQ_ORDER_ROW);
        }
    }


    private void showOrderActions(){
        CharSequence[] menuItems={getString(R.string.act_order_pay),getString(R.string.act_order_delete),"Cancel"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.act_choose));
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if(item==0){ //pay
                    //SHOW PAY FORM
                    Intent intent = new Intent(getActivity(),PodborListForm.class);
                    intent.putExtra(PodborListForm.EXTRA_TBL, MetaDatabase.Vidoplat.TABLE_NAME);
                    startActivityForResult(intent, ORDER_PAY);

                }else if(item==1){//delete
                    //TODO SHOW QESTION TO DELETE
                    if(Config.glUserUUID.equals("-1") || Config.glUserUUID.equals("-777")) {
                        //DBConnector.instance(Config.context).deleteOrder(orderID);
                        DBConnector.instance(Config.context).tryingToDeleteOrder(orderID);
                        Exporter exporter = new Exporter(null, 0);
                        exporter.startExport();
                        getActivity().finish();
                    }else{
                        mCallback.onStatusChanged("Нет прав на удаление!");
                        dialog.cancel();
                    }
                }else if(item==2){
                    dialog.cancel();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showLongClickRowMenu(final int mPos, final long mId){
        if(ordPrinted==1){
            mCallback.onStatusChanged("Счет был напечатан!");
            return;
        }
        CharSequence[] menuItems={getString(R.string.act_delete),getString(R.string.act_modif),"Cancel"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.act_choose));
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if(item==0){ //delete
                    Intent intent=new Intent();
                    intent.putExtra(MetaDatabase.OrdersRows._ID,mId);
                    doRowActions(ROW_DELETE,intent);
                }else if(item==1){//modif
                    //reCalculateRow(mPos,"0","");
                    //dialog.cancel();
                    openModificators(mPos,mId);
                }else if(item==2){
                    dialog.cancel();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


        public  void calculateRow(Intent data){
            BigDecimal qty=new BigDecimal(data.getStringExtra(MetaDatabase.OrdersRows.QTY));
            BigDecimal price=new BigDecimal(data.getStringExtra(MetaDatabase.OrdersRows.PRICE));
            BigDecimal sum=qty.multiply(price);
            sum=sum.setScale(2,BigDecimal.ROUND_CEILING);
            data.putExtra(MetaDatabase.OrdersRows.SUM,sum.toString());
            //TODO DISKOUNT
            data.putExtra(MetaDatabase.OrdersRows.DISCOUNT,"0");
        }

        public  String calculateTotal(Cursor cursor){
            if(cursor==null)
                return null;
            BigDecimal totalSum=new BigDecimal("0");
            int colSumIndex=cursor.getColumnIndex(MetaDatabase.OrdersRows.SUM);
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
              String strSum=(cursor.getString(colSumIndex));
              if (strSum==null || strSum.isEmpty())
                  strSum="0";
              BigDecimal curSum=new BigDecimal(strSum);
                totalSum=totalSum.add(curSum);
            }
            totalSum=totalSum.setScale(2,BigDecimal.ROUND_CEILING);
            return totalSum.toString();
        }


        public DaoPrinter getPrinterByCode(String printerCode){
            for (DaoPrinter curPrinter:Config.glPrinters) {
                if (curPrinter.getCode().equals(printerCode)) {
                    return curPrinter;
                }
            }
            return null;
        }



        public void prepareForPrintOrder(Cursor cursor,long mOrderID, String mTotal) {
           if (cursor == null)
             return;

           if (!NetUtils.isNetworkAvailable()) {
              //Toast.makeText(getActivity(), "Сеть недоступна - проверьте !", Toast.LENGTH_SHORT).show();
               mCallback.onStatusChanged("Сеть недоступна !");
               return;
           }

            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Config.context);

            final Handler errorHandler=new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 666) {
                        DaoPrinter printerWithError= (DaoPrinter) msg.obj;
                        mCallback.onStatusChanged("Ошибка принтера "+printerWithError.getNpname());
                        //printerCodesWithError.add(printerWithError.getCode());
                    }else if(msg.what==777){
                        mCallback.onStatusChanged("Счет отправлен на принтер");
                        if(ordPrinted==0) {
                            DBConnector.instance(Config.context).setOrderPrinted(orderID);
                            ordPrinted = 1;
                        }
                        //DaoPrinter printerNoError= (DaoPrinter) msg.obj;
                        //printerCodesNOError.add(printerNoError.getCode());
                        if(sharedPref.getBoolean("pref_main_do_after_order",false)){
                            if(sharedPref.getBoolean("pref_main_after_order_avtoriz",false)){//avtoriz
                                Intent intent = new Intent(getActivity(), OficiantMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }else{ //to order list
                                getActivity().finish();
                            }

                        }
                    }
                }
            };



            int colQty=cursor.getColumnIndex(MetaDatabase.OrdersRows.QTY);
            int colPrice=cursor.getColumnIndex(MetaDatabase.OrdersRows.PRICE);
            int colSum=cursor.getColumnIndex(MetaDatabase.OrdersRows.SUM);
            int colGood=cursor.getColumnIndex(MetaDatabase.OrdersRows.GOODS_NAME);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            String formattedIdData=String.valueOf(orderID)+" от "+dateFormat.format(date)+"г.";
            String prinJob= PosPrinter.formatShapkaOrder(formattedIdData, ordUserName);

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String strQTY = cursor.getString(colQty);
                String strPrice= cursor.getString(colPrice);
                String strGood = cursor.getString(colGood);
                String strSum = cursor.getString(colSum);
                prinJob=prinJob+PosPrinter.formatOneLineOrder(strGood,strQTY,strPrice,strSum);
            }
            prinJob=prinJob+PosPrinter.formatPodvalOrder(mTotal,"","",sharedPref.getString("pref_print_order_footer",""));
            prinJob=prinJob +PosPrinter.CUT;

            try {
                String defPrinterCode=sharedPref.getString("pref_def_printer_code","");
                DaoPrinter defPrinterDao=getPrinterByCode(defPrinterCode);
                if(defPrinterDao==null){
                    if(!Config.glPrinters.isEmpty()){
                        defPrinterDao=Config.glPrinters.get(0);
                    }
                }

                PosPrinter.startPrint(defPrinterDao,prinJob,errorHandler);
                Thread.sleep(20);
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }


        @SuppressWarnings("unchecked")
        public void prepareForPrintVstrecka(Cursor cursor, final long mOrderID)  {
            if(cursor==null)
                return ;

            if(!NetUtils.isNetworkAvailable()){
               // Toast.makeText(getActivity(),"Сеть недоступна - проверьте !",Toast.LENGTH_SHORT).show();
                mCallback.onStatusChanged("Сеть недоступна !");
                return;
            }

            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Config.context);
            int BelQty=0;

            BelQty= Integer.parseInt(sharedPref.getString("bell_qty_after_vstrecka", "0"));



            //для обновления базы данных после печати - String - код принтера  Object = HashMap(id,new QTY)
            final HashMap<String,Object> mapPrintersRowUpdate=new HashMap<String, Object>();

            final HashMap<String,String> mapPrinterJobs=new HashMap<String, String>();



            int colQty=cursor.getColumnIndex(MetaDatabase.OrdersRows.QTY);
            int colQtyPrinted=cursor.getColumnIndex(MetaDatabase.OrdersRows.QTY_PRINTED);
            int colGood=cursor.getColumnIndex(MetaDatabase.OrdersRows.GOODS_NAME);
            int colID=cursor.getColumnIndex(MetaDatabase.OrdersRows._ID);
            int colPrinter=cursor.getColumnIndex(MetaDatabase.OrdersRows.PRINTER_CODE);
            int colModif=cursor.getColumnIndex(MetaDatabase.OrdersRows.MOD_NAMES);

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String strQTY=cursor.getString(colQty);
                String strQTYPrinted=cursor.getString(colQtyPrinted);
                String strGood=cursor.getString(colGood);
                String strPrinter=cursor.getString(colPrinter);
                String strModif=cursor.getString(colModif);

                if(strQTY.equals(strQTYPrinted))
                    continue;

                long rowID=cursor.getLong(colID);
                BigDecimal curQTY=new BigDecimal(strQTY);
                BigDecimal curQTYPrinted=new BigDecimal(strQTYPrinted);
                BigDecimal needPrintQty=curQTY.subtract(curQTYPrinted);


                if(!mapPrinterJobs.containsKey(strPrinter)){
                    String prinJob= PosPrinter.formatShapkaVstrechka(String.valueOf(orderID),ordUserName,ordZalName,ordStol);

                    if(needPrintQty.compareTo(BigDecimal.ZERO)<0){
                        prinJob=prinJob+PosPrinter.formatOneLineVstrechkaCanceled(strGood, needPrintQty.toString());
                    }else{
                        prinJob=prinJob+PosPrinter.formatOneLineVstrechka(strGood,needPrintQty.toString(),strModif);
                    }


                    mapPrinterJobs.put(strPrinter,prinJob);

                    HashMap<Long,String> tmpMapRowUpdate=new HashMap<Long, String>();
                    tmpMapRowUpdate.put(rowID,strQTY);
                    mapPrintersRowUpdate.put(strPrinter,tmpMapRowUpdate);

                }else{
                    String prinJob=mapPrinterJobs.get(strPrinter);
                    if(needPrintQty.compareTo(BigDecimal.ZERO)<0){
                        prinJob=prinJob+PosPrinter.formatOneLineVstrechkaCanceled(strGood, needPrintQty.toString());
                    }else{
                        prinJob=prinJob+PosPrinter.formatOneLineVstrechka(strGood,needPrintQty.toString(),strModif);
                    }


                    mapPrinterJobs.put(strPrinter,prinJob);

                    HashMap<Long,String> tmpMapRowUpdate= (HashMap<Long, String>) mapPrintersRowUpdate.get(strPrinter);
                    tmpMapRowUpdate.put(rowID,strQTY);
                    mapPrintersRowUpdate.put(strPrinter,tmpMapRowUpdate);
                }

                //mapRowUpdate.put(rowID,strQTY); //for update new value after printed
            }

            if(mapPrinterJobs.isEmpty()){
                mCallback.onStatusChanged("Печатать нечего");
                return;//no print rows
            }


            //для сбора принтеров с ошибками - не напечатанные задания
          //  printerCodesWithError.clear();
            //printerCodesNOError.clear();

            final int totaljobs=mapPrinterJobs.size();
            final int[] okJobs = {0};
            final int[] errJobs = {0};

            final Handler errorHandler=new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 666) {
                      DaoPrinter printerWithError= (DaoPrinter) msg.obj;
                       mCallback.onStatusChanged("Ошибка принтера "+printerWithError.getNpname());
                        //printerCodesWithError.add(printerWithError.getCode());
                        //Log.d("INCOME MESS ERR",printerWithError.getCode());
                       // addToArrayList(printerCodesWithError,printerWithError.getCode());
                        //Toast.makeText(Config.context,"Ошибка принтера "+printerWithError.getCode(),Toast.LENGTH_SHORT).show();
                        errJobs[0]++;
                       // if(totaljobs==okJobs[0]+errJobs[0]){
                       //     getActivity().finish();
                       // }
                    }else if(msg.what==777){
                        DaoPrinter printerNoError= (DaoPrinter) msg.obj;
                        DBConnector.instance(Config.context).updateRowsOrderAfterPrint((HashMap<Long, String>) mapPrintersRowUpdate.get(printerNoError.getCode()),mOrderID);
                        setCursors();
                        okJobs[0]++;
                        if(totaljobs==okJobs[0]){
                            getActivity().finish();
                        }
                        //printerCodesNOError.add(printerNoError.getCode());
                       // addToArrayList(printerCodesNOError,printerNoError.getCode());
                       // Log.d("INCOME MESS OK",printerNoError.getCode());
                    }

                   // Log.d("INCOME MESS",String.valueOf(msg.what));
                }
            };
            mCallback.onStatusChanged("Начало печати");
            //отправка заданий на принтера
            for (HashMap.Entry<String, String> entry : mapPrinterJobs.entrySet()) {
                String printerCode = entry.getKey();
                String job=entry.getValue()+PosPrinter.getLineSplitter()+PosPrinter.CUT;

                if(BelQty>0){
                    for (int i=0;i<BelQty;i++){
                        job=job+PosPrinter.BELL;
                    }
                }
                //Log.d("Printer code",printerCode);

                for (DaoPrinter curPrinter:Config.glPrinters){
                    //Log.d("Printer CICLE code",curPrinter.getCode());
                    if(curPrinter.getCode().equals(printerCode)){
                        try {
                            //Log.d("START PRINT",printerCode);
                            PosPrinter.startPrint(curPrinter,job,errorHandler);
                           // DBConnector.instance(Config.context).updateRowsOrderAfterPrint((HashMap<Long, String>) mapPrintersRowUpdate.get(printerCode),mOrderID);
                            Thread.sleep(50);
                            Log.d("SLEEP","OK1");
                        }catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }


          /*  while (mapPrinterJobs.size()!=(printerCodesWithError.size()+printerCodesNOError.size())){
                try {
                    Thread.sleep(50);
                    Log.d("SLEEP","OK");
                } catch (InterruptedException e) {
                    Log.d("SLEEP","ERR");
                    e.printStackTrace();
                }
            }*/



            //Обновляем в базе печать
         //   for(String curPrinterCode:printerCodesNOError){
         //       DBConnector.instance(Config.context).updateRowsOrderAfterPrint((HashMap<Long, String>) mapPrintersRowUpdate.get(curPrinterCode),mOrderID);
         //       Log.d("PRINT NO NO ERRROR",curPrinterCode);
        //    }

          //  for(String curPrinterCodeEr:printerCodesWithError){
          //      Log.d("PRINT ERRROR",curPrinterCodeEr);
               // Toast.makeText(Config.context,"ощибка принтера с кодом "+curPrinterCodeEr,Toast.LENGTH_SHORT).show();
          //  }

          //  Log.d("PRINTERS NO ERR SIZE", String.valueOf(printerCodesNOError.size()));
          //  Log.d("PRINTERS ERR SIZE", String.valueOf(printerCodesWithError.size()));
        }



   /* class ReadOrder extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> data = null;
            for (String par : strings) {
                data = Socket1c.writeToSocketAndGet(ServerIP, ServerPort, par);
                // Log.d("FROM_SOK",data.toString());
            }
            //Log.d("FROMSOK",data.toString());
            return data;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            populateAdapter(result);

        }
    }*/
}
