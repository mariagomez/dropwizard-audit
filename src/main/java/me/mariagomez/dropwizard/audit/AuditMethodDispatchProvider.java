package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

import java.util.Arrays;
import java.util.List;

public class AuditMethodDispatchProvider implements ResourceMethodDispatchProvider {

    private static final List<String> AUDITABLE_METHODS = Arrays.asList("POST", "PUT", "DELETE");
    private ResourceMethodDispatchProvider provider;
    private AuditWriter auditWriter;

    public AuditMethodDispatchProvider(ResourceMethodDispatchProvider provider) {
        this.provider = provider;
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        RequestDispatcher requestDispatcher = provider.create(abstractResourceMethod);
        if (isMethodAuditable(abstractResourceMethod.getHttpMethod())){
            return new AuditRequestDispatcher(requestDispatcher, auditWriter);
        }
        return requestDispatcher;
    }

    private boolean isMethodAuditable(String method) {
        return AUDITABLE_METHODS.contains(method);
    }

}
