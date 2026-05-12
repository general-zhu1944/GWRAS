package com.kitty.radar.business.area;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import org.netbeans.validation.api.ui.ValidationGroup;


/**
 * 区域handler基类。
 */
public abstract class AreaHandler {

    protected AreaInputDialog dialog;

    protected ValidationGroup group;

    /**
     * 点击“确定”按钮后要执行的处理
     * 
     * @param e
     */
    public abstract void process(ActionEvent e);

    /**
     * 取得对话框打开后缺省获得焦点的组件
     * 
     * @return
     */
    public abstract JComponent getFocusComponent();

}
