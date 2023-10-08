package com.bank.system.service.impl;

import com.bank.system.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
