package org.agoncal.application.conference.venue.rest;

import org.agoncal.application.conference.venue.domain.Room;
import org.agoncal.application.conference.venue.repository.RoomRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@RunAsClient
public class RoomEndpointTest {

    // ======================================
    // =             Attributes             =
    // ======================================

    private static final Room TEST_ROOM = new Room("name", 123, "setup");
    private static String roomId;
    private Client client;
    private WebTarget webTarget;

    // ======================================
    // =          Injection Points          =
    // ======================================

    @ArquillianResource
    private URI baseURL;

    // ======================================
    // =         Deployment methods         =
    // ======================================

    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class)
                .addClasses(Room.class, RoomEndpoint.class, RoomRepository.class, Application.class);
    }

    // ======================================
    // =          Lifecycle methods         =
    // ======================================

    @Before
    public void initWebTarget() {
        client = ClientBuilder.newClient();
        webTarget = client.target(baseURL).path("api/rooms");
    }

    // ======================================
    // =            Test methods            =
    // ======================================

    @Test
    @InSequence(1)
    public void shouldFailCreatingInvalidRoom() throws Exception {
        Response response = webTarget.request(APPLICATION_JSON_TYPE).post(Entity.entity(null, APPLICATION_JSON_TYPE));
        assertEquals(400, response.getStatus());
    }

    @Test
    @InSequence(2)
    public void shouldCreateARoom() throws Exception {
        Response response = webTarget.request(APPLICATION_JSON_TYPE).post(Entity.entity(TEST_ROOM, APPLICATION_JSON_TYPE));
        assertEquals(201, response.getStatus());
        roomId = getRoomId(response);
        TEST_ROOM.setId(roomId);
    }

    @Test
    @InSequence(3)
    public void shouldGetAlreadyCreatedRoom() throws Exception {
        Response response = webTarget.path(roomId).request(APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        JsonObject jsonObject = readJsonContent(response);
        assertEquals(TEST_ROOM.getName(), jsonObject.getString("name"));
        assertEquals(TEST_ROOM.getCapacity(), new Integer(jsonObject.getInt("capacity")));
        assertEquals(TEST_ROOM.getSetup(), jsonObject.getString("setup"));
    }

    @Test
    @InSequence(5)
    public void shouldUpdateCreatedRoom() throws Exception {
        TEST_ROOM.setName("updated name");
        Response response = webTarget.request(APPLICATION_JSON_TYPE).put(Entity.entity(TEST_ROOM, APPLICATION_JSON_TYPE));
        assertEquals(200, response.getStatus());
        JsonObject jsonObject = readJsonContent(response);
        assertEquals(roomId, jsonObject.getString("id"));
        assertEquals(TEST_ROOM.getName(), jsonObject.getString("name"));
    }

    @Test
    @InSequence(6)
    public void shouldRemoveRoom() throws Exception {
        Response response = webTarget.path(roomId).request(APPLICATION_JSON_TYPE).delete();
        assertEquals(204, response.getStatus());
    }

    @Test
    @InSequence(7)
    public void shouldRemoveWithInvalidInput() throws Exception {
        Response response = webTarget.request(APPLICATION_JSON_TYPE).delete();
        assertEquals(405, response.getStatus());
    }

    // ======================================
    // =           Private methods          =
    // ======================================

    private String getRoomId(Response response) {
        String location = response.getHeaderString("location");
        return location.substring(location.lastIndexOf("/") + 1);
    }

    private static JsonObject readJsonContent(Response response) {
        JsonReader jsonReader = readJsonStringFromResponse(response);
        return jsonReader.readObject();
    }

    private static JsonReader readJsonStringFromResponse(Response response) {
        String competitionJson = response.readEntity(String.class);
        StringReader stringReader = new StringReader(competitionJson);
        return Json.createReader(stringReader);
    }
}
