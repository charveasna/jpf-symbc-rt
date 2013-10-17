package gov.nasa.jpf.symbc.realtime.rtsm;

import javax.scj.PeriodicParameters;
import javax.scj.PeriodicThread;

public class PeriodicReadSensor extends PeriodicThread {

	final int TRESHOLD = 35;

	// Motor related
	Motor conveyorMotor;
	final int CONVEYOR_SPEED = 25;

	// Sensor related
	boolean awaitingBrick = true;
	boolean buttonDown = false;

	int lastRead = 0;

	// Tuning
	final int BRICK_DETECTED = 250;
	final int BLUE_DETECTED = 285;

	// Spooler related
	PeriodicMotorSpooler motorSpooler;
	Sensors sens;
	public PeriodicReadSensor(PeriodicParameters pp, PeriodicMotorSpooler motorSpooler) {
		super(pp);
		conveyorMotor = new Motor(2);
		sens = new Sensors();
		conveyorMotor.setMotorPercentage(null, false,
				CONVEYOR_SPEED);
		this.motorSpooler = motorSpooler;
	}

	public boolean run() {
		sens.synchronizedReadSensors();
		int input = sens.getBufferedSensor(0);

		if (awaitingBrick) {
			if (input > lastRead) {
				lastRead = input;
			} else if ((lastRead - input) >= TRESHOLD) {
				awaitingBrick = false;
				if (lastRead > BRICK_DETECTED) {
					brickFound(lastRead);
					//System.out.println("Brick found, LastRead = " + lastRead);
				}
			}
		} else {
			if (input < lastRead) {
				lastRead = input;
			} else if ((input - lastRead) >= TRESHOLD) {
				awaitingBrick = true;
			}
		}
		
		return true;
	}
	private void brickFound(int brick) {
		if (brick > BLUE_DETECTED) {
			//System.out.println("blue");
			this.motorSpooler.add(4);
		} else {
			//System.out.println("white");
			this.motorSpooler.add(2);
		}
	}

	protected boolean cleanup() {
		conveyorMotor.setMotorPercentage(null, false, 0);
		//System.out.println("PeriodicReadSensor cleanup!");
		return true;
	}
}
