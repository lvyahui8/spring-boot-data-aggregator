package io.github.lvyahui8.aggregator.assistant.psi.jam.parameter;

import com.intellij.jam.JamElement;
import com.intellij.jam.JamService;
import com.intellij.jam.reflect.JamMemberMeta;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.semantic.SemKey;
import org.jetbrains.annotations.Nullable;

/**
 * @version V0.1.0
 * @ClassName: DynamicParameter
 * @author: LiHaiQing
 * @date: 2020/4/20 22:01
 */
public interface DynamicParameter extends JamElement {

    SemKey<DynamicParameter> DYNAMIC_PARAMETER_JAM_KEY = JamService.JAM_ELEMENT_KEY.subKey("DynamicParameter");
    SemKey<JamMemberMeta> DYNAMIC_PARAMETER_META_KEY = JamService.getMetaKey(DYNAMIC_PARAMETER_JAM_KEY);

    PsiClass getPsiElement();

    boolean isPsiValid();

    @Nullable
    PsiAnnotation getAnnotation();

}
