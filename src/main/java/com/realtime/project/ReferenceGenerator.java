package com.realtime.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import se.lth.control.*;

public class ReferenceGenerator extends Thread {

    private double amplitude;
    private int period;
    private double ref;

    private class RefGUI {
	private JPanel paramsLabelPanel = new JPanel();
	private JPanel paramsFieldPanel = new JPanel();
        private BoxPanel paramsPanel = new BoxPanel(BoxPanel.HORIZONTAL);
        private JTextField paramsAmpField = new JTextField();
        private JTextField paramsPeriodField = new JTextField();
  
    public ReferenceGenerator(double pos) {
	amplitude = pos;
    }
    
    public synchronized double getRef() 
    {
	return ref;
    }
    
    public void run() {
	period = 100;
	while (true) {
	    synchronized (this) {
		ref = amplitude;
	    }
	    try {
		sleep(period);
	    } catch (Exception x) {}
	}
    }
}

