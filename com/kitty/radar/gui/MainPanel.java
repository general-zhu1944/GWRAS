package com.kitty.radar.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kitty.radar.MapOverlay;
import com.kitty.radar.PPI;
import com.kitty.radar.RadarBase;
import com.kitty.radar.business.cr.CR;
import com.kitty.radar.business.vil.VIL;
import com.kitty.radar.color.RadarColor;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

public class MainPanel extends JPanel implements Printable {
    private  MapOverlay map;

    //面板基本参数
    private RadarBase radarBase;

    public int xoffset = 0;

    public int yoffset = 0;

    //存储的是画好的图像，MainPanel只是将该图像在画在自己的界面上，由于每个MainPanel不一样，所以每个MainPanel应该有一个
    public BufferedImage image;

    //图像是否更新
    public boolean update = true;

    //光标位置
    private int mX = 0;
    private int mY = 0;

    //光标是否活动
    private boolean mActive = false;

    //状态信息
    private String statusText = "";
    //是否展示状态信息
    private boolean showStatusText = false;

    //标题信息
    private String labelText = "";

    public Dimension initSize = new Dimension(-10, -10);

    public MapOverlay getMap() {
        return map;
    }

    public void setMap(MapOverlay map) {
        this.map = map;
    }

    public int getmX() {
        return mX;
    }

    public void setmX(int mX) {
        this.mX = mX;
    }

    public int getmY() {
        return mY;
    }

    public void setmY(int mY) {
        this.mY = mY;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String text) {
        this.statusText = text;
    }

    public boolean isShowStatusText() {
        return showStatusText;
    }

    public void setShowStatusText(boolean showText) {
        this.showStatusText = showText;
    }

    public MainPanel(int activeMoment, int currentMoment) {
        radarBase = new RadarBase();
        radarBase.active_moment = activeMoment;
        radarBase.currentMoment = currentMoment;
        map = new MapOverlay(radarBase);
    }


    public void paintComponent(Graphics g) {
//    	if (!isInitFinished())
//			return;
        super.paintComponent(g);
        int width = this.getWidth();
        int height = this.getHeight();
        setBaseSize(width, height);
        if (xoffset != 0 || yoffset != 0) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }
        drawImage(g, xoffset, yoffset);
        {
            //绘制标题
            evaLabelText();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.white);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 16));//微软雅黑
            g2.drawString(labelText, width/2-200, 20);
            g2.dispose();
        }
        if (mActive) {
            //绘制十字光标
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(mX, mY + 10, mX, mY - 10);
            g2.drawLine(mX + 10, mY, mX - 10, mY);
            g2.dispose();
        }

        if (showStatusText) {
            //绘制状态信息
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.white);
            g2.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            g2.drawString(statusText, width/2-210, height-10);
            g2.dispose();
        }

        //绘制激活红色边框
        if(GUIManager.activeMainPanel == this&&GUIManager.mainPanelContainer.getComponents().length!=1) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.red);
            g2.drawLine(0, 0, width, 0);
            g2.drawLine(width, 0, width, height);
            g2.drawLine(width, height, 0, height);
            g2.drawLine(0, height, 0, 0);
            g2.dispose();
        } else {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.gray);
            g2.drawLine(0, 0, width, 0);
            g2.drawLine(width, 0, width, height);
            g2.drawLine(width, height, 0, height);
            g2.drawLine(0, height, 0, 0);
            g2.dispose();
        }

    }

    private String evaLabelText() {
        if(this.radarBase.l2 != null) {
            labelText =  RadarUtils.getFileTime(radarBase) +
                    " - " + CommonUtils.format(radarBase.l2.getElevation(radarBase.cutNum), 1)+"°"+
                    //				"- " + radarBase.l2.getSrcFileName() +
                    " - " + RadarUtils.getMomentLabel(radarBase);
        }
        return labelText;
    }

    private boolean isInitFinished() {
		if (initSize == null) {
			return true;
		}
		if (getSize().equals(initSize)) {
			initSize = null;
			return true;
		}
		return false;
	}

    public void drawImage(Graphics g, int xoffset, int yoffset) {
        RadarBase RadarBase = this.radarBase;
        int width = RadarBase.width;
        int height = RadarBase.height;
        if (image == null) {
//            image = new BufferedImage(width*10, height*10, BufferedImage.TYPE_INT_RGB);
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            update = true;
        }
        if (update) {
            RadarBase.datas = null;
//            GUIManager.toolBarLabel.setText(" ");
//            GUIManager.statusCenter.setText(" ");
            Graphics2D g2 = (Graphics2D) image.createGraphics();
            g2.setPaint(Color.BLACK);
            g2.fillRect(0, 0, RadarBase.width, RadarBase.height);
            try {
                int moment = RadarBase.currentMoment;
				if (moment == CommonProps.MOMENT_VIL) {
					VIL.display(g2,this.radarBase);
				} else {
                    if (RadarBase.view == CommonProps.VIEW_PPI) {
                        if (RadarBase.cutNum == CommonProps.MOMENT_CR) {
                            CR.display(g2,this.radarBase);
                        } else {
                            PPI.displayPPI(g2, this.radarBase);
                        }
					}
                }

                update = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            displayColorBar(g2, width, height);
            g2.dispose();
        }
        int w = width - CommonProps.COLOR_WIDTH;
        g.drawImage(image, xoffset, yoffset, xoffset + w, yoffset + height, 0, 0, w, height, null);
		//JOptionPane.showMessageDialog(null, "消息提示tjjjjt："+image.getWidth()+"ll"+image.getHeight());
        g.drawImage(map.drawMap(), xoffset, yoffset, width, height, null);
        g.drawImage(image, w, 0, width, height, w, 0, width, height, null);

    }

    private void displayColorBar(Graphics2D g, int width, int height) {
        RadarColor radarColor = RadarUtils.getRadarColor(this.radarBase.currentMoment);
        Color[] colors = radarColor.getColors();
        float[] values = radarColor.getColorValues();
        g.setPaint(new Color(130, 130, 130));
        g.fillRect((width - CommonProps.COLOR_WIDTH), 0, CommonProps.COLOR_WIDTH, height);

        int boxHeight = height / 18; // 色标块高度
        int boxWidth = 40; // 色标块宽度
        int x = width - (boxWidth + CommonProps.COLOR_WIDTH) / 2; // 色标块左边缘X坐标
        int textX = width - 25 + radarColor.getScale() * 3; // 色标值右边缘X坐标
        int textY = boxHeight / 2 + 4; // 色标值相对色标块顶部的距离

        g.setPaint(new Color(0, 0, 0));
        g.drawString(RadarUtils.getMomentUnitLabel(this.radarBase.currentMoment), x + 4, textY);

        Color c1 = new Color(90, 90, 90);
        Color c2 = Color.WHITE;
        String svalue;
        g.setFont(new Font("宋体", Font.PLAIN, 13));
        for (int i = 0; i < colors.length; i++) {
            int y = (i + 1) * boxHeight;
            g.setPaint(colors[i]);
            g.fillRect(x, y, boxWidth, boxHeight);

            svalue = CommonUtils.format(values[i], radarColor.getScale());
            if (i == colors.length - 1) {
                svalue = ">" + svalue;
            }
            g.setPaint(c1);
            y += textY;
            g.drawString(svalue, PositionUtils.getTextPosition(textX, svalue), y);
            g.drawString(svalue, PositionUtils.getTextPosition(textX, svalue), y + 1);
            g.setPaint(c2);
            g.drawString(svalue, PositionUtils.getTextPosition(textX - 1, svalue), y);
        }
    }

    private void setBaseSize(int width, int height) {
        RadarBase RadarBase = this.radarBase;
        if (width != RadarBase.width || height != RadarBase.height) {
            RadarBase.width = width;
            RadarBase.height = height;
            RadarBase.computeScale();
            RadarBase.center_X = (width - CommonProps.COLOR_WIDTH) / 2;
            RadarBase.center_Y = height / 2;
           // MapOverlay.image = null;
            this.map.image = null;
            image = null;
        }
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }
        BufferedImage image = new BufferedImage(radarBase.width, radarBase.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) image.createGraphics();
        drawImage(g2, 0, 0);
        g2.dispose();
        g2 = (Graphics2D) g;
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        int width = (int) pageFormat.getImageableWidth();
        int height = (int) pageFormat.getImageableHeight();
        double rate = this.getWidth() / (double) this.getHeight();
        if (width / (double) height > rate) {
            width = (int) (height * rate);
        } else {
            height = (int) (width / rate);
        }
        g2.drawImage(image, 0, 0, width, height, null);
        return Printable.PAGE_EXISTS;
    }

    public RadarBase getRadarBase() {
        return radarBase;
    }

    public void setRadarBase(RadarBase radarBase) {
        this.radarBase = radarBase;
    }

    public boolean ismActive() {
        return mActive;
    }

    public void setmActive(boolean mActive) {
        this.mActive = mActive;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }
}
