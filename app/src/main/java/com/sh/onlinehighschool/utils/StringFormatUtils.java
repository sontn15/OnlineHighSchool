package com.sh.onlinehighschool.utils;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class StringFormatUtils {
    public static String convertToStringMoneyVND(long money) {
        NumberFormat formatter = new DecimalFormat("#,###");
        return formatter.format(money) + " đ";
    }

    public static String getCurrentDateStr() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
        return date;
    }

    public static String getCurrentDateNotTimeStr() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("ddMMyyyy").format(calendar.getTime());
        return date;
    }

    public static String getCurrentDateStrFull() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(calendar.getTime());
        return date;
    }

    public static String getTextCustomerNameForTextView(String input) {
        String result;
        input = input.trim();
        String[] arr = input.split(" ");
        if (arr.length > 1) {
            result = (arr[0].trim().charAt(0) + "") + (arr[arr.length - 1].trim().charAt(0) + "");
        } else {
            result = (input.charAt(0) + "") + (input.charAt(input.length() - 1) + "");
        }
        return result.toUpperCase();
    }


    public static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    public static String getTextFromSoiCauMbStringToView(String input) {
        StringBuilder response = new StringBuilder();
        List<String> listData = Arrays.asList(input.split(","));
        for (int i = 1; i <= listData.size(); i++) {
            response.append(listData.get(i - 1)).append(",");
            if (i % 9 == 0 && i != listData.size()) {
                response.append("\n");
            }
        }
        return response.substring(0, response.length() - 1);
    }

    public static String convertUTF8ToString(String value) {
        try {
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replace("Đ", "D")
                    .replace("đ", "d").toLowerCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
