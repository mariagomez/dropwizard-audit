package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
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

public class AuditProviderTest {

    @Mock
    private ResourceMethodDispatchProvider provider;
    @Mock
    private AbstractResourceMethod abstractResourceMethod;
    @Mock
    private RequestDispatcher defaultRequestDispatcher;
    private List<String> auditMethods = newArrayList("POST", "PUT", "DELETE");

    private AuditProvider auditProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(provider.create(abstractResourceMethod)).thenReturn(defaultRequestDispatcher);

        auditProvider = new AuditProvider(provider);
    }

    @Test
    public void shouldReturnDefaultDispatcherWhenMethodIsNotPOSTOrPUTOrDELETE() {
        when(abstractResourceMethod.getHttpMethod()).thenReturn(randomAlphabetic(4));
        RequestDispatcher requestDispatcherActual = auditProvider.create(abstractResourceMethod);
        assertThat(requestDispatcherActual, is(defaultRequestDispatcher));
    }

    @Test
    public void shouldReturnCustomDispatcherWhenMethodIsPOSTOrPUTOrDELETE() {
        AuditMethodDispatcher expected = new AuditMethodDispatcher(defaultRequestDispatcher);
        for (String auditMethod : auditMethods) {
            when(abstractResourceMethod.getHttpMethod()).thenReturn(auditMethod);
            AuditMethodDispatcher actual = (AuditMethodDispatcher) auditProvider.create(abstractResourceMethod);
            assertReflectionEquals(actual, expected);
        }
    }

}