package ua.yalta.oficiant;

import ua.yalta.oficiant.refs.DaoPrinter;
import ua.yalta.oficiant.refs.DaoUsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 29.10.12
 * Time: 14:16
 * To change this template use File | Settings | File Templates.
 */
public final class Config {
    public static OficiantMain context = null;
    public static String glUserName = null;
    public static String glUserUUID = null;
    public static String glRoleUUID = "SuperAdmin"; //текущая роль
    /**
     * keys - class RoleValues public String-имя параметра текущей роли
     * val - значение параметра текущей роли
     */
    public static Map<String, Boolean> glRoleValues = new HashMap<String, Boolean>();
    public static List<DaoPrinter> glPrinters= new ArrayList<DaoPrinter>();
    public static List<DaoUsers> glUsers= new ArrayList<DaoUsers>();
}
