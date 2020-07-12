package io.github.lvyahui8.aggregator.assistant.psi.jam.consumer;

import com.intellij.jam.JamStringAttributeElement;
import com.intellij.jam.reflect.JamAnnotationMeta;
import com.intellij.jam.reflect.JamAttributeMeta;
import com.intellij.jam.reflect.JamMemberMeta;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementRef;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.ref.AnnotationChildLink;
import com.intellij.semantic.SemKey;
import com.intellij.spring.model.aliasFor.SpringAliasFor;
import com.intellij.spring.model.aliasFor.SpringAliasForUtils;
import io.github.lvyahui8.aggregator.assistant.constant.DataConsumerConstant;

/**
 * @version V0.1.0
 * @ClassName: CustomDataConsumerJam
 * @author: LiHaiQing
 * @date: 2020/4/22 20:51
 */
public class CustomDataConsumerJam extends DataConsumerJam {

    public static final String DEBUG_NAME = "CustomDataConsumerJam";

    public static final SemKey<JamAnnotationMeta> JAM_ANNOTATION_META_KEY;
    public static final SemKey<CustomDataConsumerJam> JAM_KEY;
    public static final SemKey<JamMemberMeta<PsiParameter, CustomDataConsumerJam>> META_KEY;
    static {
        JAM_ANNOTATION_META_KEY = DATA_CONSUMER_ANNOTATION_KEY.subKey(DEBUG_NAME);
        JAM_KEY = DataConsumer.DATA_CONSUMER_JAM_KEY.subKey(DEBUG_NAME);
        META_KEY = DataConsumerJam.PARAMETER_META.getMetaKey().subKey(DEBUG_NAME);
    }

    private final PsiElementRef<PsiAnnotation> myPsiAnnotation;
    private final AnnotationChildLink myAnnotationChildLink;

    public CustomDataConsumerJam(String annotation, PsiMember psiMember) {
        super(psiMember);
        this.myAnnotationChildLink = new AnnotationChildLink(annotation);
        this.myPsiAnnotation = this.myAnnotationChildLink.createChildRef(this.getPsiElement());
    }

    @Override
    protected JamStringAttributeElement<String> getBeanIdAttributeValue() {
        SpringAliasFor nameAttributeAliasFor = this.getIdAttributeAliasFor();
        if (nameAttributeAliasFor != null) {
            return JamAttributeMeta.singleString(nameAttributeAliasFor.getMethodName()).getJam(
                    this.myPsiAnnotation);
        } else {
            SpringAliasFor valueAttributeAliasFor = this.getValueAttributeAliasFor();
            return valueAttributeAliasFor != null ? JamAttributeMeta.singleString(
                    valueAttributeAliasFor.getMethodName()).getJam(this.myPsiAnnotation)
                    : super.getBeanIdAttributeValue();
        }
    }

    private SpringAliasFor getIdAttributeAliasFor() {
        return SpringAliasForUtils.findAliasFor(this.getPsiElement(),
                this.myAnnotationChildLink.getAnnotationQualifiedName(), DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER,
                DataConsumerConstant.ID_ATTRIBUTE);
    }

    private SpringAliasFor getValueAttributeAliasFor() {
        return SpringAliasForUtils.findAliasFor(this.getPsiElement(),
                this.myAnnotationChildLink.getAnnotationQualifiedName(), DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER,
                DataConsumerConstant.VALUE_ATTRIBUTE);
    }

    @Override
    public boolean isPsiValid() {
        return this.getPsiElement().isValid();
    }

    @Override
    public PsiAnnotation getPsiAnnotation() {
        return this.myPsiAnnotation.getPsiElement();
    }
}