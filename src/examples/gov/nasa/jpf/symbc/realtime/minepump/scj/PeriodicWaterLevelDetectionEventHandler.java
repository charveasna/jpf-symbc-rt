/*******************************************************************************
 * Copyright (c) 2010
 *     Andreas Engelbredt Dalsgaard
 *     Casper Jensen 
 *     Christian Frost
 *     Kasper Luckow.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Andreas Engelbredt Dalsgaard <andreas.dalsgaard@gmail.com> - Changes to run on  jop SCJ implementation
 *     Casper Jensen <semadk@gmail.com> - Initial implementation
 *     Christian Frost <thecfrost@gmail.com> - Initial implementation
 *     Kaspuckow <luckow@cs.aau.dk> - Initial implementation
 ******************************************************************************/
package gov.nasa.jpf.symbc.realtime.minepump.scj;

import gov.nasa.jpf.symbc.realtime.minepump.actuators.WaterpumpActuator;
import gov.nasa.jpf.symbc.realtime.minepump.sensors.HighWaterSensor;
import gov.nasa.jpf.symbc.realtime.minepump.sensors.LowWaterSensor;
import gov.nasa.jpf.symbc.realtime.minepump.sensors.MethaneSensor;

import javax.scj.PeriodicParameters;
import javax.scj.PeriodicThread;


public class PeriodicWaterLevelDetectionEventHandler extends PeriodicThread
{
	private HighWaterSensor highWaterSensor;
	private LowWaterSensor lowWaterSensor;
	private WaterpumpActuator waterpumpActuator;
	

	public static void main(String[] args) {
		int criticalMethaneLevel = 2;
		int brickHistorySize = 5;
		HighWaterSensor hSens = new HighWaterSensor(1, 2);
		LowWaterSensor lSens = new LowWaterSensor(2, 2);
		
		WaterpumpActuator w = new WaterpumpActuator(0);
		PeriodicWaterLevelDetectionEventHandler l = new PeriodicWaterLevelDetectionEventHandler(
				new PeriodicParameters(2000),
				hSens, lSens, w);
		
		l.run();
	}
	
	public PeriodicWaterLevelDetectionEventHandler(
			PeriodicParameters parameters, 
			HighWaterSensor highWaterSensor, LowWaterSensor lowWaterSensor,
			WaterpumpActuator waterpumpActuator) {
		super(parameters);
		
		this.highWaterSensor = highWaterSensor;
		this.lowWaterSensor = lowWaterSensor;
		this.waterpumpActuator = waterpumpActuator;
	}

	@Override
	public boolean run() {
		if (this.highWaterSensor.criticalWaterLevel()) {
		    this.waterpumpActuator.emergencyStop(true);
		}
		else if (this.lowWaterSensor.criticalWaterLevel()) {
		    this.waterpumpActuator.start();
		}
		// What is this return value for?
		return true;
	}
}
