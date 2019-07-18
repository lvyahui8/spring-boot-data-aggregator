package io.github.lvyahui8.spring.aggregate.concurrent;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/18 21:49
 */
public abstract class AbstractRunnableWrapper implements Runnable {
    private Thread rootThread;

    @Override
    public void run() {
        /* copy context to executor service threads */
        try {
            doRun();
        } finally {
            /* remove all cope context*/
        }
    }

    /**
     * actual run method
     */
    public abstract void doRun();
}
