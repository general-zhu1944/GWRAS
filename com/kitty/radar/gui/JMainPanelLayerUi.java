package com.kitty.radar.gui;

import com.kitty.radar.RadarBase;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class JMainPanelLayerUi extends LayerUI<MainPanel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//1 »­Ïß, 2 ²â¾à
	public static int active = 0;
	
    private RadarBase radarBase;
    
    private DrawlinePanel drawlinePanel;
    private MeasurePanel measurePanel;

    public JMainPanelLayerUi(RadarBase radarBase) {
        this.radarBase = radarBase;
        drawlinePanel = new DrawlinePanel(radarBase);
        measurePanel = new MeasurePanel(radarBase);
    }
    
    @Override
    public void installUI(JComponent c) {
	  super.installUI(c);
	  JLayer jlayer = (JLayer)c;
	  jlayer.setLayerEventMask(
	    AWTEvent.MOUSE_EVENT_MASK |
	    AWTEvent.MOUSE_MOTION_EVENT_MASK |
	    AWTEvent.MOUSE_WHEEL_EVENT_MASK
	  );
	}

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        if(1 == active) {
        	drawlinePanel.paint(g);
        }
        if(2 == active) {
        	measurePanel.paint(g);
        }
        
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends MainPanel> l) {
    	if(e.getID() == MouseEvent.MOUSE_WHEEL) {
    		
    	}
    	
        if(1 == active) {
            if (e.getID() == MouseEvent.MOUSE_PRESSED) 
            	drawlinePanel.mousePressed(e);
            if (e.getID() == MouseEvent.MOUSE_RELEASED) 
            	drawlinePanel.mouseReleased(e);
        }
        if(2 == active) {
            if (e.getID() == MouseEvent.MOUSE_PRESSED) 
            	measurePanel.mousePressed(e);
            if (e.getID() == MouseEvent.MOUSE_RELEASED) 
            	measurePanel.mouseReleased(e);
        }
    }
    
    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e, JLayer<? extends MainPanel> l) {
        if(1 == active) {
            if(e.getID() == MouseEvent.MOUSE_WHEEL)
            	drawlinePanel.mouseWheelMoved(e);
        }
        if(2 == active) {
            if (e.getID() == MouseEvent.MOUSE_WHEEL) 
            	measurePanel.mouseWheelMoved(e);
        }
    }
    
    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends MainPanel> l) {
        if(1 == active) {
    	    if (e.getID() == MouseEvent.MOUSE_MOVED) 
    	    	drawlinePanel.mouseMoved(e);
    	    if (e.getID() == MouseEvent.MOUSE_DRAGGED)  {
    	    	drawlinePanel.mouseDragged(e);
    	    }
        }
        if(2 == active) {
    	    if (e.getID() == MouseEvent.MOUSE_MOVED) 
    	    	measurePanel.mouseMoved(e);
    	    if (e.getID() == MouseEvent.MOUSE_DRAGGED)  {
    	    	measurePanel.mouseDragged(e);
    	    }
        }
    }
    
}

