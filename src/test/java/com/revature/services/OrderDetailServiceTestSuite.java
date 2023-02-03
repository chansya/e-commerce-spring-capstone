package com.revature.services;

import com.revature.dtos.CreateOrderRequest;
import com.revature.dtos.OrderDetailRequest;
import com.revature.dtos.OrderDetailResponse;
import com.revature.models.*;
import com.revature.repositories.OrderDetailRepository;
import com.revature.repositories.OrderRepository;
import com.revature.repositories.PaymentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class OrderDetailServiceTestSuite {
    private static OrderDetailService sut;
    private static OrderDetailRepository orderDetailRepository;
    private static OrderRepository orderRepository;
    private static PaymentRepository paymentRepository;
    private static ProductService productService;
    private static OrderService orderService;

    private static PaymentService paymentService;


    //Change to BeforeEach
    @BeforeEach
    public void init() {

        orderRepository = mock(OrderRepository.class);
        orderDetailRepository = mock(OrderDetailRepository.class);
        productService = mock(ProductService.class);
        orderService = mock(OrderService.class);
        sut = new OrderDetailService(orderDetailRepository, productService, orderService);
    }

    @Test
    public void test_findAll(){
        sut.findAll();
        verify(orderDetailRepository, times(2)).findAll();
    }

    @Test
    public void test_findAllOrderDetailsByOrder(){
        when(orderService.findById(anyInt())).thenReturn(new Order());
        when(orderDetailRepository.findByOrderId(any(Order.class))).thenReturn(new ArrayList<>());
        List<OrderDetailResponse> returnedList = sut.findAllOrderDetailsByOrder(5);
        verify(orderService, times(1)).findById(anyInt());
        verify(orderDetailRepository, times(1)).findByOrderId(any(Order.class));
        Assertions.assertTrue(returnedList!=null);
    }

    @Test
    public void test_findById(){
        sut.findById(5);
        verify(orderDetailRepository, times(1)).findById(anyInt());
    }

    @Test
    public void test_delete_orderExists(){
        //two paths -> RunTime Exception or return true
        when(orderDetailRepository.findById(anyInt())).thenReturn(Optional.of(new OrderDetail()));
        sut.delete(5);
        verify(orderDetailRepository, times(1)).delete(any(OrderDetail.class));

    }

    @Test
    public void test_delete_orderDoesNotExist(){
        when(orderDetailRepository.findById(anyInt())).thenThrow(RuntimeException.class);
        try {
            sut.delete(5);
            fail("Exception should have appeared");
        }catch(RuntimeException e){
            verify(orderDetailRepository, times(1)).findById(anyInt());
        }

    }


    @Test
    public void test_createOrderDetail_returnOrderDetailResponse_givenValidCreateOrderDetailRequest(){
        User validUser = spy(new User(1, "valid", "valid", "valid", "valid", true, true, ""));
        Payment validPayment = spy(new Payment("1", "0000", "Visa", new Date(2000,12,12), validUser));
        Product validProduct = spy(new Product(1,1,1,"valid","valid","valid",true));
        Order validOrder = spy(new Order(1, validUser, validPayment, new Date(2000,12,12), "valid"));
        OrderDetailRequest orderDetailRequest = spy(new OrderDetailRequest(1,1,1));

        when(orderService.findById(validOrder.getId())).thenReturn(validOrder);
        when(productService.findById(orderDetailRequest.getProductId())).thenReturn(Optional.of(validProduct));
        doReturn(new OrderDetail(orderDetailRequest, validOrder, validProduct)).when(orderDetailRepository).save(any(OrderDetail.class));

        OrderDetailResponse validOrderDetailResponse=sut.createOrderDetail(orderDetailRequest);
        Assertions.assertInstanceOf(OrderDetailResponse.class, validOrderDetailResponse);

        verify(orderDetailRepository, times(1)).save(any());
    }
}
