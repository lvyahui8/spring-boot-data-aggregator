package io.github.lvyahui8.aggregator.assistant.utils;

import com.intellij.codeInsight.AnnotationTargetUtil;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.jam.model.util.JamCommonUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.*;
import com.intellij.psi.PsiAnnotation.TargetType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedMembersSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.SpringModelVisitorUtils;
import com.intellij.spring.contexts.model.CombinedSpringModel;
import com.intellij.spring.contexts.model.CombinedSpringModelImpl;
import com.intellij.spring.contexts.model.SpringModel;
import com.intellij.spring.facet.SpringFileSet;
import com.intellij.spring.model.CommonSpringBean;
import com.intellij.spring.model.SpringBeanPointer;
import com.intellij.spring.model.SpringModelSearchParameters;
import com.intellij.spring.model.SpringObjectFactoryEffectiveTypeProvider;
import com.intellij.spring.model.jam.qualifiers.SpringJamQualifier;
import com.intellij.spring.model.jam.utils.JamAnnotationTypeUtil;
import com.intellij.spring.model.utils.SpringBeanCoreUtils;
import com.intellij.spring.model.utils.SpringModelSearchers;
import com.intellij.spring.model.utils.SpringModelUtils;
import com.intellij.spring.model.xml.beans.SpringBean;
import com.intellij.util.Query;
import com.intellij.util.SmartList;
import io.github.lvyahui8.aggregator.assistant.constants.DataConsumerConstant;
import io.github.lvyahui8.aggregator.assistant.constants.DataProviderConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @version V0.1.0
 * @ClassName: DataProviderUtil
 * @author: LiHaiQing
 * @date: 2020/4/17 22:10
 */
public class DataProviderUtil {

    private static final Set<String> DATA_PROVIDER_ANNOTATIONS = new LinkedHashSet(
            Collections.singletonList(DataProviderConstant.DATA_AGGREGATOR_DATA_PROVIDER));

    private static final Set<String> DATA_CONSUMER_ANNOTATIONS = new LinkedHashSet(
            Collections.singletonList(DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER));

    public static Set<PsiModifierListOwner> getDataProviderMembers(@NotNull PsiType type, @Nullable Module module,
                                                                   @NotNull PsiMethod method) {
        if (module == null) {
            return Collections.emptySet();
        } else {
            Set<PsiModifierListOwner> membersCandidate = new LinkedHashSet();
            GlobalSearchScope scope = method.getResolveScope();
            // TODO 这里获取的是方法的return type
            Set<PsiType> effectiveTypes = getEffectiveTypes(type);
            String flagValue = getAnnotationValue(method, DataProviderConstant.DATA_AGGREGATOR_DATA_PROVIDER,
                    DataProviderConstant.VALUE_ATTRIBUTE);
            DATA_CONSUMER_ANNOTATIONS.forEach(annotation -> {
                PsiClass annotationClass = JavaPsiFacade.getInstance(module.getProject()).findClass(annotation, scope);
                if (annotationClass != null) {
                    final Query<PsiMember> psiMembers = AnnotatedMembersSearch.search(annotationClass, scope);
                    psiMembers.forEach(psiMember -> {
                        if (psiMember instanceof PsiField) {
                            effectiveTypes.forEach(effectiveType -> {
                                PsiType psiType = ((PsiField) psiMember).getType();
                                if (!"java.lang.Object".equals(psiType.getCanonicalText()) && psiType.isAssignableFrom(
                                        effectiveType)) {
                                    membersCandidate.add(psiMember);
                                }
                            });
                        } else if (psiMember instanceof PsiMethod) {
                            PsiParameter[] psiParameters = ((PsiMethod) psiMember).getParameterList().getParameters();
                            for (PsiParameter psiParameter : psiParameters) {
                                effectiveTypes.forEach(effectiveType -> {
                                    addCandidatesPsiMethod(method, psiParameter, effectiveType, membersCandidate,
                                            flagValue);
                                });
                            }
                        }
                        return true;
                    });
                }
            });
            return filterCandidates(method, membersCandidate, module);
        }
    }

    private static String getAnnotationValue(PsiMethod method, String qualifiedName, String attributeName) {
        final PsiAnnotation[] methodAnnotations = method.getAnnotations();
        for (PsiAnnotation psiAnnotation : methodAnnotations) {
            // TODO 判断不准，加类加载器判断比较合适
            if (psiAnnotation.getQualifiedName().equals(qualifiedName)) {
                return AnnotationUtil.getStringAttributeValue(psiAnnotation, attributeName);
            }
        }
        return null;
    }

    private static void addCandidatesPsiMethod(PsiMethod method, PsiParameter psiParameter, PsiType effectiveType,
                                               Set<PsiModifierListOwner> membersCandidate, String flagValue) {
        PsiType psiType = psiParameter.getType();
        if (!"java.lang.Object".equals(psiType.getCanonicalText())) {
            final PsiAnnotation[] annotations = psiParameter.getAnnotations();
            if (psiType.isAssignableFrom(effectiveType)) {
                for (PsiAnnotation psiAnnotation : annotations) {
                    final String qualifiedName = psiAnnotation.getQualifiedName();
                    String value = getAnnotationValue(method, DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER,
                            DataConsumerConstant.VALUE_ATTRIBUTE);
                    if (qualifiedName.equals(DataConsumerConstant.DATA_AGGREGATOR_DATA_CONSUMER) && flagValue.equals(
                            value)) {
                        membersCandidate.add(psiParameter);
                        return;
                    }
                }
            }
        }
    }

    @NotNull
    private static Set<PsiType> getEffectiveTypes(@NotNull PsiType type) {
        Set<PsiType> types = new LinkedHashSet();
        types.add(type);
        types.addAll(SpringBeanCoreUtils.getFactoryBeanTypes(type, null));
        return types;
    }

    public static Set<PsiModifierListOwner> filterCandidates(@NotNull PsiMethod method, @NotNull
            Set<? extends PsiModifierListOwner> membersCandidate, @Nullable Module module) {
        SpringJamQualifier qualifier = getQualifier(method, getQualifiedAnnotation(method, module));
        return membersCandidate.stream().filter((owner) -> {
            SpringJamQualifier candidateQualifier = getQualifier(owner);
            if (candidateQualifier != null) {
                if (qualifier != null && qualifier.compareQualifiers(candidateQualifier, module)) {
                    return true;
                } else {
                    return method.getName().equals(candidateQualifier.getQualifierValue());
                }
            } else {
                return true;
            }
        }).collect(Collectors.toSet());
    }

    @Nullable
    public static SpringJamQualifier getQualifier(@Nullable PsiModifierListOwner modifierListOwner) {
        return modifierListOwner == null ? null : getQualifier(modifierListOwner,
                getQualifiedAnnotation(modifierListOwner));
    }

    @Nullable
    public static SpringJamQualifier getQualifier(@Nullable PsiModifierListOwner modifierListOwner,
                                                  @Nullable PsiAnnotation qualifiedAnnotation) {
        return qualifiedAnnotation == null ? null : new SpringJamQualifier(qualifiedAnnotation, modifierListOwner);
    }

    @Nullable
    public static PsiAnnotation getQualifiedAnnotation(@NotNull PsiModifierListOwner modifierListOwner) {
        return getQualifiedAnnotation(modifierListOwner, ModuleUtilCore.findModuleForPsiElement(modifierListOwner));
    }

    @Nullable
    private static PsiAnnotation getQualifiedAnnotation(@NotNull PsiModifierListOwner modifierListOwner,
                                                        @Nullable Module module) {
        if (module == null) {
            return null;
        } else {
            JamAnnotationTypeUtil jamAnnotationTypeUtil = JamAnnotationTypeUtil.getInstance(module);
            List<PsiClass> annotationTypeClasses = jamAnnotationTypeUtil.getQualifierAnnotationTypesWithChildren();
            Iterator<PsiClass> iterator = annotationTypeClasses.iterator();
            PsiAnnotation annotation;
            do {
                PsiClass annotationTypeClass;
                do {
                    if (!iterator.hasNext()) {
                        if (modifierListOwner instanceof PsiParameter) {
                            PsiMethod psiMethod = PsiTreeUtil.getParentOfType(modifierListOwner, PsiMethod.class);
                            if (psiMethod != null && isDataProviderByAnnotation(psiMethod)) {
                                for (PsiClass typeClass : annotationTypeClasses) {
                                    annotationTypeClass = typeClass;
                                    if (AnnotationTargetUtil.findAnnotationTarget(annotationTypeClass,
                                            TargetType.METHOD) != null) {
                                        annotation = AnnotationUtil.findAnnotation(psiMethod, true,
                                                annotationTypeClass.getQualifiedName());
                                        if (annotation != null) {
                                            return annotation;
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
                    annotationTypeClass = iterator.next();
                } while ((!(modifierListOwner instanceof PsiField) || AnnotationTargetUtil.findAnnotationTarget(
                        annotationTypeClass, TargetType.METHOD) == null));
                annotation = AnnotationUtil.findAnnotation(modifierListOwner, true, annotationTypeClass.getQualifiedName());
            } while (annotation == null);
            return annotation;
        }
    }

    public static boolean isDataProviderByAnnotation(@NotNull PsiModifierListOwner owner) {
        PsiModifierList modifierList = owner.getModifierList();
        if (modifierList != null && !modifierList.hasModifierProperty("static")
                && modifierList.getAnnotations().length != 0) {
            return AnnotationUtil.isAnnotated(owner, DATA_PROVIDER_ANNOTATIONS, 0);
        } else {
            return false;
        }
    }

    public static CommonSpringModel getProcessingSpringModel(@Nullable PsiClass psiClass) {
        if (psiClass != null && psiClass.getQualifiedName() != null) {
            if (psiClass instanceof PsiAnonymousClass) {
                return SpringModelUtils.getInstance().getModuleCombinedSpringModel(psiClass);
            } else {
                CommonSpringModel model = SpringModelUtils.getInstance().getPsiClassSpringModel(psiClass);
                if (model instanceof CombinedSpringModel) {
                    model = filterClassRelatedModels((CombinedSpringModel) model, psiClass);
                }
                return isEmptyModel(model) ? null : model;
            }
        } else {
            return null;
        }
    }

    private static CommonSpringModel filterClassRelatedModels(@NotNull CombinedSpringModel model, @NotNull PsiClass aClass) {
        HashSet<CommonSpringModel> models = new HashSet();
        for (CommonSpringModel commonSpringModel : model.getUnderlyingModels()) {
            if (SpringModelSearchers.doesBeanExist(commonSpringModel,
                    SpringModelSearchParameters.byClass(aClass).withInheritors())) {
                if (commonSpringModel instanceof SpringModel) {
                    SpringFileSet fileSet = ((SpringModel) commonSpringModel).getFileSet();
                    if (fileSet != null && fileSet.isAutodetected()) {
                        return model;
                    }
                }
                models.add(commonSpringModel);
            }
        }
        return new CombinedSpringModelImpl(models, model.getModule());
    }

    private static boolean isEmptyModel(@NotNull CommonSpringModel model) {
        boolean underlyingIsEmpty = ((CombinedSpringModel) model).getUnderlyingModels().isEmpty();
        return model.equals(SpringModel.UNKNOWN) || underlyingIsEmpty;
    }


    public static boolean isInjectionPoint(@NotNull PsiMethod psiMethod) {
        boolean hasParameters = psiMethod.getParameterList().getParametersCount() != 0;
        return hasParameters && isDataProviderByAnnotation(psiMethod);
    }

    public static Set<SpringBeanPointer> getDataProviderBeansFor(@NotNull PsiModifierListOwner injectionPointOwner,
                                                                 @NotNull PsiType psiType,
                                                                 @NotNull CommonSpringModel springModel) {
        if (psiType instanceof PsiTypeParameter) {
            return Collections.emptySet();
        } else {
            PsiAnnotation qualifiedAnnotation = getEffectiveQualifiedAnnotation(injectionPointOwner);
            if (qualifiedAnnotation != null) {
                return getQualifiedDataProviderBeans(psiType, qualifiedAnnotation, springModel);
            } else {
                return Collections.emptySet();
            }
        }
    }

    public static PsiAnnotation getEffectiveQualifiedAnnotation(@NotNull PsiModifierListOwner modifierListOwner) {
        return modifierListOwner instanceof PsiMethod ? null : getQualifiedAnnotation(modifierListOwner);
    }

    private static Set<SpringBeanPointer> getQualifiedDataProviderBeans(@NotNull PsiType type, @NotNull PsiAnnotation annotation, @NotNull CommonSpringModel model) {
        return filterPointersByDataProviderType(type, getQualifiedBeanPointers(annotation, model));
    }

    public static Set<SpringBeanPointer> filterPointersByDataProviderType(@NotNull PsiType searchType, @NotNull Set<? extends SpringBeanPointer> beanPointers) {
        Set<SpringBeanPointer> dataProviderPointers = new HashSet();
        for (SpringBeanPointer bean : beanPointers) {
            PsiType[] psiTypes = bean.getEffectiveBeanTypes();
            for (int i = 0; i < psiTypes.length; ++i) {
                PsiType psiType = psiTypes[i];
                if (canBeDataProviderByType(searchType, psiType)) {
                    dataProviderPointers.add(bean);
                    break;
                }
            }
        }
        return dataProviderPointers;
    }

    public static boolean canBeDataProviderByType(@NotNull PsiType psiType, @NotNull PsiType searchType) {
        if (psiType.isAssignableFrom(searchType)) {
            return true;
        } else {
            PsiType iterableType = getIterableType(psiType);
            if (iterableType != null && iterableType.isAssignableFrom(searchType)) {
                return true;
            } else if (isObjectFactoryEffectiveType(psiType, searchType)) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static PsiType getIterableType(@NotNull PsiType psiType) {
        return psiType instanceof PsiArrayType ? ((PsiArrayType) psiType).getComponentType()
                : PsiUtil.extractIterableTypeParameter(psiType, true);
    }

    private static boolean isObjectFactoryEffectiveType(@NotNull PsiType psiType, @NotNull PsiType aType) {
        PsiType objectFactoryEffectiveType = SpringObjectFactoryEffectiveTypeProvider.getObjectFactoryEffectiveType(aType);
        return objectFactoryEffectiveType != null && psiType.isAssignableFrom(objectFactoryEffectiveType);
    }

    public static Set<SpringBeanPointer> getQualifiedBeanPointers(@NotNull PsiAnnotation qualifiedAnnotation, @NotNull CommonSpringModel model) {

        List<SpringBeanPointer> candidates = getQualifiedBeans(qualifiedAnnotation, model);
        String name = getQualifiedBeanName(qualifiedAnnotation);
        if (name != null) {
            SpringBeanPointer pointer = SpringModelSearchers.findBean(model, name);
            if (pointer != null) {
                candidates = new ArrayList(candidates);
                (candidates).add(pointer.getBasePointer());
            }
        }
        return excludeDataProviderCandidates(candidates, null, model);
    }

    public static List<SpringBeanPointer> getQualifiedBeans(@NotNull PsiAnnotation psiAnnotation, @Nullable CommonSpringModel model) {
        if (model == null) {
            return new ArrayList();
        } else {
            SpringJamQualifier qualifier = getQualifier(null, psiAnnotation);
            return SpringModelVisitorUtils.findQualifiedBeans(model, qualifier);
        }
    }

    public static String getQualifiedBeanName(@NotNull PsiAnnotation qualifiedAnnotation) {
        PsiAnnotationMemberValue attributeValue = qualifiedAnnotation.findDeclaredAttributeValue("value");
        return attributeValue == null ? null : JamCommonUtil.getObjectValue(attributeValue, String.class);
    }

    public static Set<SpringBeanPointer> excludeDataProviderCandidates(Collection<SpringBeanPointer> beans, String primaryCandidateName, CommonSpringModel model) {
        Set<SpringBeanPointer> pointers = new LinkedHashSet();
        Collection<SpringBeanPointer> primaryBeans = beans.size() > 1 ? getPrimaryBeans(beans, primaryCandidateName, model) : beans;
        if (!primaryBeans.isEmpty()) {
            primaryBeans.forEach(springBeanPointer -> {
                if (isDataProviderCandidate(springBeanPointer)) {
                    pointers.add(springBeanPointer);
                }
            });
        } else {
            beans.forEach(springBeanPointer -> {
                if (isDataProviderCandidate(springBeanPointer)) {
                    pointers.add(springBeanPointer);
                }
            });
        }
        return pointers;
    }

    private static boolean isDataProviderCandidate(@Nullable SpringBeanPointer pointer) {
        if (pointer != null && pointer.isValid()) {
            CommonSpringBean springBean = pointer.getSpringBean();
            if (!(springBean instanceof SpringBean)) {
                return true;
            } else if (((SpringBean) springBean).isAbstract()) {
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static List<SpringBeanPointer> getPrimaryBeans(@NotNull Collection<? extends SpringBeanPointer> beans, @Nullable String primaryCandidateName, @Nullable CommonSpringModel model) {
        if (primaryCandidateName == null) {
            return new ArrayList();
        } else {
            List<SpringBeanPointer> byPrimary = new SmartList();
            List<SpringBeanPointer> byName = new SmartList();

            for (SpringBeanPointer springBeanPointer : beans) {
                if (springBeanPointer.isValid()) {
                    CommonSpringBean springBean = springBeanPointer.getSpringBean();
                    if (springBean.isPrimary()) {
                        if (isMyName(primaryCandidateName, springBeanPointer, model)) {
                            return Collections.singletonList(springBeanPointer);
                        }
                        byPrimary.add(springBeanPointer);
                    } else if (isMyName(primaryCandidateName, springBeanPointer, model)) {
                        byName.add(springBeanPointer);
                    }
                }
            }
            return byPrimary.isEmpty() ? byName : byPrimary;
        }
    }

    private static boolean isMyName(@Nullable String name, @NotNull SpringBeanPointer springBeanPointer, @Nullable CommonSpringModel model) {
        if (name == null) {
            return false;
        } else {
            String beanName = springBeanPointer.getName();
            if (name.equals(beanName)) {
                return true;
            } else {
                if (beanName != null && model != null) {
                    for (String aliasName : SpringModelVisitorUtils.getAllBeanNames(model, springBeanPointer)) {
                        if (name.equals(aliasName)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    }

    public static boolean isJavaUtilOptional(@NotNull PsiType type) {
        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        return psiClass != null && "java.util.Optional".equals(psiClass.getQualifiedName());
    }

    public static PsiType getOptionalType(@NotNull PsiType psiClassType) {
        return PsiUtil.substituteTypeParameter(psiClassType, "java.util.Optional", 0, false);
    }
}