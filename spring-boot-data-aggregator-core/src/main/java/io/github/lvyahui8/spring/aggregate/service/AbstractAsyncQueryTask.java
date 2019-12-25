package io.github.lvyahui8.spring.aggregate.service;

import lombok.Setter;

import java.util.concurrent.Callable;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/12/25 22:40
 */
public abstract class AbstractAsyncQueryTask<T> implements AsyncQueryTask<T> {
    @Setter
    private Callable<T> callable;
    @Setter
    private Thread      taskFromThread;

    @Override
    public T call() throws Exception {
        try {
            beforeExecute(taskFromThread);
            return callable.call();
        } finally {
            afterExecute(taskFromThread);
        }
    }
}
