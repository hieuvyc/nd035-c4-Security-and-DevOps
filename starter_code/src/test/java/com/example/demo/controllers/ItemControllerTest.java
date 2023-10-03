package com.example.demo.controllers;

import com.example.demo.TestUtils;
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

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    private UserController userController;
    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        userController = new UserController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void getItemByIdTest() throws Exception {
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
        modifyCart.setItemId(5);
        modifyCart.setQuantity(5);

        ResponseEntity<List<Item>> listItems = itemController.getItems();

        assertEquals(200, listItems.getStatusCodeValue());
    }

    @Test
    public void getItemsByNameTest() throws Exception {
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

        when(userRepo.findById(u.getId())).thenReturn(Optional.of(u));
        ResponseEntity<User> res = userController.findById(u.getId());
        assertEquals(200, res.getStatusCodeValue());

        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setUsername(u.getUsername());
        modifyCart.setItemId(5);
        modifyCart.setQuantity(5);

        ResponseEntity<List<Item>> listItems = itemController.getItemsByName(u.getUsername());
        assertEquals(404, listItems.getStatusCodeValue());
    }
}
