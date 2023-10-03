package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    public User createUser() {
        Cart cart = new Cart();
        cart.setId(Long.valueOf(1));
        cart.addItem(createItem());

        User user = new User();
        user.setId(Long.valueOf(1));
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);
        return user;
    }

    public Item createItem(){
        Item item = new Item();
        item.setId(Long.valueOf(1));
        item.setDescription("books");
        item.setPrice(BigDecimal.valueOf(10,03));
        return item;
    }

    private UserOrder createOrder() {
        Cart cart = new Cart();
        cart.setId(Long.valueOf(1));
        cart.addItem(createItem());
        return UserOrder.createFromCart(cart);
    }

    @Test
    public void submitOrderTest() throws Exception {
        when(userRepo.findByUsername("test")).thenReturn(createUser());
        ResponseEntity<UserOrder> userOrder = orderController.submit("test");
        assertEquals(HttpStatus.OK, userOrder.getStatusCode());
    }

    @Test
    public void getOrdersForUserTest() throws Exception {
        when(userRepo.findByUsername("test")).thenReturn(createUser());
        when(orderRepo.findByUser(any())).thenReturn(List.of(createOrder()));
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("test");
        assertEquals(HttpStatus.OK, ordersForUser.getStatusCode());
    }
}
