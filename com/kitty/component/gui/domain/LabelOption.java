package com.kitty.component.gui.domain;

import com.kitty.component.gui.BasicPanel;

/**
 * 用于ScrollLabel的显示文本项。
 */
public class LabelOption {
    
    private String text;
    
    private String href;
    
    private int width; // 文本显示宽度，单位：像素
    
    private long showTime = 15000; // 每个Option的显示时间，单位：ms
    
    public LabelOption(String text) {
        this.text = text;
        this.width = BasicPanel.getLabelWidth(text);
    }
    
    public LabelOption(String text, String href) {
        this(text);
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getWidth() {
        return width;
    }

}
