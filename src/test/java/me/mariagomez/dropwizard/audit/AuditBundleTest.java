package me.mariagomez.dropwizard.audit;

import io.dropwizard.Configuration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuditBundleTest {

    @Mock
    private Configuration configuration;
    @Mock
    private Environment environment;
    @Mock
    private JerseyEnvironment jersey;

    private AuditBundle auditBundle;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(environment.jersey()).thenReturn(jersey);
        auditBundle = new AuditBundle();
    }

    @Test
    public void shouldRegisterAuditAdapter() throws Exception {
        auditBundle.run(configuration, environment);
        verify(jersey, times(1)).register(isA(AuditMethodDispatcherAdapter.class));
    }
}