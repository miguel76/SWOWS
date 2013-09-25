/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.tuio;

import java.util.HashSet;
import java.util.Set;

import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

public class TuioSmoother implements TuioListener {
	
	private TuioListener innerListener;

	private Set<TuioObject> addedObjects = new HashSet<TuioObject>();
	private Set<TuioObject> updatedObjects = new HashSet<TuioObject>();
	private Set<TuioObject> deletedObjects = new HashSet<TuioObject>();
	private Set<TuioObject> ghostObjects = new HashSet<TuioObject>();
	private Set<TuioCursor> addedCursors = new HashSet<TuioCursor>();
	private Set<TuioCursor> updatedCursors = new HashSet<TuioCursor>();
	private Set<TuioCursor> deletedCursors = new HashSet<TuioCursor>();
	private Set<TuioCursor> ghostCursors = new HashSet<TuioCursor>();
	
	private TuioTime lastRefreshTime = null;

	private Thread notifyingThread = new Thread() {
		private TuioTime currRefreshTime;
		@Override
		public void run() {
			while (true) {
				//System.out.println("Waiting for events in TUIO smoother...");
				while (lastRefreshTime == null) yield();
				//System.out.println("Waiting for lock for events passing in TUIO smoother...");
				synchronized (TuioSmoother.this) {
					currRefreshTime = lastRefreshTime;
					//System.out.println("Beginning events passing in TUIO smoother");
					for (TuioObject tobj : addedObjects) {
						innerListener.addTuioObject(tobj);
					}
					addedObjects.clear();
					for (TuioObject tobj : updatedObjects) {
						innerListener.updateTuioObject(tobj);
					}
					updatedObjects.clear();
					for (TuioObject tobj : deletedObjects) {
						innerListener.removeTuioObject(tobj);
					}
					deletedObjects.clear();
					for (TuioCursor tcur : addedCursors) {
						innerListener.addTuioCursor(tcur);
					}
					addedCursors.clear();
					for (TuioCursor tcur : updatedCursors) {
						innerListener.updateTuioCursor(tcur);
					}
					updatedCursors.clear();
					for (TuioCursor tcur : deletedCursors) {
						innerListener.removeTuioCursor(tcur);
					}
					deletedCursors.clear();
					lastRefreshTime = null;
					//System.out.println("Ended events passing in TUIO smoother");
				}
				//System.out.println("Beginning inner refresh in TUIO smoother");
				innerListener.refresh(currRefreshTime);
				//System.out.println("Ended inner refresh in TUIO smoother");
			}
		}
	}; 

	public TuioSmoother(TuioListener innerListener) {
		this.innerListener = innerListener;
		notifyingThread.start();
	}
	
	public void addTuioObject(TuioObject tobj) {
		synchronized (this) {
			if (!deletedObjects.contains(tobj) && !ghostObjects.contains(tobj)) {
				updatedObjects.remove(tobj);
				addedObjects.add(tobj);
			}
		}
	}

	public void updateTuioObject(TuioObject tobj) {
		synchronized (this) {
//			System.out.println(
//					"TUIO Smoother: updating object " + tobj
//							+ " ( x:" + tobj.getX() + ", y:" + tobj.getY() + ")");
			if (!deletedObjects.contains(tobj) && !addedObjects.contains(tobj) && !ghostObjects.contains(tobj))
				updatedObjects.add(tobj);
//			System.out.println(
//					"TUIO Smoother: updated object " + tobj);
		}
	}

	public void removeTuioObject(TuioObject tobj) {
		synchronized (this) {
			if (!ghostObjects.contains(tobj)) {
				if (addedObjects.contains(tobj)) {
					addedObjects.remove(tobj);
					ghostObjects.add(tobj);
				} else {
					updatedObjects.remove(tobj);
					deletedObjects.add(tobj);
				}
			}
		}
	}

	public void addTuioCursor(TuioCursor tcur) {
		synchronized (this) {
			//System.out.println("Adding cursor " + tcur + " in TUIO smoother");
			if (!deletedCursors.contains(tcur) && !ghostCursors.contains(tcur)) {
				updatedCursors.remove(tcur);
				addedCursors.add(tcur);
			}
			//System.out.println("Added cursor " + tcur + " in TUIO smoother");
		}
	}

	public void updateTuioCursor(TuioCursor tcur) {
		synchronized (this) {
//			System.out.println(
//					"TUIO Smoother: updating cursor " + tcur
//					        //+ " " + point2nodeMapping.get(cursor)
//							+ " ( x:" + tcur.getX() + ", y:" + tcur.getY() + ")");
			if (!deletedCursors.contains(tcur) && !addedCursors.contains(tcur) && !ghostCursors.contains(tcur))
				updatedCursors.add(tcur);
//			System.out.println(
//					"TUIO Smoother: Updated cursor " + tcur);
		}
	}

	public void removeTuioCursor(TuioCursor tcur) {
		synchronized (this) {
			//System.out.println("Removing cursor " + tcur + " in TUIO smoother");
			if  ( !ghostCursors.contains(tcur) ) {
				if (addedCursors.contains(tcur)) {
					addedCursors.remove(tcur);
					ghostCursors.add(tcur);
				} else {
					updatedCursors.remove(tcur);
					deletedCursors.add(tcur);
				}
			}
			//System.out.println("Removed cursor " + tcur + " in TUIO smoother");
		}
	}

	public void refresh(TuioTime ftime) {
		//System.out.println("Refresh called in TUIO smoother");
		lastRefreshTime = ftime;
	}

}
