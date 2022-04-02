package io.github.lvyahui8.spring.aggregate2;

import lombok.Data;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/4/1
 */
@Data
public class Context {
    Map<String,Object> resultMap;
    Map<String,Object> paramMap;
    Executor executor;
    ExceptionHandler exceptionHandler = new DefaultExceptionHandler();
    long defaultTimeout;
}
