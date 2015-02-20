package me.mariagomez.dropwizard.audit;

public class AuditInfo {

    private String method;

    public AuditInfo(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

}
