package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public class AuditMethodDispatcher implements RequestDispatcher {
    public AuditMethodDispatcher(RequestDispatcher dispatcher) {


    }

    @Override
    public void dispatch(Object resource, HttpContext context) {

    }
}
