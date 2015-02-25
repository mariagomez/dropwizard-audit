package me.mariagomez.dropwizard.audit;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import me.mariagomez.dropwizard.audit.filters.RemoteAddressFilter;

public class AuditBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private AuditWriter auditWriter;

    public AuditBundle(AuditWriter auditWriter) {
        this.auditWriter = auditWriter;
    }

    @Override
    public void run(T configuration, Environment environment) {
        JerseyEnvironment jersey = environment.jersey();
        jersey.register(new AuditMethodDispatchAdapter(auditWriter));
        jersey.getResourceConfig().getContainerRequestFilters().add(new RemoteAddressFilter());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
