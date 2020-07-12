package io.github.lvyahui8.aggregator.assistant.psi.navigation;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.jam.model.common.CommonModelElement;
import com.intellij.openapi.diagnostic.Attachment;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.spring.SpringBundle;
import com.intellij.spring.gutter.SpringBeansPsiElementCellRenderer;
import com.intellij.spring.gutter.groups.SpringGutterIconBuilder;
import com.intellij.util.SmartList;
import io.github.lvyahui8.aggregator.assistant.utils.NavigationGutterIconBuilderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.*;

import javax.swing.*;
import java.util.Collection;

/**
 * @version V0.1.0
 * @ClassName: BaseNavigation
 * @author: LiHaiQing
 * @date: 2020/4/17 21:49
 */
public class BaseNavigation extends RelatedItemLineMarkerProvider {

    protected void annotateMethod(UMethod uMethod, PsiElement identifier,
                                  Collection<? super RelatedItemLineMarkerInfo> result) {
    }

    protected void annotateClass(Collection<? super RelatedItemLineMarkerInfo> result, UClass uClass,
                                 PsiElement identifier) {
    }

    protected void annotateClass(@NotNull Collection<? super RelatedItemLineMarkerInfo> result, UClass uClass) {
        PsiElement identifier = UElementKt.getSourcePsiElement(uClass.getUastAnchor());
        if (identifier != null) {
            this.annotateClass(result, uClass, identifier);
        }
    }

    protected void annotateMethod(UMethod method, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        PsiElement identifier = getAnchorSafe(method);
        if (identifier != null) {
            this.annotateMethod(method, identifier, result);
        }
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement psiElement,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        UElement element = UastUtils.getUParentForIdentifier(psiElement);
        if (element instanceof UMethod) {
            this.annotateMethod((UMethod)element, result);
        } else if (element instanceof UClass) {
            this.annotateClass(result, (UClass)element);
        }
    }

    protected static void addSpringJavaBeanGutterIcon(Collection<? super RelatedItemLineMarkerInfo> result,
                                                      PsiElement psiIdentifier,
                                                      NotNullLazyValue<Collection<? extends CommonModelElement>> targets,
                                                      Icon icon) {
        SpringGutterIconBuilder<CommonModelElement> builder = SpringGutterIconBuilder.createBuilder(icon,
                NavigationGutterIconBuilderUtil.COMMON_MODEL_ELEMENT_CONVERTER,
                NavigationGutterIconBuilderUtil.COMMON_MODEL_ELEMENT_GOTO_PROVIDER);
        builder.setTargets(targets)
                .setEmptyPopupText(SpringBundle.message("gutter.navigate.no.matching.beans", new Object[0]))
                .setPopupTitle(SpringBundle.message("spring.bean.class.navigate.choose.class.title", new Object[0]))
                .setCellRenderer(SpringBeansPsiElementCellRenderer.INSTANCE)
                .setTooltipText(SpringBundle.message("spring.bean.class.tooltip.navigate.declaration", new Object[0]));
        result.add(builder.createSpringGroupLineMarkerInfo(psiIdentifier));
    }

    @Nullable
    private static PsiElement getAnchorSafe(UMethod method) {
        PsiElement identifier = UElementKt.getSourcePsiElement(method.getUastAnchor());
        if (identifier == null) {
            return null;
        } else if (identifier.isValid() && identifier.getContainingFile() != null) {
            return identifier;
        } else {
            SmartList<Attachment> attachments = new SmartList();
            PsiElement sourcePsi = method.getSourcePsi();
            if (sourcePsi != null) {
                attachments.add(
                        new Attachment("uMethod.sourcePsi", sourcePsi.isValid() ? sourcePsi.getText() : "<invalid>"));
                PsiFile containingFile = sourcePsi.isValid() ? sourcePsi.getContainingFile() : null;
                if (containingFile != null) {
                    attachments.add(new Attachment(containingFile.getName(),
                            containingFile.isValid() ? containingFile.getText() : "<invalid file>"));
                }
            }

            System.out.println(
                    "invalid identifier came from " + method + " of " + method.getClass() + " is valid = " + identifier
                            .isValid() + " is physical = " + identifier.isPhysical());
            return null;
        }
    }
}