package io.github.lvyahui8.aggregator.assistant.psi.jam.provider;

import com.intellij.jam.JamPomTarget;
import com.intellij.jam.JamService;
import com.intellij.jam.JamStringAttributeElement;
import com.intellij.jam.reflect.*;
import com.intellij.psi.PsiAnchor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTarget;
import com.intellij.semantic.SemKey;
import com.intellij.spring.model.jam.JamPsiMemberSpringBean;
import com.intellij.spring.model.jam.javaConfig.SpringJavaBean;
import io.github.lvyahui8.aggregator.assistant.constant.DataProviderConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @version V0.1.0
 * @ClassName: DataProviderBean
 * @author: LiHaiQing
 * @date: 2020/4/17 21:42
 */
public abstract class DataProviderJam extends SpringJavaBean {

    private static final String DEBUG_NAME = "DataProviderJam";

    public static final SemKey<JamAnnotationMeta> DATA_PROVIDER_ANNOTATION_KEY = JamService
            .ANNO_META_KEY.subKey(DEBUG_NAME);
    public static final SemKey<DataProviderJam> DATA_PROVIDER_JAM_KEY =
            JamPsiMemberSpringBean.PSI_MEMBER_SPRING_BEAN_JAM_KEY.subKey(DEBUG_NAME);
    public static final JamMethodMeta<DataProviderJam> METHOD_META =
            new JamMethodMeta(null, DataProviderJam.class, DATA_PROVIDER_JAM_KEY);

    /***
     * @DataProvider annotation meta
     */
    private static final JamStringAttributeMeta.Collection<String> ID_ATTRIBUTE_META = JamAttributeMeta
            .collectionString(DataProviderConstant.ID_ATTRIBUTE);
    private static final JamStringAttributeMeta.Collection<String> VALUE_ATTRIBUTE_META = JamAttributeMeta
            .collectionString(DataProviderConstant.VALUE_ATTRIBUTE);
    private static final JamNumberAttributeMeta.Single<Integer> TIMEOUT_META = JamAttributeMeta.singleInteger(
            DataProviderConstant.TIME_OUT_ATTRIBUTE);
    private static final JamBooleanAttributeMeta IDEMPOTENT_META = JamAttributeMeta.singleBoolean(
            DataProviderConstant.IDEMPOTENT_ATTRIBUTE, true);

    public static final JamAnnotationMeta ANNOTATION_META =
            new JamAnnotationMeta(DataProviderConstant.DATA_AGGREGATOR_DATA_PROVIDER)
                    .addAttribute(ID_ATTRIBUTE_META)
                    .addAttribute(VALUE_ATTRIBUTE_META)
                    .addAttribute(TIMEOUT_META)
                    .addAttribute(IDEMPOTENT_META);

    static {
        METHOD_META.addAnnotation(ANNOTATION_META);
        METHOD_META.addPomTargetProducer((dataProviderJam, consumer) -> {
            PsiTarget psiTarget = dataProviderJam.getPsiTarget();
            if (psiTarget != null) {
                consumer.consume(psiTarget);
            }
        });
    }

    private final PsiAnchor myAnchor;

    public DataProviderJam(@NotNull PsiMethod psiMethod) {
        this.myAnchor = PsiAnchor.create(psiMethod);
    }

    @Override
    public PsiAnnotation getPsiAnnotation() {
        return ANNOTATION_META.getAnnotation(getPsiElement());
    }

    @NotNull
    @Override
    public PsiMethod getPsiElement() {
        return (PsiMethod) myAnchor.retrieve();
    }

    @Nullable
    public PsiTarget getPsiTarget() {
        final List<JamStringAttributeElement<String>> beanIdAttributeValue = getBeanIdAttributeValue();
        if (CollectionUtils.isEmpty(beanIdAttributeValue)) {
            return null;
        } else {
            return new JamPomTarget(this, beanIdAttributeValue.get(0));
        }
    }

    protected List<JamStringAttributeElement<String>> getBeanIdAttributeValue() {
        List<JamStringAttributeElement<String>> nameAttributes = getIdAttributeValue();
        return nameAttributes.isEmpty() ? getValueAttributeValue() : nameAttributes;
    }

    @NotNull
    private List<JamStringAttributeElement<String>> getIdAttributeValue() {
        return ANNOTATION_META.getAttribute(getPsiElement(), ID_ATTRIBUTE_META);
    }

    private List<JamStringAttributeElement<String>> getValueAttributeValue() {
        return ANNOTATION_META.getAttribute(getPsiElement(), VALUE_ATTRIBUTE_META);
    }
}