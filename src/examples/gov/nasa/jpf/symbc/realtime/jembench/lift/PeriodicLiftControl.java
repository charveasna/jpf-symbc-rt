package gov.nasa.jpf.symbc.realtime.jembench.lift;

import javax.scj.PeriodicParameters;
import javax.scj.PeriodicThread;

public class PeriodicLiftControl extends PeriodicThread {

	private final LiftControl ctrl;
	private final TalIo io;
	
	public PeriodicLiftControl(PeriodicParameters pp) {
		super(pp);
		this.ctrl = new LiftControl();
		this.io = new TalIo();
	}

	@Override
	protected boolean run() {
		ctrl.setVals();
		ctrl.getVals();
		ctrl.loop(io);
		return true;
	}

}
