package io.github.lvyahui8.aggregator.assistant.psi.contributor;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PsiClassPattern;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.PsiParameterPattern;
import com.intellij.semantic.SemContributor;
import com.intellij.semantic.SemRegistrar;
import com.intellij.semantic.SemService;
import io.github.lvyahui8.aggregator.assistant.constants.DataConsumerConstant;
import io.github.lvyahui8.aggregator.assistant.psi.jam.consumer.DataConsumerJam;
import io.github.lvyahui8.aggregator.assistant.psi.jam.consumer.DataConsumerJamOld;
import io.github.lvyahui8.aggregator.assistant.psi.jam.parameter.DynamicParameterJam;
import org.jetbrains.annotations.NotNull;

/**
 * @version V0.1.0
 * @ClassName: DataConsumerSemContributor
 * @author: LiHaiQing
 * @date: 2020/4/20 22:11
 */
public class DataConsumerSemContributor  extends SemContributor {

    @Override
    public void registerSemProviders(@NotNull SemRegistrar registrar, @NotNull Project project) {
        SemService semService = SemService.getSemService(project);
        registerConsumerBeans(registrar, semService);
    }

    private static void registerConsumerBeans(SemRegistrar registrar, @NotNull SemService semService) {
        String dataConsumer = DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER;

        DataConsumerJamOld.META.register(registrar, PsiJavaPatterns.psiParameter()
                .withAnnotation(dataConsumer));



        PsiClassPattern nonAnnotationClass = PsiJavaPatterns.psiClass().nonAnnotationType().withoutModifiers("private");
        DynamicParameterJam.META.register(registrar, nonAnnotationClass.withAnnotation(dataConsumer));

        //OtherJam.PARAMETER_META.register(registrar, PsiJavaPatterns.psiParameter().withAnnotation(dataConsumer));

        DataConsumerJam.PARAMETER_META.register(registrar, PsiJavaPatterns.psiParameter().withAnnotation(dataConsumer));
        PsiParameterPattern pattern = PsiJavaPatterns.psiParameter().withAnnotation(dataConsumer);
        //PsiMethodPattern beanMethodPattern = (PsiJavaPatterns.psiMethod().withoutModifiers("private"))
        //    .constructor(false);
        //PsiMethodPattern pattern = beanMethodPattern.andNot(PsiJavaPatterns.psiMethod().withAnnotation(dataConsumer));
        registerDataConsumerBean(registrar, pattern, semService);

    }

    private static void registerDataConsumerBean(SemRegistrar registrar, PsiParameterPattern pattern,
                                                 SemService semService) {
        String dataConsumer = DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER;
//        SpringSemContributorUtil.registerMetaComponents(semService, registrar, pattern,
//                CustomDataConsumerJam.META_KEY, CustomDataConsumerJam.JAM_KEY, SpringSemContributorUtil.createFunction(
//                        CustomDataConsumerJam.JAM_KEY,
//                        CustomDataConsumerJam.class,
//                        SpringSemContributorUtil.getCustomMetaAnnotations(dataConsumer),
//                        (pair) -> new CustomDataConsumerJam(pair.first, pair.second),
//                        null,
//                        SpringAliasForUtils.
//                                getAnnotationMetaProducer(CustomDataConsumerJam.JAM_ANNOTATION_META_KEY,
//                                        DataConsumerJam.PARAMETER_META)));

    }


}