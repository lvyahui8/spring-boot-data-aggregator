package io.github.lvyahui8.aggregator.assistant.utils;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.DomGotoRelatedItem;
import com.intellij.jam.model.common.CommonModelElement;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.psi.PsiElement;
import com.intellij.spring.SpringBundle;
import com.intellij.spring.gutter.SpringBeansPsiElementCellRenderer;
import com.intellij.spring.gutter.groups.SpringGutterIconBuilder;
import com.intellij.spring.model.CommonSpringBean;
import com.intellij.spring.model.SpringBeanPointer;
import com.intellij.spring.model.xml.DomSpringBean;
import com.intellij.util.NotNullFunction;
import io.github.lvyahui8.aggregator.assistant.enums.IconEnum;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @version V0.1.0
 * @ClassName: NavigationGutterIconBuilderUtil
 * @author: LiHaiQing
 * @date: 2020/4/17 21:54
 */
public class NavigationGutterIconBuilderUtil {

    private NavigationGutterIconBuilderUtil() {
    }

    public static final NotNullFunction<SpringBeanPointer, Collection<? extends PsiElement>> BEAN_POINTER_CONVERTER
            = (pointer) -> !pointer.isValid() ? Collections.emptySet() : Collections.singleton(
            pointer.getSpringBean().getIdentifyingPsiElement());

    public static final NotNullFunction<CommonModelElement, Collection<? extends PsiElement>>
            COMMON_MODEL_ELEMENT_CONVERTER = (modelElement) -> {
        return Collections.singleton(modelElement.getIdentifyingPsiElement());
    };
    private static final String AUTOWIRED_DEPENDENCIES_GOTO_GROUP = "Autowired dependencies";
    public static final NotNullFunction<SpringBeanPointer, Collection<? extends GotoRelatedItem>>
            AUTOWIRED_BEAN_POINTER_GOTO_PROVIDER = (pointer) -> {
        CommonSpringBean bean = pointer.getSpringBean();
        if (bean instanceof DomSpringBean) {
            return Collections.singletonList(new DomGotoRelatedItem((DomSpringBean)bean, "Autowired dependencies"));
        } else {
            PsiElement element = bean.getIdentifyingPsiElement();
            return element != null ? Collections.singletonList(new GotoRelatedItem(element, "Autowired dependencies"))
                    : Collections.emptyList();
        }
    };

    public static final NotNullFunction<CommonModelElement, Collection<? extends GotoRelatedItem>>
            COMMON_MODEL_ELEMENT_GOTO_PROVIDER = (modelElement) -> {
        if (modelElement instanceof DomSpringBean) {
            return Collections.singletonList(new DomGotoRelatedItem((DomSpringBean)modelElement));
        } else {
            PsiElement element = modelElement.getIdentifyingPsiElement();
            return element != null ? Collections.singletonList(new GotoRelatedItem(element)) : Collections.emptyList();
        }
    };

    public static void addAutowiredBeansGutterIcon(@NotNull Collection<? extends SpringBeanPointer> collection,
                                                   @NotNull Collection<? super RelatedItemLineMarkerInfo> holder,
                                                   @NotNull PsiElement identifier) {
        addAutowiredBeansGutterIcon(collection, holder, identifier,
                SpringBundle.message("navigate.to.autowired.dependencies", new Object[0]));
    }

    static void addAutowiredBeansGutterIcon(@NotNull Collection<? extends SpringBeanPointer> collection,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo> result,
                                            @NotNull PsiElement identifier, @NotNull String tooltipText) {
        List<SpringBeanPointer> sorted = new ArrayList(collection);
        Collections.sort(sorted, SpringBeanPointer.DISPLAY_COMPARATOR);
        SpringGutterIconBuilder<SpringBeanPointer> builder = SpringGutterIconBuilder
                .createBuilder(IconEnum.DATA_PROVIDER.getIcon(), BEAN_POINTER_CONVERTER,
                        AUTOWIRED_BEAN_POINTER_GOTO_PROVIDER);
        builder.setPopupTitle(SpringBundle.message("spring.bean.class.navigate.choose.class.title", new Object[0]))
                .setCellRenderer(SpringBeansPsiElementCellRenderer.INSTANCE)
                .setTooltipText(tooltipText).setTargets(sorted);
        result.add(builder.createSpringRelatedMergeableLineMarkerInfo(identifier));
    }
}