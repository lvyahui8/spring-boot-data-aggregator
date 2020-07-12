package io.github.lvyahui8.aggregator.assistant.psi.jam.consumer;

import com.intellij.jam.JamElement;
import com.intellij.jam.JamService;
import com.intellij.jam.reflect.JamMemberMeta;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import com.intellij.semantic.SemKey;

/**
 * @version V0.1.0
 * @ClassName: DataConsumer
 * @author: LiHaiQing
 * @date: 2020/4/22 20:49
 */
public interface DataConsumer extends JamElement {

    String DEBUG_NAME = "DataConsumer";

    SemKey<DataConsumer> DATA_CONSUMER_JAM_KEY = JamService.JAM_ELEMENT_KEY.subKey(DEBUG_NAME);
    SemKey<JamMemberMeta> DATA_CONSUMER_META_KEY = JamService.getMetaKey(DATA_CONSUMER_JAM_KEY);


    PsiMember getPsiElement();

    boolean isPsiValid();

    PsiAnnotation getPsiAnnotation();

}