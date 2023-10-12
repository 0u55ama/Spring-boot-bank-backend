package com.bank.system.service.impl;

import com.bank.system.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount(CreditDebitRequest creditRequest);
    BankResponse debitAccount(CreditDebitRequest debitRequest);
    BankResponse transferOperation(TransferRequest transferRequest);

    BankResponse login(LoginDto loginDto);
}
