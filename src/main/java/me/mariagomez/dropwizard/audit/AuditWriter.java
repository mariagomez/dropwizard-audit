package me.mariagomez.dropwizard.audit;

public interface AuditWriter {
    void write(AuditInfo auditInfo);
}
