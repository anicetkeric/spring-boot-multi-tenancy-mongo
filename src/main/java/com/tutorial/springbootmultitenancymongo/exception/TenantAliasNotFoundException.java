package com.tutorial.springbootmultitenancymongo.exception;

/**
 * <h2>TenantAliasNotFoundException</h2>
 *
 * @author aek
 *
 * Description: trigger exception when tenant alias not found
 */
public class TenantAliasNotFoundException extends RuntimeException {
    public TenantAliasNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public TenantAliasNotFoundException(String msg) {
        super(msg);
    }
}
