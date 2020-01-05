package io.github.lvyahui8.spring.example.wrapper;

import io.github.lvyahui8.spring.aggregate.service.AsyncQueryTaskWrapper;
import io.github.lvyahui8.spring.example.context.ExampleAppContext;
import io.github.lvyahui8.spring.example.context.RequestContext;
import io.github.lvyahui8.spring.example.model.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2020/1/5 13:10
 */
@Slf4j
public class CustomAsyncQueryTaskWrapper implements AsyncQueryTaskWrapper {

    private Long tenantId;
    private User user;

    @Override
    public void beforeSubmit() {
        /* 提交任务前, 先从当前线程拷贝出ThreadLocal中的数据到任务中 */
        log.info("asyncTask beforeSubmit. threadName: {}",Thread.currentThread().getName());
        this.tenantId = RequestContext.getTenantId();
        this.user = ExampleAppContext.getUser();
    }

    @Override
    public void beforeExecute(Thread taskFrom) {
        /* 任务提交后, 执行前, 在异步线程中用数据恢复ThreadLocal(Context) */
        log.info("asyncTask beforeExecute. threadName: {}, taskFrom: {}",Thread.currentThread().getName(),taskFrom.getName());
        RequestContext.setTenantId(tenantId);
        ExampleAppContext.setLoggedUser(user);
    }

    @Override
    public void afterExecute(Thread taskFrom) {
        /* 任务执行完成后, 清理异步线程中的ThreadLocal(Context) */
        log.info("asyncTask afterExecute. threadName: {}, taskFrom: {}",Thread.currentThread().getName(),taskFrom.getName());
        RequestContext.removeTenantId();
        ExampleAppContext.remove();
    }
}
