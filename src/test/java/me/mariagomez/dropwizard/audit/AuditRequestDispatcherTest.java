package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

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

    @Mock
    private HttpContext context;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private AuditWriter auditWriter;
    @Mock
    private HttpResponseContext responseContext;
    private String method;
    private AuditRequestDispatcher auditRequestDispatcher;

    @Before
    public void setUp() {
        initMocks(this);
        HttpRequestContext requestContext = mock(HttpRequestContext.class);
        method = randomAlphabetic(4);
        when(context.getResponse()).thenReturn(responseContext);
        when(context.getRequest()).thenReturn(requestContext);
        when(requestContext.getMethod()).thenReturn(method);

        auditRequestDispatcher = new AuditRequestDispatcher(dispatcher, auditWriter);
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
    public void shouldSendTheCompleteAuditInfoToTheWriter() {
        int successResponseCode = nextInt(100) + 200;
        when(responseContext.getStatus()).thenReturn(successResponseCode);

        auditRequestDispatcher.dispatch(new Object(), context);
        ArgumentCaptor<AuditInfo> captor = ArgumentCaptor.forClass(AuditInfo.class);
        verify(auditWriter, times(1)).write(captor.capture());
        assertThat(captor.getValue(), instanceOf(AuditInfo.class));
        assertThat(captor.getValue().getMethod().toString(), is(method));
    }
}