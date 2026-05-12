package com.kitty.radar.business.area;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.kitty.radar.MapOverlay;
import com.kitty.radar.RadarBase;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.util.PositionUtils;

/**
 * 区域JTable数据模型。
 */
public class AreaTableModel extends AbstractTableModel {

    private List areaList = new ArrayList();

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return areaList.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Area area = (Area) areaList.get(rowIndex);
        if (columnIndex == 0) {
            return area.getAreaName();
        } else if (columnIndex == 1) {
            return area.toString();
        } else {
            return area.isVisible();
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            Area area = (Area) areaList.get(rowIndex);
            area.setVisible(((Boolean) aValue).booleanValue());
            GUIManager.activeMainPanel.getMap().update = true;
//            MapOverlay.update = true;
            GUIManager.repaintCurrent();;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            return true;
        }
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 2) {
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "类别";
        } else if (column == 1) {
            return "描述";
        } else {
            return "显示";
        }
    }

    public void add(Area area, int index) {
        if (index == -1) {
            areaList.add(area);
        } else {
            areaList.set(index, area);
        }
    }

    public void remove(int[] rowIndexes) {
        for (int i = rowIndexes.length - 1; i >= 0; i--) {
            areaList.remove(rowIndexes[i]);
        }
        this.fireTableDataChanged();
//        MapOverlay.update = true;
        GUIManager.activeMainPanel.getMap().update = true;
        GUIManager.repaintCurrent();
    }

    public Area get(int index) {
        if (index < 0 || index > areaList.size() - 1) {
            return null;
        }
        return (Area) areaList.get(index);
    }

    public List getAreaList() {
        return areaList;
    }

    /**
     * 是否有区域正在显示
     * 
     * @return
     */
    public boolean isAreaVisible() {
        Iterator areas = areaList.iterator();
        while (areas.hasNext()) {
            if (((Area) areas.next()).isVisible()) {
                return true;
            }
        }
        return false;
    }

    public List getShapeList() {
        List shapeList = new ArrayList();
        Iterator areas = areaList.iterator();
        while (areas.hasNext()) {
            Area area = (Area) areas.next();
            if (area.isVisible()) {
                shapeList.add(area.toShape());
            }
        }
        return shapeList;
    }

    public boolean contains(double azimuth, double range, List shapeList, RadarBase radarBase) {
        XYCoord c = PositionUtils.toXYCoord(azimuth, range, radarBase);
        Iterator shapes = shapeList.iterator();
        while (shapes.hasNext()) {
            Shape shape = (Shape) shapes.next();
            if (shape.contains(c.x, c.y)) {
                return true;
            }
        }
        return false;
    }

}
