package io.github.lvyahui8.spring.aggregate.service;

import java.util.concurrent.Callable;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/12/25 22:40
 */
public abstract class AsyncQueryTask<T> implements Callable<T> {
    Thread      taskFromThread;
    AsyncQueryTaskWrapper asyncQueryTaskWrapper;

    public AsyncQueryTask(Thread taskFromThread, AsyncQueryTaskWrapper asyncQueryTaskWrapper) {
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
     *
     * @return
     * @throws Exception
     */
    public abstract T  execute() throws Exception;
}
