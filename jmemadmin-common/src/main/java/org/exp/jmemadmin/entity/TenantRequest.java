package org.exp.jmemadmin.entity;

public class TenantRequest {
    // TODO:attributions wait to expand
    private String tenant;

    public TenantRequest() {
        // Do nothing
    }

    public TenantRequest(String tenant) {
        this.tenant = tenant;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String toString() {
        return "TenantRequest [tenant=" + tenant + "]";
    }

}
