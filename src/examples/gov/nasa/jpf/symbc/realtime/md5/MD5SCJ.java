package gov.nasa.jpf.symbc.realtime.md5;

/**
 *  This file is part of oSCJ.
 *
 *   oSCJ is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   oSCJ is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with oSCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *   Copyright 2009, 2010
 *   @authors  Lei Zhao, Ales Plsek
 */


import gov.nasa.jpf.symbc.Debug;
import gov.nasa.jpf.symbc.realtime.md5.util.MyMD5Input;

import javax.scj.PeriodicParameters;
import javax.scj.PeriodicThread;


public class MD5SCJ extends PeriodicThread {

	private static byte[] bytes={0,1,2,34};
	MyMD5Input myMD;
    public MD5SCJ(PeriodicParameters per) {
        super(per);
        myMD = new MyMD5Input();
    }

	/* (non-Javadoc)
	 * @see javax.scj.RealtimeThread#run()
	 */
	@Override
	public boolean run() {
        //for (String in : Constants.input) {
            
            //myMD.run(bytes);
		myMD.read((byte)Debug.makeSymbolicInteger("SYMB"));
        //    myMD.run(in);
            //myMD.finalHash(in);
        //}
		return true;
	}
}
