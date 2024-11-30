package com.itm.space.backendresources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.ws.rs.core.Response;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceLayerTests extends BaseIntegrationTest {

    @MockBean
    private Keycloak keycloakClient;

    @MockBean
    private RealmResource realmResource;

    @MockBean
    private UsersResource usersResource;

    @MockBean
    private Response response;

    private static final String TEST_REALM = "ITM";

    @BeforeEach
    void setup() {
        when(keycloakClient.realm(TEST_REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);
        when(response.getEntity()).thenReturn("user-id");
    }

    @Test
    void createUser_Success() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("new-user");

        Response createdResponse = keycloakClient.realm(TEST_REALM).users().create(user);

        assertEquals(201, createdResponse.getStatus());
        assertEquals("user-id", createdResponse.getEntity());
        System.out.println("Test passed with status: " + createdResponse.getStatus());
    }


    @Test
    void getUserById_Success() {
        UUID userId = UUID.randomUUID();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("new-user");


        when(keycloakClient.realm(TEST_REALM).users().get(userId.toString())).thenReturn(mock(UserResource.class));
        when(keycloakClient.realm(TEST_REALM).users().get(userId.toString()).toRepresentation()).thenReturn(userRepresentation);


        UserRepresentation foundUser = keycloakClient.realm(TEST_REALM).users().get(userId.toString()).toRepresentation();


        assertNotNull(foundUser);
        assertEquals("new-user", foundUser.getUsername());

    }
}
