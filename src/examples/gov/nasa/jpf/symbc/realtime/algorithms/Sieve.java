package gov.nasa.jpf.symbc.realtime.algorithms;

import javax.scj.PeriodicParameters;
import javax.scj.PeriodicThread;
import javax.scj.RealtimeSystem;

public class Sieve extends PeriodicThread {

	private static final int sieveSize = 100;
	private final boolean  flags[];

	public static void main(String[] args) {
		/*new sieve(new PeriodicParameters(100));
		new sieve(new PeriodicParameters(100));
		
		RealtimeSystem.start();*/
		
		Sieve s = new Sieve(new PeriodicParameters(200));
		s.run();
	}
	
	public Sieve(PeriodicParameters pp) {
		super(pp);
		flags = new boolean[sieveSize+1];
	}

	@Override
	public boolean run() {
		int i, prime, k, count;
		count=0;
		for(i=0; i<=sieveSize; i++) 
			flags[i]=true;
		for (i=0; i<=sieveSize; i++) {
			if(flags[i]) {
				prime=i+i+3;
				for(k=i+prime; k<=sieveSize; k+=prime)
					flags[k]=false;
				count++;
			}
		}
		return true;
	}

}
