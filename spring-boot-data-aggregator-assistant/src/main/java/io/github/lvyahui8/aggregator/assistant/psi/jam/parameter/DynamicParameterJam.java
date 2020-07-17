package io.github.lvyahui8.aggregator.assistant.psi.jam.parameter;

import com.intellij.jam.annotations.JamPsiConnector;
import com.intellij.jam.reflect.JamAnnotationMeta;
import com.intellij.jam.reflect.JamAttributeMeta;
import com.intellij.jam.reflect.JamClassMeta;
import com.intellij.jam.reflect.JamStringAttributeMeta;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementRef;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.semantic.SemKey;
import io.github.lvyahui8.aggregator.assistant.constants.DynamicParameterConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @version V0.1.0
 * @ClassName: JamDynamicParameter
 * @author: LiHaiQing
 * @date: 2020/4/20 22:03
 */
public class DynamicParameterJam implements DynamicParameter {
    private static final String DEBUG_NAME = "DynamicParameterJam";

    public static final SemKey<DynamicParameterJam> REPEATABLE_ANNOTATION_JAM_KEY =
            DynamicParameter.DYNAMIC_PARAMETER_JAM_KEY.subKey(DEBUG_NAME);

    public static final JamAnnotationMeta ANNOTATION_META =
            new JamAnnotationMeta(DynamicParameterConstant.DATA_AGGREGATOR_DYNAMIC_PARAMETER);

    /***
     *  @DynamicParameter annotation meta
     */
    private static final JamStringAttributeMeta.Single<String> TARGET_KEY_ATTRIBUTE_META
            = JamAttributeMeta.singleString(DynamicParameterConstant.TARGET_KEY_ATTRIBUTE);
    private static final JamStringAttributeMeta.Single<String> REPLACEMENT_KEY_ATTRIBUTE_META
            = JamAttributeMeta.singleString(DynamicParameterConstant.REPLACEMENT_KEY_ATTRIBUTE);

    public static final JamClassMeta<DynamicParameterJam> META
            = new JamClassMeta(null, DynamicParameterJam.class, REPEATABLE_ANNOTATION_JAM_KEY);

    static {
        META.addAnnotation(ANNOTATION_META);
        ANNOTATION_META.addAttribute(TARGET_KEY_ATTRIBUTE_META);
        ANNOTATION_META.addAttribute(REPLACEMENT_KEY_ATTRIBUTE_META);
    }

    private final PsiElementRef<PsiAnnotation> myPsiAnnotation;
    private final PsiClass myPsiClass;

    public DynamicParameterJam(@NotNull PsiClass psiClass) {
        myPsiClass = psiClass;
        myPsiAnnotation = ANNOTATION_META.getAnnotationRef(psiClass);
    }

    public DynamicParameterJam(@NotNull PsiAnnotation annotation) {
        myPsiClass = PsiTreeUtil.getParentOfType(annotation, PsiClass.class, true);
        myPsiAnnotation = PsiElementRef.real(annotation);
    }

    @Override
    @NotNull
    @JamPsiConnector
    public PsiClass getPsiElement() {
        return myPsiClass;
    }

    @Override
    public boolean isPsiValid() {
        return myPsiClass.isValid();
    }

    @Override
    @Nullable
    public PsiAnnotation getAnnotation() {
        return ANNOTATION_META.getAnnotation(getPsiElement());
    }
}