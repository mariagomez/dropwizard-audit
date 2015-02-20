package me.mariagomez.dropwizard.audit;

import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

public class AuditMethodDispatchAdapter implements ResourceMethodDispatchAdapter {
    private AuditWriter auditWriter;

    public AuditMethodDispatchAdapter(AuditWriter auditWriter) {
        this.auditWriter = auditWriter;
    }

    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider) {
        return new AuditMethodDispatchProvider(provider, auditWriter);
    }
}
