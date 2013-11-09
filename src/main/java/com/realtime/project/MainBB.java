package com.realtime.project;

public class MainBB {
	public static void main(String[] argv) {
		final int regulPriority = 8;

		BeamAndBall bb = new BeamAndBall();

		ReferenceGenerator refgen = new ReferenceGenerator(20.0, 4.0);
//		BeamAndBallRegul regul = new BeamAndBallRegul(refgen, bb, regulPriority);

		refgen.start();
		//	try { Thread.sleep(10000); } catch (Exception x) {}
		regul.start();
	}
}

