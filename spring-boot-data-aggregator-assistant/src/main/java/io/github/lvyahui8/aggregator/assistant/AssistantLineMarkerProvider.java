package io.github.lvyahui8.aggregator.assistant;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.impl.compiled.ClsTypeParameterImpl;
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl;
import io.github.lvyahui8.aggregator.assistant.constants.AssistantIcons;
import io.github.lvyahui8.aggregator.assistant.utils.AssistantUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName:  AssistantLineMarkerProvider.java
 * @author: LiHaiQing
 * @date:   2020/4/2 20:36
 * @version V1.0.0
 */
public class AssistantLineMarkerProvider extends RelatedItemLineMarkerProvider {

    static String DATA_PROVIDER = "@DataProvider";
//    @DataConsumer:
//    @InvokeParameter:

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof PsiJavaTokenImpl && element.getParent() instanceof PsiLiteralExpression) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element.getParent();
            String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
            if (value != null && value.contains(DATA_PROVIDER)) {
                Project project = element.getProject();
//                找到包含这个flag的所有文件
                final List<ClsTypeParameterImpl> targets = AssistantUtil.findTargets(project, DATA_PROVIDER);
                if (targets.size() > 0) {
//                    打标
                    NavigationGutterIconBuilder<PsiElement> builder =
                            NavigationGutterIconBuilder.create(AssistantIcons.data_consumer).
                                    setTargets(targets).
                                    setTooltipText("Navigate to a simple property");
                    result.add(builder.createLineMarkerInfo(element));
                }
            }
        }
    }

}
