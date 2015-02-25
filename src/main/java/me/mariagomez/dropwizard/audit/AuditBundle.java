package me.mariagomez.dropwizard.audit;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import me.mariagomez.dropwizard.audit.filters.RemoteAddressFilter;
import me.mariagomez.dropwizard.audit.providers.PrincipalProvider;

public class AuditBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private AuditWriter auditWriter;
    private PrincipalProvider principalProvider;

    public AuditBundle(AuditWriter auditWriter, PrincipalProvider principalProvider) {
        this.auditWriter = auditWriter;
        this.principalProvider = principalProvider;
    }

    @Override
    public void run(T configuration, Environment environment) {
        JerseyEnvironment jersey = environment.jersey();
        jersey.register(new AuditMethodDispatchAdapter(auditWriter, principalProvider));
        jersey.getResourceConfig().getContainerRequestFilters().add(new RemoteAddressFilter());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
