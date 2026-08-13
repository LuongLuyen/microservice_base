package com.eazybytes.loans.audit;

import com.eazybytes.common.audit.AuditAwareBase;
import org.springframework.stereotype.Component;

@Component("auditAwareImpl")
public class AuditAwareImpl extends AuditAwareBase {

    @Override
    protected String getServiceName() {
        return "LOANS_MS";
    }
}
