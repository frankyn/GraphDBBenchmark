package com.silvertower.app.bench.akka;


import akka.actor.ActorRef;

public class SlaveReference {
	private ActorRef slaveRef;
	private int nbCoresAvailable;
	private boolean working;
	public SlaveReference(ActorRef slaveRef, int nbCoresAvailable) {
		this.slaveRef = slaveRef;
		this.nbCoresAvailable = nbCoresAvailable;
		this.working = false;
	}
	
	public ActorRef getSlaveRef() {
		return slaveRef;
	}
	
	public int getNbCoresAvailable() {
		return nbCoresAvailable;
	}
	
	public void setWorking() {
		working = true;
	}
	
	public void unsetWorking() {
		working = false;
	}
	
	public boolean isWorking() {
		return working;
	}
}
