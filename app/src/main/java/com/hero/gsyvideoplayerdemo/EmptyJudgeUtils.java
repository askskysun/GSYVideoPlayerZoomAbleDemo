package com.hero.gsyvideoplayerdemo;

import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 123456 on 2017/6/8.
 */

public class EmptyJudgeUtils {
  public enum EnumStringReplaceType {
        REPLACETYPE_LINEFREE, REPLACETYPE_NONESTRING, REPLACETYPE_NULL;
      //客户端数据替换类型  /n 替换成 //n    ""   不替换
      //服务端数据替换类型  //n 替换成 /n    ""   不替换
    }
    public static boolean stringIsEmpty(String s) {
        try {
            if (null == s)
                return true;
            if (s.length() == 0)
                return true;
            if (s.trim().length() == 0)
                return true;
            if (s.trim().equals(""))
                return true;
            if (s.trim().equals("null"))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean stringIsEquals(String s1, String s2) {
        try {
            if (null == s1 || null == s2)
                return false;
            if (s1.trim().equals(s2))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean stringIsEquals(String s1, String... sList) {
        try {
            if (null == s1)
                return false;
            if (sList != null && sList.length > 0) {
                for (int i = 0; i < sList.length; i++) {
                    if (s1.trim().equals(sList[i]))
                        return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean stringListSizeMany(List<String> list) {
        if (null == list)
            return true;
        if (list.size() > 0) {
            return false;
        }
        return true;
    }

    public static boolean imageViewListSizeMany(List<ImageView> list) {
        if (null == list)
            return true;
        if (list.size() > 0) {
            return false;
        }
        return true;
    }
    public static String editTextGetString(EditText editText) {
        Editable ethomePhotodesc = editText.getText();
        if (ethomePhotodesc == null) {
          return "";
        }
        if (ethomePhotodesc != null && ethomePhotodesc.length() == 0) {
            return "";
        }
        if (ethomePhotodesc != null && ethomePhotodesc.toString().trim().equals("")) {
            return "";
        }
        return ethomePhotodesc.toString().replace("\n","\\n");
    }

    /**
     *  获取TextView  内容
     */
    public static String textViewGetString(TextView textView , EnumStringReplaceType replaceType , boolean isTrim) {
        CharSequence text = textView.getText();
        if (text == null) {
            return "";
        }
        if (text != null && text.length() == 0) {
            return "";
        }
        if (text != null && text.toString().trim().equals("")) {
            return "";
        }
        if (EnumStringReplaceType.REPLACETYPE_LINEFREE.equals(replaceType)) {
            if (isTrim) {
                return text.toString().trim().replace("\n","\\n");
            }else {
                return text.toString().replace("\n","\\n");
            }
        }else if (EnumStringReplaceType.REPLACETYPE_NONESTRING.equals(replaceType)){
            if (isTrim) {
                return text.toString().trim().replace("\n","");
            }else {
                return text.toString().replace("\n","");
            }
        }else {
            if (isTrim) {
                return text.toString().trim();
            }else {
                return text.toString();
            }
        }
    }
    /**
     *  获取EditText 内容
     */
    public static String editTextGetString(EditText editText , EnumStringReplaceType replaceType , boolean isTrim) {
        Editable editable = editText.getText();

        if (editable == null) {
            return "";
        }
        if (editable != null && editable.length() == 0) {
            return "";
        }
        if (editable != null && editable.toString().trim().equals("")) {
            return "";
        }
        if (EnumStringReplaceType.REPLACETYPE_LINEFREE.equals(replaceType)) {
            if (isTrim) {
                return editable.toString().trim().replace("\n","\\n");
            }else {
                return editable.toString().replace("\n","\\n");
            }
        }else if (EnumStringReplaceType.REPLACETYPE_NONESTRING.equals(replaceType)){
            if (isTrim) {
                return editable.toString().trim().replace("\n","");
            }else {
                return editable.toString().replace("\n","");
            }
        }else {
            if (isTrim) {
                return editable.toString().trim();
            }else {
                return editable.toString();
            }
        }
    }
    public static String editTextGetStringFill(EditText editText) {
        Editable ethomePhotodesc = editText.getText();
        if (ethomePhotodesc == null) {
            return "";
        }
        if (ethomePhotodesc != null && ethomePhotodesc.length() == 0) {
            return "";
        }
        if (ethomePhotodesc != null && ethomePhotodesc.toString().trim().equals("")) {
            return "";
        }
        return ethomePhotodesc.toString().trim().replace("\n","");
    }
    /**
     * 字符串的值  与int是否相等
     * @param str
     * @param num
     * @return
     */
    public static boolean getStringIs(String str , int num) {
        if (EmptyJudgeUtils.stringIsEmpty(str)) {
            return false;
        }else {
            try {
                return Integer.valueOf(str) == num;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}
