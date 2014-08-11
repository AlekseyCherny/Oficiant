package ua.yalta.oficiant;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import ua.yalta.oficiant.db.MetaDatabase;
import ua.yalta.oficiant.forms.PodborListForm;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 05.11.12
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
public class Prefs extends PreferenceActivity {
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  getFragmentManager().beginTransaction()
      //          .replace(android.R.id.content, new PrefsFragment())
      //          .commit();

    }*/

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String settings = getArguments().getString("identKey");
            if ("main".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_main);
            } else if ("print".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_print_order);
            }else if ("server".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_server);
            }//else if ("defs".equals(settings)) {
             //   addPreferencesFromResource(R.xml.settings_server);
            //}

        }

    }

    public static class DbSettingsFragment extends PreferenceFragment {
        private SharedPreferences mPreferences;
        static final int CHOOSE_PRINTER = 10;
        static final int CHOOSE_ZAL = 11;
        Preference defPrinter;
        Preference defZal;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_def_db);
            mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            defPrinter=findPreference(getString(R.string.pref_def_printer));
            if (defPrinter != null) {
                defPrinter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        chosePrinter();
                        return true;
                    }
                });
            }
            defZal=findPreference(getString(R.string.pref_def_zal));
            if (defZal != null) {
                defZal.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        choseZal();
                        return true;
                    }
                });
            }

        }



        private void chosePrinter() {
            Intent intent = new Intent(getActivity(),PodborListForm.class);
            intent.putExtra(PodborListForm.EXTRA_TBL, MetaDatabase.Printers.TABLE_NAME);
            //intent.putExtra("forpodbor", 1);
            startActivityForResult(intent, CHOOSE_PRINTER);
        }
        private void choseZal() {
            Intent intent = new Intent(getActivity(),PodborListForm.class);
            intent.putExtra(PodborListForm.EXTRA_TBL, MetaDatabase.Zals.TABLE_NAME);
            //intent.putExtra("forpodbor", 1);
            startActivityForResult(intent, CHOOSE_ZAL);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case CHOOSE_PRINTER:
                    if (data != null) {
                        setPrinter(data.getStringExtra("oName"), data.getStringExtra("oCode"));

                    }
                    break;
                case CHOOSE_ZAL:
                    if (data != null) {
                        setZal(data.getStringExtra("oName"), data.getStringExtra("oCode"));

                    }
                    break;
            }
        }

        private void setPrinter(String name,String code) {
            SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putString ("pref_def_printer_code", code);
            editor.putString (getString(R.string.pref_def_printer), name);
            editor.commit();

            defPrinter.setSummary(name);

        }

        private void setZal(String name,String code) {
            SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putString ("pref_def_zal_code", code);
            editor.putString (getString(R.string.pref_def_zal), name);
            editor.commit();

            defZal.setSummary(name);

        }

        @Override
        public void onResume() {
            super.onResume();
            defPrinter.setSummary( mPreferences.getString(getString(R.string.pref_def_printer),""));
            defZal.setSummary( mPreferences.getString(getString(R.string.pref_def_zal),""));
        }
    }



}
