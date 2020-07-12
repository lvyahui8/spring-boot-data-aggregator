package io.github.lvyahui8.aggregator.assistant.psi.contributor;

import com.intellij.jam.reflect.JamMemberMeta;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.PsiMethodPattern;
import com.intellij.psi.PsiMethod;
import com.intellij.semantic.SemContributor;
import com.intellij.semantic.SemRegistrar;
import com.intellij.semantic.SemService;
import com.intellij.spring.model.aliasFor.SpringAliasForUtils;
import com.intellij.spring.model.jam.SpringSemContributorUtil;
import com.intellij.util.Consumer;
import io.github.lvyahui8.aggregator.assistant.constant.DataProviderConstant;
import io.github.lvyahui8.aggregator.assistant.psi.jam.provider.CustomDataProviderJam;
import io.github.lvyahui8.aggregator.assistant.psi.jam.provider.DataProviderJam;
import org.jetbrains.annotations.NotNull;


/**
 * @version V0.1.0
 * @ClassName: DataProviderSemContributor
 * @author: LiHaiQing
 * @date: 2020/4/17 21:37
 */
public class DataProviderSemContributor  extends SemContributor {

    @Override
    public void registerSemProviders(@NotNull SemRegistrar registrar, @NotNull Project project) {
        SemService semService = SemService.getSemService(project);
        registerDataProviderBeans(registrar, semService);
    }

    private static void registerDataProviderBeans(SemRegistrar registrar, @NotNull SemService semService) {
        String dataProvider = DataProviderConstant.DATA_AGGREGATOR_DATA_PROVIDER;
        PsiMethodPattern beanMethodPattern = (PsiJavaPatterns.psiMethod().withoutModifiers(new String[] {"private"}))
                .constructor(false);
        DataProviderJam.METHOD_META.register(registrar, beanMethodPattern.withAnnotation(dataProvider));
        PsiMethodPattern pattern = beanMethodPattern.andNot(PsiJavaPatterns.psiMethod().withAnnotation(dataProvider));
        registerDataProviderBean(registrar, pattern, semService);
    }

    private static void registerDataProviderBean(SemRegistrar registrar, PsiMethodPattern pattern,
                                                 @NotNull SemService semService) {
        String dataProvider = DataProviderConstant.DATA_AGGREGATOR_DATA_PROVIDER;

        SpringSemContributorUtil.registerMetaComponents(semService, registrar, pattern, CustomDataProviderJam.META_KEY,
                CustomDataProviderJam.JAM_KEY, SpringSemContributorUtil
                        .createFunction(CustomDataProviderJam.JAM_KEY, CustomDataProviderJam.class,
                                SpringSemContributorUtil.getCustomMetaAnnotations(dataProvider),
                                (pair) -> {
                                    return new CustomDataProviderJam(pair.first, (PsiMethod)pair.second);
                                }, (Consumer)null, SpringAliasForUtils
                                        .getAnnotationMetaProducer(CustomDataProviderJam.JAM_ANNOTATION_META_KEY,
                                                new JamMemberMeta[] {DataProviderJam.METHOD_META})));

    }
}