package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import me.mariagomez.dropwizard.audit.providers.PrincipalProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class AuditMethodDispatchProviderTest {

    @Mock
    private ResourceMethodDispatchProvider provider;
    @Mock
    private AbstractResourceMethod abstractResourceMethod;
    @Mock
    private RequestDispatcher defaultRequestDispatcher;
    @Mock
    private AuditWriter auditWriter;
    @Mock
    private PrincipalProvider principalProvider;
    private List<String> auditMethods = newArrayList("POST", "PUT", "DELETE");

    private AuditMethodDispatchProvider auditMethodDispatchProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(provider.create(abstractResourceMethod)).thenReturn(defaultRequestDispatcher);

        auditMethodDispatchProvider = new AuditMethodDispatchProvider(provider, auditWriter, principalProvider);
    }

    @Test
    public void shouldReturnDefaultDispatcherWhenMethodIsNotPOSTOrPUTOrDELETE() {
        when(abstractResourceMethod.getHttpMethod()).thenReturn(randomAlphabetic(4));
        RequestDispatcher requestDispatcherActual = auditMethodDispatchProvider.create(abstractResourceMethod);
        assertThat(requestDispatcherActual, is(defaultRequestDispatcher));
    }

    @Test
    public void shouldReturnCustomDispatcherWhenMethodIsPOSTOrPUTOrDELETE() {
        AuditRequestDispatcher expected = new AuditRequestDispatcher(defaultRequestDispatcher, auditWriter, principalProvider);
        for (String auditMethod : auditMethods) {
            when(abstractResourceMethod.getHttpMethod()).thenReturn(auditMethod);
            AuditRequestDispatcher actual = (AuditRequestDispatcher) auditMethodDispatchProvider.create(abstractResourceMethod);
            assertReflectionEquals(actual, expected);
        }
    }

}