package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public class AuditProvider implements ResourceMethodDispatchProvider {
    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        return null;
    }
}
