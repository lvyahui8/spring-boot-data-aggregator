package io.github.lvyahui8.aggregator.assistant.psi.jam.provider;

import com.intellij.jam.JamStringAttributeElement;
import com.intellij.jam.reflect.JamAnnotationMeta;
import com.intellij.jam.reflect.JamAttributeMeta;
import com.intellij.jam.reflect.JamMemberMeta;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementRef;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.ref.AnnotationChildLink;
import com.intellij.semantic.SemKey;
import com.intellij.spring.model.aliasFor.SpringAliasFor;
import com.intellij.spring.model.aliasFor.SpringAliasForUtils;
import io.github.lvyahui8.aggregator.assistant.constants.DataProviderConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @version V0.1.0
 * @ClassName: CustomDataProviderBean
 * @author: LiHaiQing
 * @date: 2020/4/17 21:44
 */
public class CustomDataProviderJam extends DataProviderJam {

    public static final SemKey<JamAnnotationMeta> JAM_ANNOTATION_META_KEY;
    public static final SemKey<JamMemberMeta<PsiMethod, CustomDataProviderJam>> META_KEY;
    public static final SemKey<CustomDataProviderJam> JAM_KEY;

    @Override
    public boolean isValid() {
        return this.getPsiElement().isValid();
    }

    @Override
    @Nullable
    public PsiAnnotation getPsiAnnotation() {
        return this.myPsiAnnotation.getPsiElement();
    }

    static {
        JAM_ANNOTATION_META_KEY = DATA_PROVIDER_ANNOTATION_KEY.subKey("CustomDataProviderBean", new SemKey[0]);
        META_KEY = DataProviderJam.METHOD_META.getMetaKey().subKey("CustomDataProviderBean", new SemKey[0]);
        JAM_KEY = DataProviderJam.DATA_PROVIDER_JAM_KEY.subKey("CustomDataProviderBean", new SemKey[0]);
    }

    private final PsiElementRef<PsiAnnotation> myPsiAnnotation;
    private final AnnotationChildLink myAnnotationChildLink;

    public CustomDataProviderJam(@NotNull String annotation, @NotNull PsiMethod psiMethod) {
        super(psiMethod);
        this.myAnnotationChildLink = new AnnotationChildLink(annotation);
        this.myPsiAnnotation = this.myAnnotationChildLink.createChildRef(this.getPsiElement());
    }

    @Override
    protected List<JamStringAttributeElement<String>> getBeanIdAttributeValue() {
        SpringAliasFor nameAttributeAliasFor = this.getIdAttributeAliasFor();
        if (nameAttributeAliasFor != null) {
            return JamAttributeMeta.collectionString(nameAttributeAliasFor.getMethodName()).getJam(
                    this.myPsiAnnotation);
        } else {
            SpringAliasFor valueAttributeAliasFor = this.getValueAttributeAliasFor();
            return valueAttributeAliasFor != null ? JamAttributeMeta.collectionString(
                    valueAttributeAliasFor.getMethodName()).getJam(this.myPsiAnnotation)
                    : super.getBeanIdAttributeValue();
        }
    }


    @Nullable
    private SpringAliasFor getIdAttributeAliasFor() {
        return SpringAliasForUtils.findAliasFor(this.getPsiElement(),
                this.myAnnotationChildLink.getAnnotationQualifiedName(), DataProviderConstant.DATA_AGGREGATOR_DATA_PROVIDER,
                DataProviderConstant.ID_ATTRIBUTE);
    }

    @Nullable
    private SpringAliasFor getValueAttributeAliasFor() {
        return SpringAliasForUtils.findAliasFor(this.getPsiElement(),
                this.myAnnotationChildLink.getAnnotationQualifiedName(), DataProviderConstant.DATA_AGGREGATOR_DATA_PROVIDER,
                DataProviderConstant.VALUE_ATTRIBUTE);
    }

}