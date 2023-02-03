package com.revature.services;

import com.revature.dtos.CreateOrderRequest;
import com.revature.dtos.EditOrderRequest;
import com.revature.dtos.OrderResponse;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.exceptions.UnauthorizedException;
import com.revature.models.Order;
import com.revature.models.Payment;
import com.revature.models.User;
import com.revature.repositories.OrderRepository;
//import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

import javax.annotation.meta.When;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
public class OrderServiceTestSuite {

    OrderService sut;
    OrderRepository mockOrderRepository;
    UserService mockUserService;
    PaymentService mockPaymentService;
//    EditOrderRequest mockEditOrderRequest;

    @BeforeEach
    public void testPrep(){
        mockOrderRepository = mock(OrderRepository.class);
        mockUserService = mock(UserService.class);
        mockPaymentService = mock(PaymentService.class);
        sut = new OrderService(mockOrderRepository, mockUserService, mockPaymentService);
    }

    @Test
    public void test_findAll_returnListOrderResponse(){
//        List<OrderResponse> orderResponses = new ArrayList<>();
        User validUser = spy(new User(1, "valid", "valid", "valid", "valid", true, true, ""));
        Payment validPayment = spy(new Payment("1", "0000", "Visa", new Date(2000,12,12), validUser));;
        Order validOrder = spy(new Order(1, validUser, validPayment, new Date(2000,12,12), "valid"));


        List<Order> orders = new ArrayList<>();
        orders.add(validOrder);
        when(mockOrderRepository.findAll()).thenReturn(orders);

        sut.findAll();
        verify(mockOrderRepository, times(1)).findAll();
    }



    @Test
    public void test_findAll_UserOrders() {

            List<Order> orders = new ArrayList<>();
            sut.findAllUserOrders(new User());
            verify(mockOrderRepository, times(1)).findByUserId(new User());

    }

    @Test
    public void test_findById_returnOrderResponse_givenValidId(){
        User validUser = spy(new User(1, "valid", "valid", "valid", "valid", true, true, ""));
        Payment validPayment = spy(new Payment("1", "0000", "Visa", new Date(2000,12,12), validUser));;
        Order validOrder = spy(new Order(1, validUser, validPayment, new Date(2000,12,12), "valid"));

        when(mockOrderRepository.findById(1)).thenReturn(Optional.of(validOrder));

        Order actualOrder = sut.findById(1);

        Assertions.assertInstanceOf(Order.class, actualOrder);
        verify(mockOrderRepository, times(1)).findById(anyInt());
    }

    @Test
    public void test_findById_throwResourceNotFoundException_givenInvalidId(){
        when(mockOrderRepository.findById(-10)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> sut.findById(-10));

        verify(mockOrderRepository, times(1)).findById(anyInt());
    }


    @Test
    public void test_create_order() {
        when(mockPaymentService.findPaymentById(anyString())).thenReturn(new Payment());
        when(mockOrderRepository.save(any(Order.class))).thenReturn(new Order());
        sut.createOrder(new CreateOrderRequest("33", "4"), new User());
        verify(mockPaymentService, times(1)).findPaymentById(anyString());
        verify(mockOrderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void test_Update() {
        User validUser = spy(new User(1, "valid", "valid", "valid", "valid", true, true, ""));
        Payment validPayment = spy(new Payment("1", "0000", "Visa", new Date(2000, 12, 12), validUser));

        Order validOrder = spy(new Order(1, validUser, validPayment, new Date(2000, 12, 12), "valid"));

            when(mockOrderRepository.findById(anyInt())).thenReturn(Optional.of(validOrder));
            when(mockPaymentService.findPaymentById(anyString())).thenReturn(new Payment());
            sut.update(new EditOrderRequest(4, "5", "address"), validUser);
            verify(mockOrderRepository, times(1)).findById(anyInt());
            verify(mockPaymentService, times(1)).findPaymentById(anyString());

    }


    @Test
    public void test_UpdateException() {
        User validUser = spy(new User(1, "valid", "valid", "valid", "valid", true, true, ""));
        User invalidUser = spy(new User(2, "valid", "valid", "valid", "valid", true, true, ""));

        Payment validPayment = spy(new Payment("1", "0000", "Visa", new Date(2000, 12, 12), validUser));

        Order validOrder = spy(new Order(4, invalidUser, validPayment, new Date(2000, 12, 12), "valid"));

        when(mockOrderRepository.findById(anyInt())).thenReturn(Optional.of(validOrder));
        when(mockPaymentService.findPaymentById(anyString())).thenReturn(new Payment());

        try {
            sut.update(new EditOrderRequest(4, "5", "address"), validUser);
            verify(mockOrderRepository, times(1)).findById(anyInt());
            verify(mockPaymentService, times(1)).findPaymentById(anyString());
            fail();
        } catch (UnauthorizedException e) {
            assertTrue(true,"Exception is correctly thrown");
        }
    }

}
