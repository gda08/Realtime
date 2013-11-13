package PC;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import SimEnvironment.*;

public class BeamAndBall extends VirtualProcess {

    private static final int stateNbr=3;  //antal tillst�nd
    private static final int inputNbr=2;  //antal ing�ngar
    private static final int outputNbr=2; //antal utg�ngar

    private double kPhi=4.4; //processkonstant f�r vinkel
    private double kX = 7.0; //processkonstant f�r kulan

    private double scale = 100.0;
    private RoundRectangle2D box = new RoundRectangle2D.Double(100.0, 80.0, 100.0, 50.0, 10.0, 10.0);
    private Ellipse2D axis = new Ellipse2D.Double(144, 109.0, 12.0, 12.0);
    private Rectangle2D beam = new Rectangle2D.Double(50.0, 120.0, 200.0, 5.0);
    private Ellipse2D ball = new Ellipse2D.Double(145, 112.0, 10.0, 10.0);
    private boolean init = false;

    public BeamAndBall() {
	super(stateNbr, inputNbr, outputNbr);
	Plotter plotter = new Plotter(3,100,10,-10);
	getSource(0).setPlotter(plotter,0);
	getSink(0).setPlotter(plotter,1);
	getSink(1).setPlotter(plotter,2);
    }
    
    public double[] computeOutput(double[] state, double[] input) {
	double[] output = new double[outputNbr];
	output[0] = state[0];                          //uppdatera kulans l�ge
	output[1] = state[2];                          //uppdatera bommens vinkel
	return output;
    }

    private double limit(double v, double min, double max) {
	if (v < min) {
	    v = min;
	} else {
	    if (v > max) {
		v = max;
	    }
	}
	return v;
    }

    public double[] updateState(double[] state, double[] input, double h) {
	double[] newState = new double[stateNbr];
	double ulim;
	ulim = limit(input[0],-10,10);
	newState[0] = state[0] + h*state[1];        //uppdatera kulans l�ge
	newState[1] = state[1] - kX*h*state[2];     //uppdatera kulans hastighet
	newState[2] = state[2] + kPhi*h*ulim;   //uppdatara bommens vinkel
	return newState;
    }

    public void draw(Graphics2D g2, JPanel jp, double[] state, 
		     double[] input, double[] output) {
//	if (!init) {
//	    jp.addMouseListener(new MouseListener() {
//		public void mouseClicked(MouseEvent e) {
//		    resetProcess();
//		    init = true;
//		}
//		public void mousePressed(MouseEvent e){}
//		public void mouseEntered(MouseEvent e){}
//		public void mouseReleased(MouseEvent e){}
//		public void mouseExited(MouseEvent e){}
//
//	    });
//	}
//		
//	scale = Math.min(jp.getWidth()/300.0, jp.getHeight()/200.0);
    }




} // BeamAndBall

