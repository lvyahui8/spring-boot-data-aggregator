package io.github.lvyahui8.spring.example.context;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/22 23:11
 */
public class RequestContext {
    private static ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static void removeTenantId() {
        TENANT_ID.remove();
    }
}
