package gov.nasa.jpf.symbc.realtime.rtsm;

import gov.nasa.jpf.symbc.realtime.rtsm.util.BoundedBuffer;

import javax.scj.PeriodicParameters;
import javax.scj.RealtimeSystem;
import javax.scj.PeriodicThread;

public class PeriodicMotorSpooler extends PeriodicThread {

    private final int WHITE_PUSHOFF_TIME = 642;
    private final int BLUE_PUSHOFF_TIME = 1054;
    
    private final int REVERSE_OFFSET = 80;
    private final int STOP_OFFSET = 180;

    BoundedBuffer whiteBuffer, blueBuffer;
    
    private int whiteState = 0;
    private int blueState = 0;
    
    public PeriodicMotorSpooler(PeriodicParameters pp) {
    	super(pp);
        this.whiteBuffer = new BoundedBuffer();
        this.blueBuffer = new BoundedBuffer();
    }

    public void add(int color) {
        if (color == 2){
            //whiteBuffer.enqueue(RealtimeSystem.currentTimeMicros());
        } else if (color == 4){
            blueBuffer.enqueue(1000);
        }
    }

    public boolean run() {
        int result;
        if (!whiteBuffer.isEmpty()) {
            result = whiteBuffer.peek();
            if (whiteState == 0){
	            if ((1000 - result) >= WHITE_PUSHOFF_TIME) {
	                RealtimeSystem.fire(2);
	                whiteState = 1;
	            }
            } else if (whiteState == 1){
	            if ((1000 - result) >= WHITE_PUSHOFF_TIME + REVERSE_OFFSET) {
	                RealtimeSystem.fire(2);
	                whiteState = 2;
	            }
            } else if (whiteState == 2){
	            if ((1000 - result) >= WHITE_PUSHOFF_TIME + STOP_OFFSET) {
	                whiteBuffer.dequeue();
	                RealtimeSystem.fire(2);
	                whiteState = 0;
	            }
            }
        }
        if (!blueBuffer.isEmpty()) {
            result = blueBuffer.peek();
            if (blueState == 0){
	            if ((1000 - result) >= BLUE_PUSHOFF_TIME) {
	                //RealtimeSystem.fire(4);
	                blueState = 1;
	            }
            } else if (blueState == 1){
	            if ((1000 - result) >= BLUE_PUSHOFF_TIME + REVERSE_OFFSET) {
	                //RealtimeSystem.fire(4);
	                blueState = 2;
	            }
            } else if (blueState == 2){
	            if ((1000 - result) >= BLUE_PUSHOFF_TIME + STOP_OFFSET) {
	                whiteBuffer.dequeue();
	                RealtimeSystem.fire(4);
	                blueState = 0;
	            }
            }
        }
        return true;
    }
    
    protected boolean cleanup() {
        //System.out.println("PeriodicReadSensor cleanup!");
        return true;
    }
}
