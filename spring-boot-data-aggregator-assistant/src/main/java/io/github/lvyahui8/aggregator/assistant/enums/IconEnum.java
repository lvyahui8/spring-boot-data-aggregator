package io.github.lvyahui8.aggregator.assistant.enums;

import com.intellij.ui.IconManager;

import javax.swing.*;

/**
 * @version V0.1.0
 * @ClassName: IconEnum
 * @author: LiHaiQing
 * @date: 2020/4/2 20:45
 */
public enum IconEnum {

    /***
     *
     */
    DATA_CONSUMER("/icons/dataConsumer@16.svg"),
    DATA_PROVIDER("/icons/dataProvider@18.svg");

    private String path;

    IconEnum(String path) {
        this.path = path;
    }

    public Icon getIcon() {
        return IconManager.getInstance().getIcon(path, IconEnum.class);
    }

}