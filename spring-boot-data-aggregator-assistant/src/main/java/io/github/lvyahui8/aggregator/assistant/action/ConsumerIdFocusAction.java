package io.github.lvyahui8.aggregator.assistant.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2020/7/16
 */
public class ConsumerIdFocusAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        // Set visibility only in case of existing project and editor and if a selection exists
        e.getPresentation().setEnabledAndVisible( project != null
                && editor != null
                && editor.getSelectionModel().hasSelection() );
    }
}
