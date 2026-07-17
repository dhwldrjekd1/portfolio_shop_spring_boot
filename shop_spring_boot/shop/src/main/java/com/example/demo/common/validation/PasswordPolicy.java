package com.example.demo.common.validation;

import java.util.regex.Pattern;

// 비밀번호 정책: 8자 이상, 영문 소문자/숫자/특수문자 각 최소 1개 포함
public class PasswordPolicy {

    private static final int MIN_LENGTH = 8;
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[^A-Za-z0-9]");

    public static void validate(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        if (!LOWERCASE.matcher(password).find()
                || !DIGIT.matcher(password).find()
                || !SPECIAL.matcher(password).find()) {
            throw new IllegalArgumentException("비밀번호는 영문 소문자, 숫자, 특수문자를 모두 포함해야 합니다.");
        }
    }
}
