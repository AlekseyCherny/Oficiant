package ua.yalta.oficiant.remote;

/**
 * Created with IntelliJ IDEA.
 * User: Босс
 * Date: 12.12.12
 * Time: 9:23
 * Сответствия параметра роли с удаленной системой
 * Значения текущей роли грузятся в класс Config - glRoleValues, где ключом будет
 * статическая строка из этого класса, а значением-значение для текущей роли
 */
public final class RoleValues {
    public static final String OPEN_ALL_TABLES = "ОткрыватьВсеСтолы";
    public static final String EDIT_ORDER_AFTER_PREORDER = "ИзменятьЗаказПослеПредчека";
}
