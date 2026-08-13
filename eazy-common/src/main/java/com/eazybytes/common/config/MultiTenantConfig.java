package com.eazybytes.common.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenantConfig {

    @Configuration
    public static class TenantContext {
        private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

        public static void setTenantId(String tenantId) {
            TENANT_ID.set(tenantId);
        }

        public static String getTenantId() {
            return TENANT_ID.get();
        }

        public static void clear() {
            TENANT_ID.remove();
        }
    }
}
