package com.bank.system.service.impl.impl;

import com.bank.system.config.JwtTokenProvider;
import com.bank.system.dto.*;
import com.bank.system.entity.Role;
import com.bank.system.entity.User;
import com.bank.system.repository.UserRepository;
import com.bank.system.service.impl.TransactionService;
import com.bank.system.service.impl.UserService;
import com.bank.system.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Creating an account is saving a new user information into the db
         * check if user is already has an account
         */

        if (userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .otherPhoneNumber(userRequest.getOtherPhoneNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();

        User savedUser = userRepository.save(newUser);
        //send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulation! your bank account has been successfully created" +
                        "\nYour Account Details : " +
                        "\nAccount Name : " + savedUser.getFirstName() + savedUser.getLastName() +
                        "\nAccount Number : " + savedUser.getAccountNumber() +
                        "\nAccount Balance : " + savedUser.getAccountBalance() +
                        "\nAccount Status : " + savedUser.getStatus())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(
                                savedUser.getFirstName()+ " "
                                        +savedUser.getLastName()+ " "
                                                +savedUser.getOtherName())
                        .build())
                .build();

    }
    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        EmailDetails loginAlert  = EmailDetails.builder()
                .subject("You're logged in!")
                .recipient(loginDto.getEmail())
                .messageBody("You have logged in to your account. If you did not initiate this request, please contact you bank")
                .build();
        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("555")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        // check if the user account number exists in the db
        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }
    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExists){
            return AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();

    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditRequest) {
        //checking if the account exists
        boolean isAccountExists = userRepository.existsByAccountNumber(creditRequest.getAccountNumber());
        if (!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(creditRequest.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditRequest.getAmount()));
        userRepository.save(userToCredit);

        //save a credit transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(creditRequest.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return  BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_SUCCESSFULLY_CREDITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_SUCCESSFULLY_CREDITED_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                                .accountNumber(userToCredit.getAccountNumber())
                                .accountBalance(userToCredit.getAccountBalance() )
                                .build()
                )
                .build();

    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest debitRequest) {
        //checking if the account exists
        boolean isAccountExists = userRepository.existsByAccountNumber(debitRequest.getAccountNumber());
        if (!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(debitRequest.getAccountNumber());
        boolean canDebit = userToDebit.getAccountBalance().compareTo(debitRequest.getAmount()) > 0;

        if (!canDebit) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(
                            AccountInfo.builder()
                                    .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                                    .accountNumber(userToDebit.getAccountNumber())
                                    .accountBalance(userToDebit.getAccountBalance())
                                    .build()
                    )
                    .build();
        }

        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(debitRequest.getAmount()));
        userRepository.save(userToDebit);

        //save a debit transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToDebit.getAccountNumber())
                .transactionType("DEBIT")
                .amount(debitRequest.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_SUCCESSFULLY_DEBITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_SUCCESSFULLY_DEBITED_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                                .accountNumber(userToDebit.getAccountNumber())
                                .accountBalance(userToDebit.getAccountBalance())
                                .build()
                )
                .build();
    }

    @Override
    public BankResponse transferOperation(TransferRequest transferRequest) {
        boolean isSourceAccountExists = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());

        if (! isSourceAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.SOURCE_ACCOUNT_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.SOURCE_ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        boolean isRecipientAccountExists = userRepository.existsByAccountNumber(transferRequest.getRecipientAccountNumber());

        if (!isRecipientAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.RECIPIENT_ACCOUNT_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.RECIPIENT_ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccount = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());

        boolean canTransfer = sourceAccount.getAccountBalance().compareTo(transferRequest.getTransferredAmount()) > 0;

        if (!canTransfer){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(sourceAccount.getFirstName() + " " + sourceAccount.getLastName())
                            .accountNumber(sourceAccount.getAccountNumber())
                            .accountBalance(sourceAccount.getAccountBalance())
                            .build())
                    .build();
        }
        User recipientAccount = userRepository.findByAccountNumber(transferRequest.getRecipientAccountNumber());

        recipientAccount.setAccountBalance(recipientAccount.getAccountBalance().add(transferRequest.getTransferredAmount()));
        userRepository.save(recipientAccount);
        EmailDetails recipientEmailDetails = EmailDetails.builder()
                .subject("ENQUIRY ALERT!")
                .recipient(recipientAccount.getEmail())
                .messageBody("Your received " + transferRequest.getTransferredAmount() + " from Mr " + sourceAccount.getLastName() + " " + sourceAccount.getLastName() )
                .build();
        emailService.sendEmailAlert(recipientEmailDetails);

        //save a debit transaction
        TransactionDto transactionDtoRecipient = TransactionDto.builder()
                .accountNumber(recipientAccount.getAccountNumber())
                .transactionType("TRANSFER : You received a transfer")
                .amount(transferRequest.getTransferredAmount())
                .build();
        transactionService.saveTransaction(transactionDtoRecipient);

        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(transferRequest.getTransferredAmount()));
        userRepository.save(sourceAccount);
        EmailDetails sourceEmailDetails = EmailDetails.builder()
                .subject("DEBIT ALERT!")
                .recipient(recipientAccount.getEmail())
                .messageBody("The amount " + transferRequest.getTransferredAmount() + " have been sent from your account to Mr " + recipientAccount.getLastName() + " " + recipientAccount.getLastName() )
                .build();

        emailService.sendEmailAlert(sourceEmailDetails);

        //save a debit transaction
        TransactionDto transactionDtoSource = TransactionDto.builder()
                .accountNumber(sourceAccount.getAccountNumber())
                .transactionType("TRANSFER : You made a transfer")
                .amount(transferRequest.getTransferredAmount())
                .build();
        transactionService.saveTransaction(transactionDtoSource);

        return BankResponse.builder()
                .responseCode(AccountUtils.SUCCESSFUL_TRANSFER_OPERATION_CODE)
                .responseMessage(AccountUtils.SUCCESSFUL_TRANSFER_OPERATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(sourceAccount.getFirstName() + " " + sourceAccount.getLastName())
                        .accountNumber(sourceAccount.getAccountNumber())
                        .accountBalance(sourceAccount.getAccountBalance())
                        .build())
                .build();
    }
}
