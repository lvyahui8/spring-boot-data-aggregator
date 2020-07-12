package io.github.lvyahui8.aggregator.assistant.psi.jam.consumer;

import com.intellij.jam.JamPomTarget;
import com.intellij.jam.JamService;
import com.intellij.jam.JamStringAttributeElement;
import com.intellij.jam.reflect.*;
import com.intellij.psi.*;
import com.intellij.semantic.SemKey;
import io.github.lvyahui8.aggregator.assistant.constant.DataConsumerConstant;
import io.github.lvyahui8.aggregator.assistant.psi.jam.parameter.DynamicParameterJam;

import java.util.Objects;

/**
 * @version V0.1.0
 * @ClassName: DataConsumerJam
 * @author: LiHaiQing
 * @date: 2020/4/22 20:50
 */
public abstract class DataConsumerJam implements DataConsumer {

    private static final String DEBUG_NAME = "DataConsumerJam";

    public static final SemKey<JamAnnotationMeta> DATA_CONSUMER_ANNOTATION_KEY = JamService.ANNO_META_KEY.subKey(
            DEBUG_NAME);

    //public static final JamMethodMeta<DataConsumerJam> METHOD_META = new JamMethodMeta(DataConsumerJam.class);
    public static final JamParameterMeta<DataConsumerJam> PARAMETER_META = new JamParameterMeta(DataConsumerJam.class);

    /***
     * @DataProvider annotation meta
     */
    private static final JamStringAttributeMeta.Single<String> ID_ATTRIBUTE_META = JamAttributeMeta
            .singleString(DataConsumerConstant.ID_ATTRIBUTE);
    private static final JamStringAttributeMeta.Single<String> VALUE_ATTRIBUTE_META = JamAttributeMeta
            .singleString(DataConsumerConstant.VALUE_ATTRIBUTE);
    public static final JamAnnotationAttributeMeta.Collection<DynamicParameterJam> DYNAMIC_PARAMETERS_META =
            JamAttributeMeta.annoCollection(DataConsumerConstant.DYNAMIC_PARAMETERS_ATTRIBUTE,
                    DynamicParameterJam.ANNOTATION_META,
                    DynamicParameterJam.class);

    public static final JamAnnotationMeta ANNOTATION_META =
            new JamAnnotationMeta(DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER)
                    .addAttribute(ID_ATTRIBUTE_META)
                    .addAttribute(VALUE_ATTRIBUTE_META)
                    .addAttribute(DYNAMIC_PARAMETERS_META);

    static {
        PARAMETER_META.addAnnotation(ANNOTATION_META)
                .addPomTargetProducer((dataConsumerJam, pomTargetConsumer) -> {
                    PsiTarget psiTarget = dataConsumerJam.getPsiTarget();
                    System.out.println(psiTarget.getNavigationElement().getText());
                    if (psiTarget != null) {
                        pomTargetConsumer.consume(psiTarget);
                    }
                });
        //METHOD_META.addAnnotation(ANNOTATION_META)
        //    .addPomTargetProducer((dataConsumerJam, pomTargetConsumer) -> {
        //        PsiTarget psiTarget = dataConsumerJam.getPsiTarget();
        //        if (psiTarget != null) {
        //            pomTargetConsumer.consume(psiTarget);
        //        }
        //    });
    }

    private final PsiAnchor myAnchor;

    public DataConsumerJam(PsiMember psiMember) {
        this.myAnchor = PsiAnchor.create(psiMember);
    }

    @Override
    public PsiAnnotation getPsiAnnotation() {
        return ANNOTATION_META.getAnnotation(getPsiElement());
    }

    @Override
    public PsiMember getPsiElement() {
        System.out.println("---DataConsumerJam---");
        System.out.println(myAnchor.retrieve().getText());
        System.out.println("---DataConsumerJam---");
        return (PsiMethod) Objects.requireNonNull(myAnchor.retrieve());
    }

    public PsiTarget getPsiTarget() {
        final JamStringAttributeElement<String> beanIdAttributeValue = getBeanIdAttributeValue();
        if (beanIdAttributeValue == null) {
            return null;
        } else {
            return new JamPomTarget(this, beanIdAttributeValue);
        }
    }

    protected JamStringAttributeElement<String> getBeanIdAttributeValue() {
        JamStringAttributeElement<String> idAttribute = getIdAttributeValue();
        return idAttribute == null ? getValueAttributeValue() : idAttribute;
    }

    private JamStringAttributeElement<String> getIdAttributeValue() {
        return ANNOTATION_META.getAttribute(getPsiElement(), ID_ATTRIBUTE_META);
    }

    private JamStringAttributeElement<String> getValueAttributeValue() {
        return ANNOTATION_META.getAttribute(getPsiElement(), VALUE_ATTRIBUTE_META);
    }

}