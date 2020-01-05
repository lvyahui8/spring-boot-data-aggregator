package io.github.lvyahui8.spring.aggregate.service;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/12/24 23:07
 */
public interface AsyncQueryTaskWrapper {
    /**
     * 任务提交之前执行. 此方法在提交任务的那个线程中执行
     */
    void beforeSubmit();

    /**
     * 任务开始执行前执行. 此方法在异步线程中执行
     * @param taskFrom 提交任务的那个线程
     */
    void beforeExecute(Thread taskFrom);

    /**
     * 任务执行结束后执行. 此方法在异步线程中执行
     * 注意, 不管用户的方法抛出何种异常, 此方法都会执行.
     * @param taskFrom 提交任务的那个线程
     */
    void afterExecute(Thread taskFrom);
}
