package com.revature.services;


import com.revature.dtos.CreatePaymentRequest;
import com.revature.dtos.EditPaymentRequest;
import com.revature.dtos.PaymentResponse;
import com.revature.exceptions.InvalidUserInputException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Payment;
import com.revature.models.User;
import com.revature.repositories.PaymentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTestSuite {
    private static PaymentService paymentService;
    private static UserService userService;
    private static PaymentRepository paymentRepository;

    public static User dummyUser = new User(1, "email", "pass", "first", "last", true, true, "token");

    public static User dummyUser2 = new User(2, "email2", "pass2", "first2", "last2", true, true, "token2");

    @BeforeAll
    static void setup() {
        paymentRepository = mock(PaymentRepository.class);
        userService = mock(UserService.class);

        paymentService = new PaymentService(paymentRepository, userService);
    }

    @Test
    void test_createPayment_returnPaymentResponse_givenValidInput() {
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setCcv("111");
        createPaymentRequest.setExpDate(new Date(System.currentTimeMillis()));
        createPaymentRequest.setCardNumber("1111111111");
        User user = new User();
        PaymentResponse paymentResponse = paymentService.createPayment(createPaymentRequest, user);
        Assertions.assertInstanceOf(PaymentResponse.class, paymentResponse);
    }

    @Test
    void test_createPayment_throwException_givenInvalidInput() {
        CreatePaymentRequest createPaymentRequest1 = new CreatePaymentRequest();
        createPaymentRequest1.setCcv("111");;
        createPaymentRequest1.setCardNumber("1111111111");

        CreatePaymentRequest createPaymentRequest2 = new CreatePaymentRequest();
        createPaymentRequest2.setCcv("111");
        createPaymentRequest2.setExpDate(new Date(System.currentTimeMillis()));

        CreatePaymentRequest createPaymentRequest3 = new CreatePaymentRequest();
        createPaymentRequest3.setCardNumber("111111111111");
        createPaymentRequest3.setExpDate(new Date(System.currentTimeMillis()));

        User user = new User();
        Assertions.assertThrows(InvalidUserInputException.class, () -> paymentService.createPayment(createPaymentRequest1, user));
        Assertions.assertThrows(InvalidUserInputException.class, () -> paymentService.createPayment(createPaymentRequest2, user));
        Assertions.assertThrows(InvalidUserInputException.class, () -> paymentService.createPayment(createPaymentRequest3, user));
    }


    @Test
    void testFindPaymentById(){
        Payment test = new Payment();
        when(paymentRepository.findById("1")).thenReturn(Optional.of(test));
        paymentService.findPaymentById("1");
        verify(paymentRepository, times(1)).findById("1");

    }

    @Test
    void testUpdatePayment(){
        when(paymentRepository.findById(anyString())).thenReturn(Optional.of(new Payment("2", "a", "b", new Date(1), dummyUser)));
        try{
            paymentService.updatePayment(new EditPaymentRequest(), dummyUser2);
            fail("Exception should have been throw");
        } catch(ResourceNotFoundException e){
            Assertions.assertNotNull(e);
        }
    }

    @Test
    void updatePayment_ExceptionThrown(){
        when(paymentRepository.findById(anyString())).thenReturn(Optional.of(new Payment("2", "a", "b", new Date(1), dummyUser)));
        try{
            paymentService.updatePayment(new EditPaymentRequest(), dummyUser2);
            fail("Exception should have been throw");
        } catch(ResourceNotFoundException e){
            Assertions.assertNotNull(e);
        }
    }

    @Test
    void testDeletePayment(){
        when(paymentRepository.findById(anyString())).thenReturn(Optional.of(new Payment("9", "a", "b", new Date(1), dummyUser)));
        paymentService.deletePayment("cheese", dummyUser);
        verify(paymentRepository, times(1)).delete(any(Payment.class));
    }

    @Test
    void testFindAllByUser(){
        List<Payment> pList = new ArrayList<>();
        when(paymentRepository.findCardsByUser(anyInt())).thenReturn(Optional.of(pList));
        List<PaymentResponse> rList = paymentService.findAllByUser(4);
        verify(paymentRepository, times(1)).findCardsByUser(anyInt());
        Assertions.assertNotNull(rList);
    }

}
