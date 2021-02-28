package com.sh.onlinehighschool.utils;

import java.util.regex.Pattern;

//Bắt điều kiện nhập form đăng nhập
public class InputHelper {

    public static String validEmail(String email){
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        if (email == null)
            return "Không được bỏ trống trường này";
        else if (!pattern.matcher(email).matches())
            return "Định dạng email không đúng";
        else return null;
    }

    public static String validPassword(String password){
        if (password == null){
            return "Không được bỏ trống trường này";
        } else {
            if (password.length() < 6 | password.length() >32)
                return "Mật khẩu phải có độ dài từ 6-32 kí tự";
            else return null;
        }
    }

    public static String validName(String name){
        if (name == null){
            return "Không được bỏ trống trường này";
        } else {
            if (name.length() < 3)
                return "Tên phải chứa tối thiểu 3 kí tự";
            else return null;
        }
    }

    public static String emptyData(int data, String value){
        if (data == 0){
            return "Bạn chưa nhập " + value;
        }
        return null;
    }

    public static String emptyData(String data, String value){
        if (data == null || data.equals("")){
            return "Bạn chưa nhập " + value;
        }
        return null;
    }

    public static String getCharAvatar(String s){
        if (s != null){
            String[] words = s.split("\\s");
            return String.valueOf(words[words.length - 1].toUpperCase().charAt(0));
        }
        return "";
    }

}
