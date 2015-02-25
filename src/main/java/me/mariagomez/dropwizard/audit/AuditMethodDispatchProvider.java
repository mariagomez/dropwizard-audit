package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import me.mariagomez.dropwizard.audit.providers.PrincipalProvider;

import java.util.Arrays;
import java.util.List;

public class AuditMethodDispatchProvider implements ResourceMethodDispatchProvider {

    private static final List<String> AUDITABLE_METHODS = Arrays.asList("POST", "PUT", "DELETE");
    private ResourceMethodDispatchProvider provider;
    private AuditWriter auditWriter;
    private PrincipalProvider principalProvider;

    public AuditMethodDispatchProvider(ResourceMethodDispatchProvider provider, AuditWriter auditWriter,
                                       PrincipalProvider principalProvider) {
        this.provider = provider;
        this.auditWriter = auditWriter;
        this.principalProvider = principalProvider;
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        RequestDispatcher requestDispatcher = provider.create(abstractResourceMethod);
        if (isMethodAuditable(abstractResourceMethod.getHttpMethod())){
            return new AuditRequestDispatcher(requestDispatcher, auditWriter, principalProvider);
        }
        return requestDispatcher;
    }

    private boolean isMethodAuditable(String method) {
        return AUDITABLE_METHODS.contains(method);
    }

}
