package com.example.demo.Model;

public class TelemetryLocReq {
	// private long time;
	private String deviceId;

	private float lat;
	private float lon;

	private float alt;
	private float acc;

	private float speed;
	private float bear;

	private int flags;
	private float batStatus;

	private int emergency;

	public TelemetryLocReq() {
		super();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	public float getAlt() {
		return alt;
	}

	public void setAlt(float alt) {
		this.alt = alt;
	}

	public float getAcc() {
		return acc;
	}

	public void setAcc(float acc) {
		this.acc = acc;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getBear() {
		return bear;
	}

	public void setBear(float bear) {
		this.bear = bear;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public float getBatStatus() {
		return batStatus;
	}

	public void setBatStatus(float batStatus) {
		this.batStatus = batStatus;
	}

	public int getEmergency() {
		return emergency;
	}

	public void setEmergency(int emergency) {
		this.emergency = emergency;
	}


}
