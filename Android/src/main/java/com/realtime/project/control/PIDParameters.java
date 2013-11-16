package com.realtime.project.control;

public class PIDParameters implements Cloneable {
    public double K;
    public double Ti;
    public double Tr;
    public double Td;
    public double N;
    public double Beta;
    public double H;
    public boolean integratorOn;

    public synchronized Object clone() {
		try {
		    return super.clone();
		} catch (Exception x) {
			return null;
		}
    }
}
