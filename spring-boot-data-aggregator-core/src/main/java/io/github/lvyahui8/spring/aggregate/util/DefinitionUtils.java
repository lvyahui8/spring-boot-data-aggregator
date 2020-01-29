package io.github.lvyahui8.spring.aggregate.util;

import io.github.lvyahui8.spring.aggregate.model.*;
import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.annotation.DynamicParameter;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import io.github.lvyahui8.spring.enums.ExceptionProcessingMethod;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/14 13:34
 */
public class DefinitionUtils {

    /**
     * get provider's consume definitions
     *
     * @param method provider method
     * @return result
     */
    public static DataProvideDefinition getProvideDefinition(Method method){
        List<DataConsumeDefinition> consumeDefinitions = new ArrayList<>();
        Parameter[] parameters = method.getParameters();

        DataProvideDefinition provider = new DataProvideDefinition();
        List<MethodArg> methodArgs = new ArrayList<>(method.getParameterCount());
        provider.setDepends(new ArrayList<>(method.getParameterCount()));
        provider.setParams(new ArrayList<>(method.getParameterCount()));
        provider.setMethod(method);

        for (Parameter parameter : parameters) {
            dealMethodParameter(provider, methodArgs, parameter);
        }
        provider.setMethodArgs(methodArgs);
        return provider;
    }

    private static void dealMethodParameter(DataProvideDefinition provideDefinition,
                                            List<MethodArg> methodArgs, Parameter parameter) {
        DataConsumer dataConsumer = AnnotationUtils.findAnnotation(parameter, DataConsumer.class);
        InvokeParameter invokeParameter = AnnotationUtils.findAnnotation(parameter,InvokeParameter.class);
        Assert.isTrue(dataConsumer != null || invokeParameter != null,
                "Parameters must be added @InvokeParameter or @DataConsumer annotation");
        MethodArg methodArg = new MethodArg();
        if(dataConsumer != null) {
            String dataId = dataConsumer.id();
            Assert.isTrue(! StringUtils.isEmpty(dataId),"data id must be not null!");
            methodArg.setAnnotationKey(dataId);
            methodArg.setDependType(DependType.OTHER_MODEL);
            DataConsumeDefinition dataConsumeDefinition = new DataConsumeDefinition();
            dataConsumeDefinition.setClazz(parameter.getType());
            dataConsumeDefinition.setId(dataId);
            if(dataConsumer.dynamicParameters().length > 0) {
                Map<String, String> parameterKeyMap = new HashMap<>(dataConsumer.dynamicParameters().length);
                for (DynamicParameter dynamicParameter : dataConsumer.dynamicParameters()) {
                    parameterKeyMap.put(dynamicParameter.targetKey(),dynamicParameter.replacementKey());
                }
                dataConsumeDefinition.setDynamicParameterKeyMap(parameterKeyMap);
            }
            dataConsumeDefinition.setOriginalParameterName(parameter.getName());
            if(! dataConsumer.exceptionProcessingMethod().equals(ExceptionProcessingMethod.BY_DEFAULT)) {
                dataConsumeDefinition.setIgnoreException(
                        dataConsumer.exceptionProcessingMethod().equals(ExceptionProcessingMethod.IGNORE)
                );
            }
            provideDefinition.getDepends().add(dataConsumeDefinition);
        } else {
            methodArg.setAnnotationKey(invokeParameter.value());
            methodArg.setDependType(DependType.INVOKE_PARAM);
            InvokeParameterDefinition parameterDefinition = new InvokeParameterDefinition();
            parameterDefinition.setKey(invokeParameter.value());
            provideDefinition.getParams().add(parameterDefinition);
        }
        methodArg.setParameter(parameter);
        methodArgs.add(methodArg);
    }
}
