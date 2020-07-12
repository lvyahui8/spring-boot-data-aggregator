package io.github.lvyahui8.aggregator.assistant.utils;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.compiled.ClsTypeParameterImpl;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @version V0.1.0
 * @ClassName: AssistantUtil
 * @author: LiHaiQing
 * @date: 2020/4/2 20:41
 */
public class AssistantUtil {


    public static List<ClsTypeParameterImpl> findTargets(Project project, String key) {
        List<ClsTypeParameterImpl> result = null;
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile != null) {
                final ClsTypeParameterImpl[] targets = PsiTreeUtil.getChildrenOfType(psiFile, ClsTypeParameterImpl.class);
                if (targets != null) {
                    for (ClsTypeParameterImpl target : targets) {
                        if (key.equals(target.getName())) {
                            if (result == null) {
                                result = new ArrayList<ClsTypeParameterImpl>();
                            }
                            result.add(target);
                        }
                    }
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }



}