package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    private CartController cartController;
    private UserController userController;
    private CartRepository cartRepo = mock(CartRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        when(userRepo.findByUsername("test")).thenReturn(createUser());
        when(cartRepo.save(any())).thenReturn(createCart());
    }

    public User createUser() {
        User user = new User();
        user.setId(Long.valueOf(1));
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(createCart());
        return user;
    }

    public Cart createCart() {
        Cart cart = new Cart();
        cart.setId(Long.valueOf(1));
        cart.addItem(createItem());
        return cart;
    }

    public Item createItem(){
        Item item = new Item();
        item.setId(Long.valueOf(1));
        item.setDescription("books");
        item.setPrice(BigDecimal.valueOf(10,03));
        return item;
    }

    @Test
    public void addToCartTest() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashes");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashes", u.getPassword());

        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setUsername(u.getUsername());
        modifyCart.setItemId(2);
        modifyCart.setQuantity(2);

        ResponseEntity<Cart> cartRes = cartController.addTocart(modifyCart);

        assertNotNull(cartRes);
    }

    @Test
    public void removeFromCartTest() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());

        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setUsername(u.getUsername());
        modifyCart.setItemId(5);
        modifyCart.setQuantity(5);

        ResponseEntity<Cart> cartRes = cartController.removeFromcart(modifyCart);

        assertNotNull(cartRes);
    }
}
