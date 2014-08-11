package ua.yalta.oficiant;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.db.MetaDatabase;
import ua.yalta.oficiant.exchange.DaoUpdater;


public class OficiantMain extends Activity {
    TextView input;
    TextView valueText;
    TextView helpText;
    String mPassword;
    Button btnPoint;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputdigitsform);
        Config.context = this;
        PreferenceManager.setDefaultValues(this, R.xml.settings_server, false);
        DBConnector.instance(this).initDB();

        mPassword = "";
        btnPoint = (Button) findViewById(R.id.btnPoint);
        input = (TextView) findViewById(R.id.inputText);
        input.setText("");
        valueText = (TextView) findViewById(R.id.infoText);
        valueText.setText("АВТОРИЗАЦИЯ");
        helpText = (TextView) findViewById(R.id.helpText);
        helpText.setText("Пароль:");

       // String str2="АБрикос";
       // int codePoint = Character.codePointAt(str2, 0);
       // int hex= Integer.parseInt(Integer.toHexString(codePoint));

       // int indexInArr=findIndex(codePoint);
        //str2.getChar(0);
       // int hexU=Integer.parseInt(String.valueOf(str2.charAt(0)),16);
       // Log.d("CHAR", String.valueOf(indexInArr));
       // Log.d("CHAR", Charset.availableCharsets().toString());
    }

    //Clickers
    public void NumBtnClick(View target) {
        TextView c = (TextView) target;
        CharSequence c2 = c.getText();
        mPassword = mPassword + c2.toString();
        input.setText(String.valueOf(input.getText()) + "*");
    }


    public void CanselBtnClick(View target) {
        finish();
    }


    public void OKBtnClick(View target) {
        String out = mPassword.trim();
        if (out.length() == 0) {
            return;
        }
        if (out.equals("13041970")) {
            initSessionParametersSAdmin();
            Intent intent = new Intent(this, TablesActivity.class);
            startActivity(intent);
        } else {
            Cursor mCur = DBConnector.instance(this).getUserByPass(out);
            if (mCur == null || mCur.getCount() == 0) {
                input.setText("Ошибка авторизации");
                mPassword = "Ошибка авторизации";
                if (mCur != null) {
                    mCur.close();
                }
                return;
            } else { //OK
                initSessionParameters(mCur);
                if(out.equals("13.04.")){
                    Intent intent = new Intent(this, TablesActivity.class);
                    startActivity(intent);
                }else{
                    Intent intentU = new Intent(this, OrdersByUserActivity.class);//OrderWorkActivity.class
                    startActivity(intentU);
                }



            }
        }
 
    }

    public void ClearBtnClick(View target) {
        ClearInput();
    }

    public void ClearInput() {
        input.setText(null);
        mPassword = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPassword = "";
        input.setText("");
        valueText.setText("АВТОРИЗАЦИЯ");
        helpText.setText("Пароль:");

    }

    private void initSessionParameters(Cursor uCursor) {
        uCursor.moveToFirst();
        Config.glUserName = uCursor.getString(uCursor.getColumnIndex(MetaDatabase.Users.NAME));
        Config.glUserUUID = uCursor.getString(uCursor.getColumnIndex(MetaDatabase.Users.CODE));
        //роль перезаполняем если из предыдущего сеанса она не равна роли текущего пользователя
//        if (!Config.glRoleUUID.equals(uCursor.getString(uCursor.getColumnIndex(MetaDatabase.Users.ROLE)))) {
 //           Config.glRoleUUID = uCursor.getString(uCursor.getColumnIndex(MetaDatabase.Users.ROLE));
            //TODO fill Config.glRoleValues
 //       }
        uCursor.close();

        DaoUpdater.updatePrinters();
        DaoUpdater.updateUsers();
    }

    private void initSessionParametersSAdmin() {
        Config.glUserName = "SuperAdmin";
        Config.glUserUUID = "-1";
        Config.glRoleUUID = "SuperAdmin";
        for (String key : Config.glRoleValues.keySet()) {
            Config.glRoleValues.put(key, true);
        }

        DaoUpdater.updatePrinters();
        DaoUpdater.updateUsers();
    }


}
