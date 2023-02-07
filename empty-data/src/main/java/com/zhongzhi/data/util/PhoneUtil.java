package com.zhongzhi.data.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class PhoneUtil {

    public static final long BASE = 100_0000_0000L;
    public static final long MAX = 199_9999_9999L;

    private PhoneUtil() {
    }

    public static boolean isValidPhone(String phone) {
        return toPhone(phone) != null;
    }

    public static Long toPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }
        phone = remove86(phone);
        phone = phone.trim();
        if (phone.length() != 11) {
            return null;
        }

        long num;
        try {
            num = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            return null;
        }
        if (num < BASE || num > MAX) {
            return null;
        }
        if (checkPhone(String.valueOf(num))) {
            return num;
        } else {
            return null;
        }
    }

    public static boolean checkPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$");
        return pattern.matcher(phone.trim()).matches();
    }

    public static String remove86(String phone) {
        Pattern p2 = Pattern.compile("^((\\+{0,1}0{0,2}86){0,1}-{0,1})");
        Matcher m2 = p2.matcher(phone.trim());
        StringBuffer sb = new StringBuffer();
        while (m2.find()) {
            m2.appendReplacement(sb, "");
        }
        m2.appendTail(sb);
        return sb.toString();
    }
}
