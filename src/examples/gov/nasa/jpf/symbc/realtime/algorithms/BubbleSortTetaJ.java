/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.algorithms;

import javax.scj.PeriodicParameters;
import javax.scj.PeriodicThread;

import gov.nasa.jpf.symbc.Debug;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * Also used in TetaJ
 */
public class BubbleSortTetaJ extends PeriodicThread {
	public int[] a = {Debug.makeSymbolicInteger("Symb1"),Debug.makeSymbolicInteger("Symb2"),Debug.makeSymbolicInteger("Symb3"),Debug.makeSymbolicInteger("Symb4"), Debug.makeSymbolicInteger("Symb5")};
	
	//public int[] a = {5,4,3,2,1};
	//public static int[] a = {1,2,3,4};
	
	public static void main(String args[]) {
		//new BubbleSortTetaJ(new PeriodicParameters(200));
		//new BubbleSortTetaJ(new PeriodicParameters(200));
		new BubbleSortTetaJ(new PeriodicParameters(2200)).bubble_srt();
	}

	public BubbleSortTetaJ(PeriodicParameters pp) {
		super(pp);
	}
	
	public void bubble_srt(){
		int i, j, t=0, n = 5;
		for(i = 0; i < n; i++){
			for(j = 1; j < (n-i); j++){
				if(a[j-1] > a[j]){
					t = a[j-1];
					a[j-1]=a[j];
					a[j]=t;
				}
			}
		}
	}

	@Override
	public boolean run() {
		int i, j, t=0, n = 5;
		//@loopbound = 1
		for(i = 0; i < n; i++){
			//@loopbound = 1
			for(j = 1; j < (n-i); j++){
				if(a[j-1] > a[j]){
					t = a[j-1];
					a[j-1]=a[j];
					a[j]=t;
				}
			}
		}
		return false;
	}
	
}
