package me.mariagomez.dropwizard.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import io.dropwizard.jackson.Jackson;
import me.mariagomez.dropwizard.audit.providers.PrincipalProvider;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.mariagomez.dropwizard.audit.Constants.X_REMOTE_ADDR;

public class AuditRequestDispatcher implements RequestDispatcher {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditRequestDispatcher.class);

    private RequestDispatcher dispatcher;
    private AuditWriter auditWriter;
    private PrincipalProvider principalProvider;

    public AuditRequestDispatcher(RequestDispatcher dispatcher, AuditWriter auditWriter,
                                  PrincipalProvider principalProvider) {
        this.dispatcher = dispatcher;
        this.auditWriter = auditWriter;
        this.principalProvider = principalProvider;
    }

    @Override
    public void dispatch(Object resource, HttpContext context) {
        dispatcher.dispatch(resource, context);

        int responseCode = context.getResponse().getStatus();
        if (responseCode < 200 || responseCode > 299) {
            return;
        }
        String method = context.getRequest().getMethod();
        String path = context.getRequest().getPath();
        String remoteAddress = context.getRequest().getRequestHeaders().getFirst(X_REMOTE_ADDR);
        String username = principalProvider.getUsername();
        DateTime date = DateTime.now();

        String entity = null;
        try {
            entity = MAPPER.writeValueAsString(context.getResponse().getEntity());
        } catch (JsonProcessingException e) {
            LOGGER.error("Error while parsing the entity. \n Message: " + e.getMessage());
        }

        AuditInfo auditInfo = new AuditInfo(method, responseCode, date, entity, path, remoteAddress, username);
        auditWriter.write(auditInfo);
    }
}
