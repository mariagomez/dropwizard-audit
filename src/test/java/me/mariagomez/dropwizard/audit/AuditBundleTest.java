package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.core.ResourceConfig;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import me.mariagomez.dropwizard.audit.filters.RemoteAddressFilter;
import me.mariagomez.dropwizard.audit.providers.PrincipalProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class AuditBundleTest {

    @Mock
    private Configuration configuration;
    @Mock
    private Environment environment;
    @Mock
    private JerseyEnvironment jersey;
    @Mock
    private AuditWriter auditWriter;
    @Mock
    private ResourceConfig resourceConfig;
    @Mock
    private PrincipalProvider principalProvider;

    private List<RemoteAddressFilter> filters;
    private AuditBundle auditBundle;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        filters = newArrayList();
        when(environment.jersey()).thenReturn(jersey);
        when(jersey.getResourceConfig()).thenReturn(resourceConfig);
        when(resourceConfig.getContainerRequestFilters()).thenReturn(filters);
        auditBundle = new AuditBundle(auditWriter, principalProvider);
    }

    @Test
    public void shouldRegisterAuditAdapter() throws Exception {
        AuditMethodDispatchAdapter auditMethodDispatchAdapter = new AuditMethodDispatchAdapter(auditWriter, principalProvider);
        auditBundle.run(configuration, environment);
        verify(jersey, times(1)).register(refEq(auditMethodDispatchAdapter));
    }

    @Test
    public void shouldRegisterTheFilterToCaptureRemoteIP() {
        auditBundle.run(configuration, environment);
        RemoteAddressFilter remoteAddressFilter = new RemoteAddressFilter();
        RemoteAddressFilter actualRemoteAddressFilter = filters.get(0);
        assertReflectionEquals(actualRemoteAddressFilter, remoteAddressFilter);
    }
}