package io.github.lvyahui8.aggregator.assistant.psi.jam.consumer;

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
import io.github.lvyahui8.aggregator.assistant.constant.DataConsumerConstant;
import io.github.lvyahui8.aggregator.assistant.psi.jam.parameter.DynamicParameterJam;
import org.jetbrains.annotations.NotNull;

/**
 * @version V0.1.0
 * @ClassName: DataConsumerBean
 * @author: LiHaiQing
 * @date: 2020/4/20 22:13
 */
public abstract class DataConsumerJamOld extends SpringJavaBean {
    private static String DEBUG_NAME = "DataConsumerJam";

    public static final SemKey<JamAnnotationMeta> DATA_CONSUMER_ANNOTATION_KEY = JamService.ANNO_META_KEY.subKey(
            DEBUG_NAME);
    public static final SemKey<DataConsumerJamOld> DATA_CONSUMER_JAM_KEY =
            JamPsiMemberSpringBean.PSI_MEMBER_SPRING_BEAN_JAM_KEY.subKey(DEBUG_NAME);

    public static final JamParameterMeta<DataConsumerJamOld> META = new JamParameterMeta(DataConsumerJamOld.class);

    /***
     * @DataProvider annotation meta
     */
    private static final JamStringAttributeMeta.Single<String> ID_ATTRIBUTE_META = JamAttributeMeta
            .singleString(DataConsumerConstant.ID_ATTRIBUTE);
    private static final JamStringAttributeMeta.Single<String> VALUE_ATTRIBUTE_META = JamAttributeMeta
            .singleString(DataConsumerConstant.VALUE_ATTRIBUTE);
    public static final JamAnnotationAttributeMeta.Collection<DynamicParameterJam> DYNAMIC_PARAMETERS_META =
            JamAttributeMeta.annoCollection(DataConsumerConstant.DYNAMIC_PARAMETERS_ATTRIBUTE, DynamicParameterJam.ANNOTATION_META,
                    DynamicParameterJam.class);

    public static final JamAnnotationMeta ANNOTATION_META =
            new JamAnnotationMeta(DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER)
                    .addAttribute(ID_ATTRIBUTE_META)
                    .addAttribute(VALUE_ATTRIBUTE_META)
                    .addAttribute(DYNAMIC_PARAMETERS_META);

    static {
        META.addAnnotation(ANNOTATION_META);
        META.addPomTargetProducer((variable, pomTargetConsumer) -> {
            pomTargetConsumer.consume(variable.getPomTarget());
        });
    }

    private final PsiAnchor myAnchor;

    public DataConsumerJamOld(@NotNull PsiMethod psiMethod) {
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


    public PsiTarget getPomTarget() {
        JamStringAttributeElement<String> valueAttribute = ANNOTATION_META.getAttribute(this.getPsiElement(), ID_ATTRIBUTE_META);
        if (valueAttribute.getPsiLiteral() != null) {
            return new DataConsumerPomTarget(valueAttribute);
        } else {
            JamStringAttributeElement<String> nameAttribute = ANNOTATION_META.getAttribute(this.getPsiElement(), VALUE_ATTRIBUTE_META);
            return (nameAttribute.getPsiLiteral() != null ? new DataConsumerPomTarget(nameAttribute) : this.getPsiElement());
        }
    }

    private class DataConsumerPomTarget extends JamPomTarget {
        private DataConsumerPomTarget(JamStringAttributeElement<String> valueAttribute) {
            super(DataConsumerJamOld.this, valueAttribute);
        }

        @Override
        public JamPomTarget setName(@NotNull String newName) {
            return this;
        }
    }

}