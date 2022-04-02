package io.github.lvyahui8.spring.aggregate2;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/4/1
 */
@Data
public class ResourceNode {
    Object target;
    Method method;

    Map<String,Dependent> dependents;

    @Data
    class Dependent {
        boolean strong;
        ResourceNode node;
    }

    public Object invoke(Context context) throws Exception {
        List<Object> args = new LinkedList<>();
        if (dependents != null) {
            List<CompletableFuture<Object>> asyncList = new LinkedList<>();
            for (Map.Entry<String, Dependent> entry : dependents.entrySet()) {
                final Dependent dependent = entry.getValue();
                asyncList.add(CompletableFuture.supplyAsync(() -> {
                            try {
                                return dependent.node.invoke(context);
                            } catch (Exception e) {
                                if (dependent.isStrong()) {
                                    throw new StrongDependentException(e);
                                } else {
                                    return context.getExceptionHandler().handle(e);
                                }
                            }
                        }, context.getExecutor()));
            }
            final CompletableFuture<Void> allOf = CompletableFuture.allOf(asyncList.toArray(new CompletableFuture[0]));
            allOf.wait(context.getDefaultTimeout());
            for (CompletableFuture<Object> future : asyncList) {
                args.add(future.get());
            }
        }
        return method.invoke(target,args.toArray(new Object[0]));
    }
}
