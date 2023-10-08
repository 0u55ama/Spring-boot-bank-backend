package com.bank.system.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account!";

    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Your account is created successfully!";

    public static final String ACCOUNT_DOES_NOT_EXISTS_CODE = "003";
    public static final String ACCOUNT_DOES_NOT_EXISTS_MESSAGE = "Your account does not exists!";

    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "Account found!";

    public static final String ACCOUNT_SUCCESSFULLY_CREDITED_CODE = "005";
    public static final String ACCOUNT_SUCCESSFULLY_CREDITED_MESSAGE = "Account successfully credited!";

    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance!";
    public static final String ACCOUNT_SUCCESSFULLY_DEBITED_CODE = "007";
    public static final String ACCOUNT_SUCCESSFULLY_DEBITED_MESSAGE = "Account successfully debited!";

    public static final String SOURCE_ACCOUNT_DOES_NOT_EXISTS_CODE = "008";
    public static final String SOURCE_ACCOUNT_DOES_NOT_EXISTS_MESSAGE = "Source Account does not exists!";
    public static final String RECIPIENT_ACCOUNT_DOES_NOT_EXISTS_CODE = "009";
    public static final String RECIPIENT_ACCOUNT_DOES_NOT_EXISTS_MESSAGE = "Recipient Account does not exists!";
    public static final String SUCCESSFUL_TRANSFER_OPERATION_CODE = "010";
    public static final String SUCCESSFUL_TRANSFER_OPERATION_MESSAGE = "Successful transfer operation!";


    public static String generateAccountNumber(){
        /**
         * 2023 + randomSixDigits
         */
        Year currentYear = Year.now();
        int randomDigits = (int) Math.floor(Math.random() * (999_999 - 100_000 + 1) + 100_000);

        String year = String.valueOf(currentYear);
        String randomSixDigits = String.valueOf(randomDigits);

        return year + randomSixDigits;
    }
}
