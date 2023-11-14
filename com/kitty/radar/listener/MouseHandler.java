package com.kitty.radar.listener;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.kitty.radar.MapOverlay;
import com.kitty.radar.RHI;
import com.kitty.radar.RadarBase;
import com.kitty.radar.business.cappi.CAPPI;
import com.kitty.radar.business.cr.CR;
import com.kitty.radar.business.tops.TOPS;
import com.kitty.radar.business.vil.VIL;
import com.kitty.radar.data.RadarData;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.LLCoord;
import com.kitty.radar.domain.ListElement;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.MainPanel;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

import javax.swing.*;

public class MouseHandler extends MouseAdapter implements MouseWheelListener,
		MouseMotionListener {

	private int x;

	private int y;

	@Override
	public void mouseEntered(MouseEvent e) {
		MainPanel mainPanel = (MainPanel)e.getComponent();
		mainPanel.setmActive(true);
		if(GUIManager.isSyncTool()) {
			for (MainPanel panel : GUIManager.getJpanels()) {
				panel.setmActive(true);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		MainPanel mainPanel = (MainPanel)e.getComponent();
		mainPanel.setmActive(false);
		if(GUIManager.isSyncTool()) {
			for (MainPanel panel : GUIManager.getJpanels()) {
				panel.setmActive(false);
			}
		}
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		MainPanel mainPanel = (MainPanel)e.getComponent();
		RadarBase radarBase = mainPanel.getRadarBase();
		int num = e.getWheelRotation();
		if (num == 0) {
			return;
		} else if (num > 0) {
			num = radarBase.zoom / 2;
			if (num < RadarBase.MIN_ZOOM) {
				return;
			}
		} else {
			num = radarBase.zoom +1;
			if (num > RadarBase.MAX_ZOOM) {
				return;
			}
		}
		int zoom1 = radarBase.zoom;
		radarBase.setZoom(num);
		int x = e.getX();
		int y = e.getY();
		radarBase.xoffset += Math.round((x - radarBase.center_X)
				* radarBase.zoom / (double) zoom1)
				+ radarBase.center_X - x;
		radarBase.yoffset += Math.round((y - radarBase.center_Y)
				* radarBase.zoom / (double) zoom1)
				+ radarBase.center_Y - y;
		mainPanel.getMap().update = true;
		mainPanel.getRain().update = true;
		mainPanel.update = true;
		mainPanel.repaint();
		GUIManager.syncAllMainPanel(true);
	}

	private void setActiveMainPanel(MouseEvent e) {
		//设置当前激活MainPanel变量及样式
		GUIManager.activeMainPanel = (MainPanel) e.getComponent();
		MainPanel activeMainPanel = (MainPanel) e.getComponent();
		for(JPanel panel: GUIManager.getJpanels()) {
			if(panel != GUIManager.activeMainPanel) {
				panel.setBorder(BorderFactory.createLineBorder(Color.gray));
			} else {
				if(GUIManager.mainPanelContainer.getComponents().length!=1)
				{
					panel.setBorder(BorderFactory.createLineBorder(Color.red));
				}
			}
		}

		//更新选中的toolbar
		JPanel varPanel = GUIManager.getVarPanel();
		Component[] components = varPanel.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component comp = components[i];
			if(comp instanceof JRadioButton) {
				int moment = activeMainPanel.getRadarBase().currentMoment;
				String command = ((JRadioButton) comp).getActionCommand();
				if (CommonProps.AC_REFLECTIVITY.equals(command)
						&& moment == CommonProps.MOMENT_R) {
					((JRadioButton) comp).setSelected(true);
				} else if (CommonProps.AC_VELOCITY.equals(command)
						&& moment == CommonProps.MOMENT_V) {
					((JRadioButton) comp).setSelected(true);
				} else if (CommonProps.AC_SPECTRUM_WIDTH.equals(command)
						&& moment == CommonProps.MOMENT_W) {
					((JRadioButton) comp).setSelected(true);
				} else if (CommonProps.AC_DBT.equals(command)
						&& moment == CommonProps.MOMENT_DBT) {
					((JRadioButton) comp).setSelected(true);
				} else if (CommonProps.AC_ZDR.equals(command)
						&& moment == CommonProps.MOMENT_ZDR) {
					((JRadioButton) comp).setSelected(true);
				} else if (CommonProps.AC_KDP.equals(command)
						&& moment == CommonProps.MOMENT_KDP) {
					((JRadioButton) comp).setSelected(true);
				} else if (CommonProps.AC_DP.equals(command)
						&& moment == CommonProps.MOMENT_DP) {
					((JRadioButton) comp).setSelected(true);
				} else if (CommonProps.AC_CC.equals(command)
						&& moment == CommonProps.MOMENT_CC) {
					((JRadioButton) comp).setSelected(true);
				} else if (CommonProps.AC_SNRH.equals(command)
						&& moment == CommonProps.MOMENT_SNRH) {
					((JRadioButton) comp).setSelected(true);
				} else {

				}
			}
		}
		
		//设置选中变量
		int curMoment = activeMainPanel.getRadarBase().currentMoment;
		Component[] varComps = GUIManager.getVarPanel().getComponents();
		for (int j = 0; j < varComps.length; j++) {
			Component varComp = varComps[j];
			if(varComp instanceof JRadioButton) {
				String command = ((JRadioButton) varComp).getActionCommand();

				String curActionCommond  = "";
				if(curMoment == CommonProps.MOMENT_R) {
					curActionCommond = CommonProps.AC_REFLECTIVITY;
				}
				if(curMoment == CommonProps.MOMENT_V) {
					curActionCommond = CommonProps.AC_VELOCITY;
				}
				if(curMoment == CommonProps.MOMENT_W) {
					curActionCommond = CommonProps.AC_SPECTRUM_WIDTH;
				}
				if(curMoment == CommonProps.MOMENT_DBT) {
					curActionCommond = CommonProps.AC_DBT;
				}
				if(curMoment == CommonProps.MOMENT_ZDR) {
					curActionCommond = CommonProps.AC_ZDR;
				}
				if(curMoment == CommonProps.MOMENT_KDP) {
					curActionCommond = CommonProps.AC_KDP;
				}
				if(curMoment == CommonProps.MOMENT_DP) {
					curActionCommond = CommonProps.AC_DP;
				}
				if(curMoment == CommonProps.MOMENT_CC) {
					curActionCommond = CommonProps.AC_CC;
				}
				if(curMoment == CommonProps.MOMENT_SNRH) {
					curActionCommond = CommonProps.AC_SNRH;
				}
				if(curMoment == CommonProps.MOMENT_LW) {
					curActionCommond = CommonProps.AC_LIQUID_WATER;
				}
				if(curMoment == CommonProps.MOMENT_VIL) {
					curActionCommond = CommonProps.AC_VERTICAL_LIQUID_WATER;
				}
				if(curMoment == CommonProps.MOMENT_ET) {
					curActionCommond = CommonProps.AC_ECHO_TOPS;
				}
				if(curMoment == CommonProps.MOMENT_HP) {
					curActionCommond = CommonProps.AC_HAIL_PROBABILITY;
				}
				if(command.equals(curActionCommond)) {
					((JRadioButton) varComp).setSelected(true);
				}
			}
		}

		//1. 设置cut选中
		GUIManager.createCutButtons();
		Component[] comps = GUIManager.cutPanel.getComponents();
		int cut = activeMainPanel.getRadarBase().cutNum;
		for (int i = 0; i < comps.length; i++) {
			Component comp = comps[i];
			if(comp instanceof JRadioButton) {
				int btnCut = Integer.parseInt(((JRadioButton) comp).getActionCommand());
				if(btnCut == cut) {
					((JRadioButton) comp).setSelected(true);
				}
			}
		}
		GUIManager.setPPIEnabled();

		//2. 设置文件同步
		String srcFileName = "";
		if(activeMainPanel.getRadarBase().l2 != null) {
			srcFileName = activeMainPanel.getRadarBase().l2.getSrcFileName();
		} else {
			GUIManager.list.clearSelection();
			return;
		}
		JList list = GUIManager.list;
		int size = list.getModel().getSize();
		for(int i=0; i<size; i++) {
			ListElement ele = (ListElement)(list.getModel().getElementAt(i));
			if(ele.getLabel().equals(srcFileName)) {
				list.setSelectedIndex(i);
			}
		}
	}

	private void showVarValue(MainPanel mainPanel, RadarBase radarBase, int x, int y) {
		ARCoord arc = PositionUtils.toARCoord(x, y, radarBase);

		double value = RadarData.NO_DATA;
		LLCoord llc = null;
		if (radarBase.l2 != null) {
			int currentMoment = radarBase.currentMoment;
			if (currentMoment == CommonProps.MOMENT_HP) {
			} else if (currentMoment == CommonProps.MOMENT_ET) {
				value = TOPS.getPointValue(x, y,radarBase);
			} else if (currentMoment == CommonProps.MOMENT_VIL) {
				value = VIL.getPointValue(x, y,radarBase);
			} else {
				if (radarBase.view == CommonProps.VIEW_PPI) {
					if (radarBase.cutNum == CommonProps.MOMENT_CR) {
						value = CR.getPointValue(x, y,radarBase);
					} else {
						value = radarBase.l2.getMomentValue(
								radarBase.active_moment, radarBase.cutNum,
								arc.azimuth, arc.r);
						llc = PositionUtils.toLLCoord(arc.azimuth,
								PositionUtils.toR(arc.r, radarBase.l2
										.getElevation(radarBase.cutNum)));
						XYCoord s = PositionUtils.toXYCoord2(llc.longitude, llc.latitude, radarBase);
					}
				} else if (radarBase.view == CommonProps.VIEW_CAPPI) {
					value = CAPPI.getPointValue(x, y,radarBase);
				}
				value = RadarUtils.getMomentValue((float) value, radarBase.currentMoment);
			}
		}
		if (llc == null) {
			llc = PositionUtils.toLLCoord(arc.azimuth, arc.r);
		}

		String text = "["
				+ CommonUtils.format(llc.longitude, 2)
				+ "°, "
				+ CommonUtils.format(llc.latitude, 2)
				+ "°] -- ["
				+ CommonUtils.format(arc.azimuth, 1)
				+ "°, "
				+ CommonUtils.format(arc.r, 1)
				+ RadarUtils.getDistanceUnitLabel()
				+ ", "
				+ CommonUtils.format(PositionUtils.getHeight(arc.r, radarBase.cutNum), 1)
				+ RadarUtils.getDistanceUnitLabel()
				+ "] -- "
				+ (RadarData.isValid(value) ? CommonUtils.format(value,
				RadarUtils.getMomentScale(radarBase.currentMoment))
				+ RadarUtils.getMomentUnitLabel(radarBase.currentMoment) : "N/A");
		mainPanel.setStatusText(text);
		mainPanel.setShowStatusText(true);
	}


	public void mousePressed(MouseEvent e) {
		setActiveMainPanel(e);

		MainPanel mainPanel = (MainPanel)e.getComponent();
		RadarBase radarBase = mainPanel.getRadarBase();
		x = e.getX();
		y = e.getY();
		if (GUIManager.activeToolButton != null) {
			String command = GUIManager.activeToolButton.getActionCommand();
			if (!CommonProps.AC_T_CURSOR.equals(command)
					&& !CommonProps.AC_T_MEASURE.equals(command)
					&& !CommonProps.AC_T_VCS.equals(command)) {
				return;
			}
		}
		if(GUIManager.isSyncTool()) {
			for (MainPanel panel : GUIManager.getJpanels()) {
				showVarValue(panel, panel.getRadarBase(), x, y);
				ARCoord arc = PositionUtils.toARCoord(x, y, radarBase);
				for (RHI rhi :RHI.rhis) {
					if (rhi.getRadarBase().equals(panel.getRadarBase()))
					{
						if (rhi != null) {
							if(rhi.postion==1)
							{
								rhi.postion=0;
							}
							else
							{
								rhi.postion=1;
							}
							if(rhi.postion==1)
							{
								rhi.azimuth = arc.azimuth;
								rhi.update();
							}
						}
					}
				}

			}

		} else {
			showVarValue(mainPanel, radarBase, x, y);
			ARCoord arc = PositionUtils.toARCoord(x, y, radarBase);
			for (RHI rhi :RHI.rhis) {
				if (rhi.getRadarBase().equals(GUIManager.activeMainPanel.getRadarBase()))
				{
					if (rhi != null) {
						if(rhi.postion==1)
						{
							rhi.postion=0;
						}
						else
						{
							rhi.postion=1;
						}
						if(rhi.postion==1)
						{
							rhi.azimuth = arc.azimuth;
							rhi.update();
						}
					}
				}
			}
		}



	}

	public void mouseDragged(MouseEvent e) {
		MainPanel mainPanel = (MainPanel)e.getComponent();
		RadarBase radarBase = mainPanel.getRadarBase();
		mainPanel.setmX(e.getX());
		mainPanel.setmY(e.getY());

		if(GUIManager.isSyncTool()) {
			for (MainPanel panel : GUIManager.getJpanels()) {
				if (GUIManager.activeToolButton != null) {
					String command = GUIManager.activeToolButton.getActionCommand();
					if (CommonProps.AC_T_VCS.equals(command)
					) {
						showVarValue(panel, panel.getRadarBase(), e.getX(), e.getY());
						GUIManager.syncAllMainPanelMouseMoved(e.getX(), e.getY());
					}
				}
			}
		}else {
			if (GUIManager.activeToolButton != null) {
				String command = GUIManager.activeToolButton.getActionCommand();
				if (CommonProps.AC_T_VCS.equals(command)
				) {
					showVarValue(mainPanel, mainPanel.getRadarBase(), e.getX(), e.getY());
					mainPanel.repaint();
				}
			}
		}
		if (GUIManager.activeToolButton != null
				&& CommonProps.AC_T_HAND.equals(GUIManager.activeToolButton
				.getActionCommand())) {
//			MainPanel mainPanel = (MainPanel)e.getComponent();
//			RadarBase radarBase = mainPanel.getRadarBase();
			int x0 = e.getX();
			int y0 = e.getY();
			int dx = x0 - x;
			int dy = y0 - y;

			radarBase.xoffset -= dx;
			radarBase.yoffset -= dy;

			mainPanel.xoffset += dx;
			mainPanel.yoffset += dy;
			mainPanel.repaint();

			x = x0;
			y = y0;
//			mainPanel.update = true;
//			mainPanel.getMap().update = true;
//			mainPanel.repaint();
			GUIManager.syncAllMainPanel(false, false, dx, dy, x, y);
		} else {
			GUIManager.syncAllMainPanelMouseMoved(e.getX(), e.getY());
		}
	}

	public void mouseReleased(MouseEvent e) {
		MainPanel mainPanel = (MainPanel)e.getComponent();
		RadarBase radarBase = mainPanel.getRadarBase();
		if(GUIManager.isSyncTool()) {
			for (MainPanel panel : GUIManager.getJpanels()) {
				panel.setShowStatusText(false);
			}
		}else {
			mainPanel.setShowStatusText(false);
		}
		if (GUIManager.activeToolButton != null) {
			String command = GUIManager.activeToolButton.getActionCommand();
			if (CommonProps.AC_T_HAND.equals(command)) {
				mainPanel.xoffset = 0;
				mainPanel.yoffset = 0;
				mainPanel.getMap().update = true;
				mainPanel.getRain().update = true;
				mainPanel.update = true;
				mainPanel.repaint();
				GUIManager.syncAllMainPanel(true);
			} else if (CommonProps.AC_T_ZOOM_IN.equals(command)) {
				int zoom = radarBase.zoom * 2;
				if (zoom > radarBase.MAX_ZOOM) {
					return;
				}
				radarBase.xoffset += e.getX() - radarBase.center_X;
				radarBase.yoffset += e.getY() - radarBase.center_Y;
				radarBase.setZoom(zoom);
				mainPanel.getMap().update = true;
				mainPanel.getRain().update = true;
				mainPanel.update = true;
				mainPanel.repaint();
				GUIManager.syncAllMainPanel(true);
			} else if (CommonProps.AC_T_ZOOM_OUT.equals(command)) {
				int zoom = radarBase.zoom / 2;
				if (zoom < radarBase.MIN_ZOOM) {
					return;
				}

				radarBase.xoffset += e.getX() - radarBase.center_X;
				radarBase.yoffset += e.getY() - radarBase.center_Y;
				radarBase.setZoom(zoom);
				mainPanel.getMap().update = true;
				mainPanel.getRain().update = true;
				mainPanel.update = true;
				mainPanel.repaint();
				GUIManager.syncAllMainPanel(true);
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		MainPanel mainPanel = (MainPanel)e.getComponent();
		mainPanel.setmX(e.getX());
		mainPanel.setmY(e.getY());

		if(GUIManager.isSyncTool()) {
			for (MainPanel panel : GUIManager.getJpanels()) {
				if (GUIManager.activeToolButton != null) {
					String command = GUIManager.activeToolButton.getActionCommand();
					if (!CommonProps.AC_T_CURSOR.equals(command)
					) {
						return;
					}
				}
				showVarValue(panel, panel.getRadarBase(), e.getX(), e.getY());
				ARCoord arc = PositionUtils.toARCoord(e.getX(), e.getY(), panel.getRadarBase());
				for (RHI rhi :RHI.rhis) {
					if (rhi.getRadarBase().equals(GUIManager.activeMainPanel.getRadarBase())) {
						if (rhi != null) {
							if (rhi.postion == 0) {
								RHI.azimuth = arc.azimuth;
								RHI.update();
							}
						}
					}
				}
			}
			GUIManager.syncAllMainPanelMouseMoved(e.getX(), e.getY());
		} else {
			if (GUIManager.activeToolButton != null) {
				String command = GUIManager.activeToolButton.getActionCommand();
				if (!CommonProps.AC_T_CURSOR.equals(command)
				) {
					return;
				}
			}
			showVarValue(mainPanel, mainPanel.getRadarBase(), e.getX(), e.getY());
			ARCoord arc = PositionUtils.toARCoord(e.getX(), e.getY(), mainPanel.getRadarBase());
			for (RHI rhi :RHI.rhis) {
				if (rhi.getRadarBase().equals(GUIManager.activeMainPanel.getRadarBase())) {
					if (rhi != null) {
						if (rhi.postion == 0) {
							RHI.azimuth = arc.azimuth;
							RHI.update();
						}
					}
				}
			}
			mainPanel.repaint();
		}
	}

}


