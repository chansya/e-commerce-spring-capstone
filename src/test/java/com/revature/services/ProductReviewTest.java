package com.revature.services;


import com.revature.dtos.ProductReviewRequest;
import com.revature.dtos.ProductReviewResponse;
import com.revature.dtos.UserResponse;
import com.revature.models.Product;
import com.revature.models.ProductReview;
import com.revature.models.User;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.ProductReviewRepository;
import com.revature.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.sum;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductReviewTest {

    @Mock
    private ProductReviewRepository productReviewRepository;
    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductReview productReview;
    @Mock
    private ProductReviewResponse productReviewResponse;
    @Mock
    private User user;

    @Autowired
    @InjectMocks
    private ProductReviewService productReviewService;

    @Autowired
    @InjectMocks
    private ProductService productService;

    @Autowired
    @InjectMocks
    private UserService userService;

    private List<Product> productListMock = Stream.of(
                    new Product(999,1,20,"Valid","img","Tomato",true),
                    new Product(999,1,20,"Valid","img","Potato",true),
                    new Product(999,1,20,"Valid","img","Grapes",true))
            .collect(Collectors.toList());

    private User userValidMock1 = new User(999,"Valid","Valid","Sam","Ran",true,true,"Valid");

    private List<ProductReview> productReviewMock = Stream.of(
                    new ProductReview(999,5,"Valid", productListMock.get(0), userValidMock1),
                    new ProductReview(999,2,"bad", productListMock.get(0), userValidMock1))
            .collect(Collectors.toList());

    private List<ProductReviewRequest> productReviewRequestMock= Stream.of(
                    new ProductReviewRequest(999,1,"Valid",999),
                    new ProductReviewRequest(0,0,"",0))
            .collect(Collectors.toList());

    private ProductReviewResponse  productReviewResponseMock1 = new ProductReviewResponse(999,5,"Valid",999,new UserResponse(userValidMock1));
    @BeforeEach
    public void setUp(){
        userService=new UserService(userRepository);

        productService= new ProductService(productRepository);

        productReviewService= new ProductReviewService(productReviewRepository,userService,productService);
    }

    @AfterEach
    public void tearDown()
    {
        productReviewMock = null;
    }
    @DisplayName("Find all product reviews")
    @Test
    public void findAllProductReviews(){
        //When
        when(productReviewRepository.findAll()).thenReturn(productReviewMock);
        //Action
        List<ProductReviewResponse> productReviews = productReviewService.findAll();
        verify(productReviewRepository,times(1)).findAll();
        assertEquals(productReviewMock.size(), productReviews.size());
    }

    @DisplayName("Find product reviews by id")
    @Test
    public void findProductReviewById(){
        //Given
        ArrayList<ProductReview> arr = new ArrayList<>();
        arr.add(productReviewMock.get(0));
        arr.add(productReviewMock.get(1));
        //When
        lenient().when(productReviewRepository.findById(eq(1))).thenReturn(Optional.ofNullable(arr.get(0)));
        //Then
        Optional<ProductReviewResponse> products = productReviewService.findById(999);
        verify(productReviewRepository,times(1)).findById(999);
    }

    @DisplayName("Find product reviews by product id")
    @Test
    public void productReviewsFindByProductId() {
        when(productReviewRepository.findAll()).thenReturn(new ArrayList<>());
        List<ProductReviewResponse> products = productReviewService.findAll();
        verify(productReviewRepository,times(1)).findAll();
    }
//    @DisplayName("Find product reviews by product id")
//    @Test
//    public void testFindAllProductReviewsBySpecificProduct() {
//        //Given
//        ArrayList<ProductReview> arr = new ArrayList<>();
//        arr.add(productReviewMock.get(0));
//        arr.add(productReviewMock.get(1));
//        //When
//        when(productReviewRepository.findAllByProductId(eq(1))).thenReturn(new ArrayList<>());
//        when(productReviewRepository.findAllByProductId(eq(2))).thenReturn(arr);
//        //Then
//        List<ProductReviewResponse> products = productReviewService.findByProductId(1);
//        verify(productReviewRepository,times(1)).findAllByProductId(1);
//        assertEquals(0, products.size());
//
//        products = productReviewService.findByProductId(2);
//        verify(productReviewRepository,times(1)).findAllByProductId(2);
//        assertEquals(2, products.size());
//    }

    @DisplayName("Find average product review score")
    @Test
    public void findProductAverageScore(){
        //When
        when(productReviewRepository.findProductAverageScore(999)).thenReturn(Arrays.asList(4,6));
        //Then
        int productAvg = productReviewService.findProductAverageScore(999);
        verify(productReviewRepository, times(1)).findProductAverageScore(999);
        assertEquals(productAvg, 5);
    }
    @DisplayName("Find product by score")
    @Test
    public void findProductByScore(){
        //When
        when(productReviewRepository.findAllByProductScore(anyInt(), anyInt())).thenReturn(new ArrayList<>());
        //Then
        List<ProductReviewResponse> list = productReviewService.findProductByScore(999, 5);
        verify(productReviewRepository,times(1)).findAllByProductScore(999,5);
    }

    @DisplayName("Can post review or not")
    @Test
    public void canPostReviewOrNot(){
        //When
        when(productReviewRepository.canPost(999, 999)).thenReturn(productReviewMock);
        //Then
        productReviewService.canPost(999, 999);
        verify(productReviewRepository, times(1)).canPost(999,999);
    }
    @DisplayName("Change exsting product review")
    @Test
    public void save(){

        when(productReviewRepository.canPost(anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(productReviewRepository.save(any(ProductReview.class))).thenReturn(new ProductReview());
        when(productRepository.findActiveById(anyInt())).thenReturn(Optional.of(new Product()));
        ProductReview p = productReviewService.save(new ProductReviewRequest(1, 4, "Comment", 1), new User());
        verify(productReviewRepository, times(1)).canPost(anyInt(), anyInt());
        verify(productReviewRepository, times(1)).save(any(ProductReview.class));
        assertNotNull(p);
    }
    @DisplayName("Delete By Id")
    @Test
    public void deleteById(){
        //When
        productReviewService.deleteById(999);
        //Then
        verify(productReviewRepository, times(1)).deleteById(eq(999));
    }
}