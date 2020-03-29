package com.s1mar.covid19tracker.utils;

public class TextUtils {

    public static boolean isStringEmpty(String token){
        return (token==null || token.trim().isEmpty());
    }

    public static boolean satisfiesLength(int minLength, int maxLength,String token){
        return  !TextUtils.isStringEmpty(token) && token.length()>=minLength && token.length()<=maxLength;

    }
}
