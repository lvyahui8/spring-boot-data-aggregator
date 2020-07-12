package io.github.lvyahui8.aggregator.assistant.enums;

import com.intellij.ui.IconManager;

import javax.swing.*;

/**
 * @version V0.1.0
 * @ClassName: IconEnum
 * @author: LiHaiQing
 * @date: 2020/4/2 20:45
 */
public interface IconEnum {

    /***
     *
     */
    Icon data_consumer = loadIcon("/icons/dataConsumer@16.svg");
    Icon data_provider = loadIcon("/icons/dataProvider@18.svg");


    static Icon loadIcon(String path) {
        return IconManager.getInstance().getIcon(path, IconEnum.class);
    }
}