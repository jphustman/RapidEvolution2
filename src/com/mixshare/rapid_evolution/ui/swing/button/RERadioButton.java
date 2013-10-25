package com.mixshare.rapid_evolution.ui.swing.button;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JRadioButton;
import javax.swing.JToolTip;

import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.ui.swing.tooltip.CustomToolTip;

public class RERadioButton extends JRadioButton {
   
    public RERadioButton() {
        super();
    }
    
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        if (rapid_evolution.RapidEvolution.aaEnabled)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2);
    }
    
    private JToolTip tooltip;  
    public JToolTip createToolTip() {
        if (SkinManager.instance.use_custom_tooltips) {
            if (tooltip == null) {
                tooltip = new CustomToolTip();
                tooltip.setComponent(this);            
            }
            return tooltip;
        } else {
            return super.createToolTip();
        }
    }    
    
}
