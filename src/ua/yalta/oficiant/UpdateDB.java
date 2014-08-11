package ua.yalta.oficiant;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import ua.yalta.oficiant.db.DBConnector;
import ua.yalta.oficiant.net.Socket1c;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 01.11.12
 * Time: 9:00
 * To change this template use File | Settings | File Templates.
 */
public class UpdateDB extends Activity {
    public static final int TASK_GRP = 1;
    public static final int TASK_GOODS = 2;
    public static final int TASK_USERS = 3;
    public static final int TASK_ZALS = 4;
    int curentTask;
    TextView resUpdText;
    String ServerIP;
    String ServerPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.updatedb);
        curentTask = 0;
        resUpdText = (TextView) findViewById(R.id.updResultTxt);
    }

    public void readPrefs() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ServerIP = sharedPref.getString("serverIP", "");
        ServerPort = sharedPref.getString("serverPort", "");
    }

    ////ЗАПУСК ЗДЕСЬ
    public void UpdateBase(View v) {
        TaskUpdateGroups();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void TaskUpdateGroups() {
        curentTask = TASK_GRP;
        UpdateTask myTask = new UpdateTask();
        myTask.execute("grp");
    }

    public void TaskUpdateGoods() {
        curentTask = TASK_GOODS;
        UpdateTask myTask = new UpdateTask();
        myTask.execute("goods");
    }

    public void TaskUpdateUsers() {
        curentTask = TASK_USERS;
        UpdateTask myTask = new UpdateTask();
        myTask.execute("users");
    }

    public void TaskUpdateZals() {
        curentTask = TASK_ZALS;
        UpdateTask myTask = new UpdateTask();
        myTask.execute("zals");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void updateGroups(List<String> listGrp) {
        boolean grpUpdated = DBConnector.instance(this).updateGroupsTrans(listGrp);
        if (grpUpdated) {
            resUpdText.setText("Группы успешно обновлены");
        } else {
            resUpdText.setText("Неудачная попытка обновления групп");
        }
        curentTask = 0;
        TaskUpdateGoods();
    }

    public void updateGoods(List<String> listGrp) {
        boolean grpUpdated = DBConnector.instance(this).updateGoodsTrans(listGrp);
        if (grpUpdated) {
            resUpdText.setText("Товары успешно обновлены");
        } else {
            resUpdText.setText("Неудачная попытка обновления товаров");
        }
        curentTask = 0;
        TaskUpdateUsers();
    }

    public void updateUsers(List<String> listGrp) {
        boolean grpUpdated = DBConnector.instance(this).updateUsersTrans(listGrp);
        if (grpUpdated) {
            resUpdText.setText("Users успешно обновлены");
        } else {
            resUpdText.setText("Неудачная попытка обновления Users");
        }
        curentTask = 0;
        TaskUpdateZals();
    }

    public void updateZals(List<String> listGrp) {
        boolean grpUpdated = DBConnector.instance(this).updateZalsTrans(listGrp);
        if (grpUpdated) {
            resUpdText.setText("Данные успешно обновлены");
        } else {
            resUpdText.setText("Неудачная попытка обновления данных");
        }
        curentTask = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        readPrefs();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    class UpdateTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resUpdText.setText("Начало обновления...");
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> data = null;
            for (String par : strings) {
                data = Socket1c.writeToSocketAndGet(ServerIP, ServerPort, par);
            }
            return data;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            if (curentTask == TASK_GRP) {
                updateGroups(result);
            } else if (curentTask == TASK_GOODS) {
                updateGoods(result);
            } else if (curentTask == TASK_USERS) {
                updateUsers(result);
            } else if (curentTask == TASK_ZALS) {
                updateZals(result);
            }
        }
    }
}
