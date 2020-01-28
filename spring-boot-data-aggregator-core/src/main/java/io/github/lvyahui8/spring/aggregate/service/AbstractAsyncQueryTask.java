package io.github.lvyahui8.spring.aggregate.service;

import java.util.concurrent.Callable;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/12/25 22:40
 */
public abstract class AbstractAsyncQueryTask<T> implements Callable<T> {
    /**
     * 任务来源线程
     */
    private Thread      taskFromThread;
    /**
     * 异步任务包装器
     */
    private AsyncQueryTaskWrapper asyncQueryTaskWrapper;

    protected AbstractAsyncQueryTask(Thread taskFromThread, AsyncQueryTaskWrapper asyncQueryTaskWrapper) {
        this.taskFromThread = taskFromThread;
        this.asyncQueryTaskWrapper = asyncQueryTaskWrapper;
    }

    @Override
    public T call() throws Exception {
        try {
            if(asyncQueryTaskWrapper != null) {
                asyncQueryTaskWrapper.beforeExecute(taskFromThread);
            }
            return execute();
        } finally {
            if (asyncQueryTaskWrapper != null) {
                asyncQueryTaskWrapper.afterExecute(taskFromThread);
            }
        }
    }

    /**
     * 异步任务的实际内容
     *
     * @return 异步任务返回值
     * @throws Exception 异步任务允许抛出异常
     */
    public abstract T execute() throws Exception;
}
