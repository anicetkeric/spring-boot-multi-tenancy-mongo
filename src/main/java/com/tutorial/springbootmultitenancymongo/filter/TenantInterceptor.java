package com.tutorial.springbootmultitenancymongo.filter;

import com.tutorial.springbootmultitenancymongo.exception.TenantAliasNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Slf4j
@Component
public class TenantInterceptor implements WebRequestInterceptor {

    private static final String TENANT_HEADER = "X-Tenant";

    @Override
    public void preHandle(WebRequest request) {
        String tenantHeader = request.getHeader(TENANT_HEADER);

        if (tenantHeader != null && !tenantHeader.isEmpty()) {
            TenantContext.setTenantId(request.getHeader(TENANT_HEADER));
            log.info("Tenant header get: {}", tenantHeader);
        } else {
            log.error("Tenant header not found.");
            throw new TenantAliasNotFoundException("Tenant header not found.");
        }
    }

    @Override
    public void postHandle(WebRequest webRequest, ModelMap modelMap) {
        TenantContext.clear();
    }

    @Override
    public void afterCompletion(WebRequest webRequest, Exception e) {

    }

}