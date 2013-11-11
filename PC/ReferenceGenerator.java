package com.realtime.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import se.lth.control.*;

public class ReferenceGenerator{

    private int period;
    private double ref;

   
  
    
    public ReferenceGenerator(double pos) {
	ref = pos;
    }
    public synchronized void setRef(double pos){
    	ref = pos;
    }
    public synchronized double getRef() 
    {
	return ref;
    }
    
    

}

