package me.mariagomez.dropwizard.audit;

import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import me.mariagomez.dropwizard.audit.providers.PrincipalProvider;

public class AuditMethodDispatchAdapter implements ResourceMethodDispatchAdapter {
    private AuditWriter auditWriter;
    private PrincipalProvider principalProvider;

    public AuditMethodDispatchAdapter(AuditWriter auditWriter, PrincipalProvider principalProvider) {
        this.auditWriter = auditWriter;
        this.principalProvider = principalProvider;
    }

    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider) {
        return new AuditMethodDispatchProvider(provider, auditWriter, principalProvider);
    }
}
