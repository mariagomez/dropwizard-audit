package me.mariagomez.dropwizard.audit;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AuditBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private AuditWriter auditWriter;

    @Override
    public void run(T configuration, Environment environment) {
        environment.jersey().register(new AuditMethodDispatchAdapter(auditWriter));
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
