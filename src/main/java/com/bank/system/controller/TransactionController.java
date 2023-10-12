package com.bank.system.controller;


import com.bank.system.entity.Transaction;
import com.bank.system.service.impl.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")

public class TransactionController {

    @Autowired
    private BankStatement bankStatement;

    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate)
            throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
}
