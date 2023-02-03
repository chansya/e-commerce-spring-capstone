package com.revature.services;


import com.revature.dtos.Principal;
import com.revature.dtos.UpdateUserRequest;
import com.revature.exceptions.InvalidTokenException;
import com.revature.exceptions.InvalidUserInputException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.exceptions.UnauthorizedException;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {


    public static AuthService authService;
    public static UserService userService;
    public static UserRepository userRepository;
    public static SendEmailService sendEmailService;
    public static TokenService tokenService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        sendEmailService = mock(SendEmailService.class);
        tokenService = mock(TokenService.class);
        // userService = new UserService(userRepository);
        userService = mock(UserService.class);
        authService= new AuthService(userService,sendEmailService,tokenService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByCredentials_Test() {
        authService.findByCredentials("misc", "str");
        verify(userService, times(1)).findByCredentials(anyString(), anyString());
    }

    @Test
    void verifyResetPasswordToken_validInputTest(){
        when(userService.findByResetPasswordToken(anyString())).thenReturn(Optional.of(new User()));
        authService.verifyResetPasswordToken("t");
        verify(userService, times(1)).findByResetPasswordToken(anyString());
    }

    @Test
    void verifyResetPasswordToken_notFoundExceptionTest(){
        when(userService.findByResetPasswordToken(anyString())).thenThrow(ResourceNotFoundException.class);
        try{
            authService.verifyResetPasswordToken("q");
            fail("Exception not thrown when it should have been");
        } catch(ResourceNotFoundException e){
            verify(userService, times(1)).findByResetPasswordToken(anyString());
        }
    }

    @Test
    void resetPassword_validInputTest(){
        when(userService.findByResetPasswordToken(anyString())).thenReturn(Optional.of(new User()));
        authService.resetPassword("t","newP");
        verify(userService, times(1)).findByResetPasswordToken(anyString());
        verify(userService, times(1)).resetPassword(any(User.class), anyString());
    }

    @Test
    void resetPassword_notFoundExceptionTest(){
        when(userService.findByResetPasswordToken(anyString())).thenThrow(ResourceNotFoundException.class);
        try {
            authService.resetPassword("t", "newP");
            fail("Exception not thrown when it should have been");
        } catch(ResourceNotFoundException e){
            verify(userService, times(1)).findByResetPasswordToken(anyString());
        }
    }

    @Test
    void getUserByAuthToken_validInputTokenLocatedTest(){
        //possible paths : UnAuthException(null or empty token), invalidTokenException (not found), and Principal when found
        when(tokenService.extractTokenDetails(anyString())).thenReturn(new Principal(1, "yes", true, true));
        when(userService.findUserById(anyInt())).thenReturn(new User());
        User returnedUser = authService.getUserByAuthToken("t");
        verify(tokenService, times(1)).extractTokenDetails(anyString());
        verify(userService, times(1)).findUserById(anyInt());
        assertNotNull(returnedUser);
    }

    @Test
    void getUserByAuthToken_invalidInputTokenExceptionTest(){
        when(tokenService.extractTokenDetails(anyString())).thenThrow(InvalidTokenException.class);
        try {
            authService.getUserByAuthToken("t");
            fail("Exception should have been thrown");
        } catch(InvalidTokenException e){
            verify(tokenService, times(1)).extractTokenDetails(anyString());
        }
    }

    @Test
    void getUserByAuthToken_emptyInputExceptionTest(){
        when(tokenService.extractTokenDetails("")).thenThrow(UnauthorizedException.class);
        try {
            authService.getUserByAuthToken("");
            fail("Exception should have been thrown");
        } catch(UnauthorizedException e){
            verify(tokenService, times(1)).extractTokenDetails(anyString());
        }
    }

    @Test
    void generateAuthToken_Test(){
        // possible paths: isPrincipleValid -> Boolean T return tokenGenerator.createToken(), Boolean F = invalidUserInputException
        when(tokenService.generateToken(any(Principal.class))).thenReturn("newToken");
        authService.generateAuthToken(new User());
        verify(tokenService, times(1)).generateToken(any(Principal.class));
    }

    @Test
    void generateAuthToken_invalidUserInputExceptionTest(){
        // possible paths: isPrincipleValid -> Boolean T return tokenGenerator.createToken(), Boolean F = invalidUserInputException
        when(tokenService.generateToken(any(Principal.class))).thenThrow(InvalidUserInputException.class);
        try {
            authService.generateAuthToken(new User());
            fail("Exception should have been thrown");
        } catch(InvalidUserInputException e){
            verify(tokenService, times(1)).generateToken(any(Principal.class));
        }
    }

    @Test
    void register_Test(){
        authService.register(new User());
        verify(userService, times(1)).save(any(User.class));
    }
    @Test
    void forgotPassword_userNotFoundExceptionTest(){
        doThrow(ResourceNotFoundException.class).when(userService).updateResetPasswordToken(anyString(), anyString());
        try {
            authService.forgotPassword(new UpdateUserRequest("a", "b", "c", "d"));
            fail("Exception should have been thrown");
        } catch(ResourceNotFoundException e){
            verify(userService, times(1)).updateResetPasswordToken(anyString(), anyString());
        }
    }
    @Test
    void forgotPassword_succesfulRequestTest() {
        authService.forgotPassword(new UpdateUserRequest("first", "last", "email", "pass"));
        verify(userService, times(1)).updateResetPasswordToken(anyString(),anyString());
        verify(sendEmailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

}