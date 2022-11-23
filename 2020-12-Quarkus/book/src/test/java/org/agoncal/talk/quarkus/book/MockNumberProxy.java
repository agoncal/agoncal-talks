package org.agoncal.talk.quarkus.book;

import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Mock
@RestClient
public class MockNumberProxy implements NumberProxy {

    @Override
    public String generateISBN() {
        return "mock isbn";
    }
}
