package com.itm.space.backendresources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WithMockUser(username = "testUser", roles = "MODERATOR")
public class ApiLayerTests extends BaseIntegrationTest {


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void testCreateUser() throws Exception {
        UserRequest userRequest;
        userRequest = new UserRequest("testUsername", "testEmail@gmail.com", "testPassword", "TestName", "TestLastName");

        String userJson = objectMapper.writeValueAsString(userRequest);

        doNothing().when(userService).createUser(any(UserRequest.class));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());


        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    public void testGetUserById() throws Exception {

        UUID userId = UUID.randomUUID();


        UserResponse userResponse = new UserResponse("testUsername", "testEmail@gmail.com", "testPassword", null, null);


        when(userService.getUserById(userId)).thenReturn(userResponse);


        mockMvc.perform(get("/api/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Ожидаем статус 200
                .andExpect(jsonPath("firstName").value("testUsername"));


        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    public void testHello() throws Exception {

        mockMvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("testUser"));
    }
}
