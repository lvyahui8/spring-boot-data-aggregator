package io.github.lvyahui8.aggregator.assistant.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2020/7/15
 */
public interface AggregatorProjectService {
    static AggregatorProjectService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, AggregatorProjectService.class);
    }
}
