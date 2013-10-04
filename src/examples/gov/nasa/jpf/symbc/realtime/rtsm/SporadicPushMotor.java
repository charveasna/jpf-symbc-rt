package gov.nasa.jpf.symbc.realtime.rtsm;


import javax.scj.SporadicParameters;
import javax.scj.SporadicThread;
public class SporadicPushMotor extends SporadicThread{
	Motor motor;
	
    final int IDLE = 0;
    final int FORWARD = 1;
    final int BACKWARD = 2;

    private int state = IDLE;

	public SporadicPushMotor(SporadicParameters sp, int motorNr){
		super(sp);
		motor = new Motor(motorNr);
	}

	public boolean run(){
//        switch(state) {
//        case IDLE:
//            motor.setMotorPercentage(Motor.STATE_FORWARD, false, 100);
//            state = FORWARD;
//            break;
//        case FORWARD:
//            motor.setMotorPercentage(Motor.STATE_BACKWARD, false, 100);
//            state = BACKWARD;
//            break;
//        case BACKWARD:
//            motor.setMotorPercentage(Motor.STATE_BRAKE, false, 100);
//            state = IDLE;
//            break;
//        }
        if (state == IDLE){
            //motor.setMotorPercentage(Motor.STATE_FORWARD, false, 100);
            state = FORWARD;
        } else if (state == FORWARD){
            //motor.setMotorPercentage(Motor.STATE_BACKWARD, false, 100);
            state = BACKWARD;
        } else if (state == BACKWARD){
            motor.setMotorPercentage(null, false, 100);
            state = IDLE;
        }
		return true;
	}
	
	protected boolean cleanup(){
		motor.setMotorPercentage(null, false, 0);
		//System.out.println("SporadicPushMotor cleanup!");
		return true;
	}
	
}
