package org.expenses.web.service;

import org.expenses.web.beans.Loggable;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.StringReader;
import java.net.URI;
import java.util.logging.Logger;

@Loggable
public class RateService implements Rateable {

    private static URI uri = UriBuilder
            .fromUri("http://apilayer.net/api/live?access_key=1b12a77df2533a1e60e0426d3610c2d8&currencies=EUR").build();
    @Inject
    private Logger logger;

    public double rate() {
        Response response = ClientBuilder.newClient().target(uri).request().get();
        String body = response.readEntity(String.class);
        double rate;

        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            JsonObject obj = reader.readObject();
            rate = obj.getJsonObject("quotes").getJsonNumber("USDEUR").doubleValue();
        }

        logger.info("Current rate is : " + rate);
        return rate;
    }
}
