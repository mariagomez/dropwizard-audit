package me.mariagomez.dropwizard.audit.filters;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static me.mariagomez.dropwizard.audit.Constants.X_REMOTE_ADDR;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RemoteAddressFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private ContainerRequest containerRequest;
    private RemoteAddressFilter remoteAddressFilter;

    @Before
    public void setUp() {
        initMocks(this);
        remoteAddressFilter = new RemoteAddressFilter(request);
    }

    @Test
    public void shouldAddTheRemoteIPInTheRequestHeader() {
        String remoteIP = randomAlphanumeric(15);
        when(request.getRemoteAddr()).thenReturn(remoteIP);
        InBoundHeaders value = new InBoundHeaders();
        when(containerRequest.getRequestHeaders()).thenReturn(value);
        ContainerRequest actualRequest = remoteAddressFilter.filter(containerRequest);
        assertThat(actualRequest.getRequestHeaders().keySet(), CoreMatchers.hasItem(X_REMOTE_ADDR));
        assertThat(actualRequest.getRequestHeaders().getFirst(X_REMOTE_ADDR), is(remoteIP));
    }

}