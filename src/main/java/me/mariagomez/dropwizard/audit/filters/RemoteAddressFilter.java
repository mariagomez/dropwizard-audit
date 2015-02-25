package me.mariagomez.dropwizard.audit.filters;

import com.google.common.annotations.VisibleForTesting;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import static me.mariagomez.dropwizard.audit.Constants.X_REMOTE_ADDR;

public class RemoteAddressFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest req;

    public RemoteAddressFilter() {
        // Do nothing
    }

    @VisibleForTesting
    public RemoteAddressFilter(HttpServletRequest req) {
        this.req = req;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        MultivaluedMap<String, String> requestHeaders = request.getRequestHeaders();
        requestHeaders.add(X_REMOTE_ADDR, req.getRemoteAddr());
        request.setHeaders((InBoundHeaders) requestHeaders);
        return request;
    }
}
