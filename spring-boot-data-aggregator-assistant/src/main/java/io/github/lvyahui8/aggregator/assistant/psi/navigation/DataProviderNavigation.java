package io.github.lvyahui8.aggregator.assistant.psi.navigation;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.jam.JamService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PropertyUtilBase;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.SpringBundle;
import com.intellij.spring.gutter.groups.SpringGutterIconBuilder;
import com.intellij.spring.impl.SpringAutoConfiguredModels;
import com.intellij.spring.model.SpringBeanPointer;
import com.intellij.spring.model.utils.SpringCommonUtils;
import com.intellij.spring.model.utils.SpringModelUtils;
import io.github.lvyahui8.aggregator.assistant.enums.IconEnum;
import io.github.lvyahui8.aggregator.assistant.psi.jam.provider.DataProviderJam;
import io.github.lvyahui8.aggregator.assistant.utils.DataProviderUtil;
import io.github.lvyahui8.aggregator.assistant.utils.NavigationGutterIconBuilderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version V0.1.0
 * @ClassName: DataProviderLineMarkerProvider
 * @author: LiHaiQing
 * @date: 2020/4/17 22:07
 */
public class DataProviderNavigation extends BaseNavigation {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement psiElement,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        UElement element = UastUtils.getUParentForIdentifier(psiElement);
        if (element instanceof UMethod) {
            annotateMethod((UMethod)element, result);
        } else if (element instanceof UField) {
            annotateField(result, (UField)element);
        } else if (element instanceof UParameter) {
            UElement parent = element.getUastParent();
            if (parent instanceof UMethod) {
                processAnnotatedMethod((UMethod)parent, psiElement, result);
            }
        } else {
            if (element instanceof UReferenceExpression) {
                element = element.getUastParent();
            }
            if (element instanceof UAnnotation) {
                annotateAnnotation(psiElement, result, (UAnnotation)element);
            }

        }
    }

    private static void annotateField(Collection<? super RelatedItemLineMarkerInfo> result, UField ufield) {
        PsiElement identifier = UElementKt.getSourcePsiElement(ufield.getUastAnchor());
        if (identifier != null) {
            PsiField field = UElementKt.getAsJavaPsiElement(ufield, PsiField.class);
            if (field != null) {
                if (DataProviderUtil.isDataProviderByAnnotation(field)) {
                    CommonSpringModel processor = DataProviderUtil.getProcessingSpringModel(field.getContainingClass());
                    if (processor != null) {
                        processVariable(field, result, processor, identifier, field.getType());
                    }
                }
            }
        }
    }

    private static void processAnnotatedMethod(UMethod uMethod, PsiElement identifier,
                                               Collection<? super RelatedItemLineMarkerInfo> result) {
        PsiMethod method = uMethod.getJavaPsi();
        if (DataProviderUtil.isInjectionPoint(method)) {
            CommonSpringModel model = DataProviderUtil.getProcessingSpringModel(method.getContainingClass());
            if (model != null) {
                for (UParameter parameter : uMethod.getUastParameters()) {
                    if (identifier == UElementKt.getSourcePsiElement(parameter.getUastAnchor())) {
                        PsiParameter psiParameter = UElementKt.getAsJavaPsiElement(parameter, PsiParameter.class);
                        processVariable(psiParameter, result, model, identifier, parameter.getType());
                        break;
                    }
                }
            }
        }

    }

    private static boolean processVariable(PsiModifierListOwner variable,
                                           @Nullable Collection<? super RelatedItemLineMarkerInfo> result,
                                           @NotNull CommonSpringModel model, @NotNull PsiElement identifier,
                                           @NotNull PsiType type) {
        Collection<SpringBeanPointer> list = DataProviderUtil.getDataProviderBeansFor(variable, getDataProviderType(type),
                model);
        if (!list.isEmpty()) {
            if (result != null) {
//                TODO autowired 要改
                NavigationGutterIconBuilderUtil.addAutowiredBeansGutterIcon(list, result, identifier);
            }
            return true;
        } else {
            return false;
        }
    }

    @NotNull
    private static PsiType getDataProviderType(@NotNull PsiType type) {
        if (DataProviderUtil.isJavaUtilOptional(type)) {
            PsiType optionalType = DataProviderUtil.getOptionalType(type);
            if (optionalType != null) {
                return optionalType;
            }
        }
        return type;
    }

    private static void annotateAnnotation(@NotNull PsiElement psiElement,
                                           @NotNull Collection<? super RelatedItemLineMarkerInfo> result,
                                           UAnnotation uAnnotation) {
        UElement annotatedElement = uAnnotation.getUastParent();
        if (annotatedElement instanceof UMethod) {
            PsiMethod method = ((UMethod)annotatedElement).getJavaPsi();
            if (method.isConstructor() || PropertyUtilBase.isSimplePropertySetter(method)) {
                return;
            }
            DataProviderJam dataProviderJam = getStereotypeBean(method);
            if (dataProviderJam != null) {
                UAnnotation annotationFromBean = UastContextKt.toUElement(dataProviderJam.getPsiAnnotation(),
                        UAnnotation.class);
                if (Objects.equals(annotationFromBean, uAnnotation)) {
                    addDataProviderCandidatesBeanGutterIcon(result, method, psiElement);
                }
            }
        }
    }

    private static DataProviderJam getStereotypeBean(@NotNull PsiMethod method) {
        final JamService jamService = JamService.getJamService(method.getProject());
        return jamService.getJamElement(DataProviderJam.DATA_PROVIDER_JAM_KEY, method);
    }

    private static void addDataProviderCandidatesBeanGutterIcon(
            Collection<? super RelatedItemLineMarkerInfo> result, PsiMethod method, PsiElement identifier) {
        SpringGutterIconBuilder<PsiElement> builder = SpringGutterIconBuilder
                .createBuilder(IconEnum.data_provider);
        builder.setPopupTitle(SpringBundle.message("gutter.choose.autowired.candidates.title", new Object[0]))
                .setEmptyPopupText(SpringBundle.message("gutter.navigate.no.matching.autowired.candidates", new Object[0]))
                .setTooltipText(SpringBundle.message("gutter.navigate.to.autowired.candidates.title", new Object[0]))
                .setTargets(new NotNullLazyValue<Collection<? extends PsiElement>>() {
                    @Override
                    @NotNull
                    protected Collection<? extends PsiElement> compute() {
                        if (!method.isValid()) {
                            return Collections.emptySet();
                        } else {
                            PsiType type = method.getReturnType();
                            if (type == null) {
                                return Collections.emptySet();
                            } else {
                                Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(method);
                                if (moduleForPsiElement != null) {
                                    return DataProviderUtil.getDataProviderMembers(type, moduleForPsiElement, method);
                                } else {
                                    Set<PsiModifierListOwner> members = new LinkedHashSet();
                                    Iterator iterator = getRelatedSpringModules(method).iterator();
                                    while (iterator.hasNext()) {
                                        Module module = (Module)iterator.next();
                                        members.addAll(DataProviderUtil.getDataProviderMembers(type, module, method));
                                    }
                                    return members;
                                }
                            }
                        }
                    }
                });
        result.add(builder.createSpringRelatedMergeableLineMarkerInfo(identifier));
    }

    private static Set<Module> getRelatedSpringModules(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();
        if (psiFile == null) {
            return Collections.emptySet();
        } else {
            VirtualFile virtualFile = psiFile.getOriginalFile().getVirtualFile();
            if (virtualFile == null) {
                return Collections.emptySet();
            } else {
                ProjectFileIndex fileIndex = ProjectRootManager.getInstance(element.getProject()).getFileIndex();
                if (!fileIndex.isLibraryClassFile(virtualFile) && !fileIndex.isInLibrarySource(virtualFile)) {
                    return Collections.emptySet();
                } else {
                    boolean allowAutoConfig = SpringAutoConfiguredModels.isAllowAutoConfiguration(element.getProject());
                    return fileIndex.getOrderEntriesForFile(virtualFile)
                            .stream()
                            .map(OrderEntry::getOwnerModule).filter((module) -> {
                                return SpringCommonUtils.hasSpringFacet(module) || allowAutoConfig && SpringModelUtils
                                        .getInstance().hasAutoConfiguredModels(module);
                            }).collect(Collectors.toSet());
                }
            }
        }
    }

    @Override
    public String getName() {
        return "DataProvider";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IconEnum.data_provider;
    }

    @Override
    public String getId() {
        return "DataProviderLineMarkerProvider";
    }
}