package me.mariagomez.dropwizard.audit;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public class AuditRequestDispatcher implements RequestDispatcher {

    private RequestDispatcher dispatcher;
    private AuditWriter auditWriter;

    public AuditRequestDispatcher(RequestDispatcher dispatcher, AuditWriter auditWriter) {
        this.dispatcher = dispatcher;
        this.auditWriter = auditWriter;
    }

    @Override
    public void dispatch(Object resource, HttpContext context) {
        dispatcher.dispatch(resource, context);

        int responseCode = context.getResponse().getStatus();
        if (responseCode < 200 || responseCode > 299) {
            return;
        }

        String method = context.getRequest().getMethod();
        AuditInfo auditInfo = new AuditInfo(method);
        auditWriter.write(auditInfo);

    }
}
