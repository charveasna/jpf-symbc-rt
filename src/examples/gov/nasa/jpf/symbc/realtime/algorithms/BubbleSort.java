package gov.nasa.jpf.symbc.realtime.algorithms;

import gov.nasa.jpf.symbc.Debug;

import javax.scj.PeriodicParameters;
import javax.scj.PeriodicThread;
import javax.scj.RealtimeSystem;

public class BubbleSort extends PeriodicThread {

	private static final int SIZE = 2;

	public static void main(String[] args) {
		//new BubbleSort(new PeriodicParameters(100));
		//new BubbleSort(new PeriodicParameters(100));
		
		//RealtimeSystem.start();
		BubbleSort sort = new BubbleSort(new PeriodicParameters(200));
		sort.runBubbleSort();
	}
	
	public BubbleSort(PeriodicParameters pp) {
		super(pp);
	}
	
	public void runBubbleSort() {
		int i;
		int s = SIZE;
		int ar[] = getNumbers();
		int tmp;
		boolean repeat;

		repeat=true;
		s=(2*SIZE)-1;
		//The upper bound here should be SIZE^2
		//@loopbound = 16
		while(repeat){
			repeat=false;
			//The upper bound here should be SIZE - 1
			//@loopbound = 3
			for(i=0;i<s;i++) {
				if(ar[i]>ar[i+1]){
					tmp=ar[i];
					ar[i]=ar[i+1];
					ar[i+1]=tmp;
					repeat=true;
				}
			}
		}
	}
	
	public int[] getNumbers() {
		int[] array = new int[2*SIZE];
		int s =  SIZE;
		for(int i=0;i<s;i++){
			array[i]=Debug.makeSymbolicInteger("SYMB");//2*i;
			array[s-i]=Debug.makeSymbolicInteger("SYMB");//(2*i)+1;
		}
		return array;
	}
	
	@Override
	public boolean run() {
		runBubbleSort();
		return true;
	}

}
