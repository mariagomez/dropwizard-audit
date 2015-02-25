package me.mariagomez.dropwizard.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import io.dropwizard.jackson.Jackson;
import me.mariagomez.dropwizard.audit.providers.PrincipalProvider;
import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.ws.rs.core.MultivaluedMap;

import static me.mariagomez.dropwizard.audit.Constants.X_REMOTE_ADDR;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuditRequestDispatcherTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Mock
    private HttpContext context;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private AuditWriter auditWriter;
    @Mock
    private HttpResponseContext responseContext;
    @Mock
    private PrincipalProvider principalProvider;

    private String method;
    private String entity;
    private String path;
    private String remoteAddress;
    private String username;
    private AuditRequestDispatcher auditRequestDispatcher;

    @Before
    public void setUp() {
        initMocks(this);
        entity = RandomStringUtils.randomAlphabetic(100);
        path = randomAlphabetic(20);
        method = randomAlphabetic(4);
        remoteAddress = randomAlphabetic(10);
        username = randomAlphabetic(10);
        MultivaluedMap<String, String> headerList = new MultivaluedMapImpl();
        headerList.putSingle(X_REMOTE_ADDR, remoteAddress);
        HttpRequestContext requestContext = mock(HttpRequestContext.class);
        when(context.getResponse()).thenReturn(responseContext);
        when(context.getRequest()).thenReturn(requestContext);
        when(requestContext.getMethod()).thenReturn(method);
        when(requestContext.getPath()).thenReturn(path);
        when(requestContext.getRequestHeaders()).thenReturn(headerList);
        when(responseContext.getEntity()).thenReturn(entity);
        when(principalProvider.getUsername()).thenReturn(username);

        auditRequestDispatcher = new AuditRequestDispatcher(dispatcher, auditWriter, principalProvider);
    }

    @Test
    public void shouldCallTheDefaultDispatcherToGoThroughTheChain() {
        Object resource = new Object();

        auditRequestDispatcher.dispatch(resource, context);

        verify(dispatcher, times(1)).dispatch(resource, context);
    }

    @Test
    public void shouldNotCallTheWriterWhenResponseCodeIsLessThan200() {
        int codigoRespuesta = nextInt(200);
        when(responseContext.getStatus()).thenReturn(codigoRespuesta);

        auditRequestDispatcher.dispatch(new Object(), context);

        verifyZeroInteractions(auditWriter);
    }

    @Test
    public void shouldNotCallTheWriterWhenResponseCodeIsMoreThan200() {
        int codigoRespuesta = nextInt(300) + 300;
        when(responseContext.getStatus()).thenReturn(codigoRespuesta);

        auditRequestDispatcher.dispatch(new Object(), context);

        verifyZeroInteractions(auditWriter);
    }

    @Test
    public void shouldTheWriterWithTheAuditInfoIfTheResponseCodeIsInThe200s() {
        int successResponseCode = nextInt(100) + 200;
        when(responseContext.getStatus()).thenReturn(successResponseCode);

        auditRequestDispatcher.dispatch(new Object(), context);

        verify(auditWriter, times(1)).write(isA(AuditInfo.class));
    }

    @Test
    public void shouldSendTheCompleteAuditInfoToTheWriter() throws JsonProcessingException {
        int successResponseCode = nextInt(100) + 200;
        when(responseContext.getStatus()).thenReturn(successResponseCode);

        auditRequestDispatcher.dispatch(new Object(), context);

        ArgumentCaptor<AuditInfo> captor = ArgumentCaptor.forClass(AuditInfo.class);
        verify(auditWriter, times(1)).write(captor.capture());
        assertAuditInfo(successResponseCode, captor.getValue());
    }

    private void assertAuditInfo(int successResponseCode, AuditInfo value) throws JsonProcessingException {
        assertThat(value, instanceOf(AuditInfo.class));
        assertThat(value.getMethod().toString(), is(method));
        assertThat(value.getResponseCode(), is(successResponseCode));
        assertThat(value.getDate(), is(instanceOf(DateTime.class)));
        assertThat(value.getEntity(), is(MAPPER.writeValueAsString(entity)));
        assertThat(value.getPath(), is(path));
        assertThat(value.getRemoteAddress(), is(remoteAddress));
        assertThat(value.getUsername(), is(username));
    }
}