package com.bank.system.controller;

import com.bank.system.dto.*;
import com.bank.system.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Account Management APIs")
public class UserController {

    @Autowired
    UserService userService;
    @Operation(
            summary = "Create New User Account",
            description = "Creating a new user account and assigning an account id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 201 CREATED"
    )
    @PostMapping()
    public BankResponse createAccount(@RequestBody UserRequest request){
        return userService.createAccount(request);
    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }
    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number, check how much money on you account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }
    @Operation(
            summary = "Name Enquiry",
            description = "Given an account number, check the name of the account "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @Operation(
            summary = "Credit Operation",
            description = "Given an account number and an amount, add that amount to the account as a credit operation"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }
    @Operation(
            summary = "Debit Operation",
            description = "Given an account number and an amount, withdraw that amount from the account as a debit operation"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }
    @Operation(
            summary = "Transfer Operation",
            description = "Given a source account number and destination account number and an amount, add that amount to the destination account from the source account as a transfer"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @PostMapping("/transfer")
    public BankResponse transferOperation(@RequestBody TransferRequest request){
        return userService.transferOperation(request);
    }
}
