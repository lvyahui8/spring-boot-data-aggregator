package io.github.lvyahui8.spring.aggregate2;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/4/2
 */
public class DefaultExceptionHandler implements ExceptionHandler
{
    @Override
    public Object handle(Exception e) {
        // nothing to do
        return null;
    }
}
