package com.ads.uvlamp;

public class EspDevice {

    private int deviceNum;
    private String deviceIp;
    private String deviceMac;
    private String deviceHostname;
    private String deviceId;
    private String currentSsid;
    private String currentFirmware;

    public void setDeviceNum(int deviceNum){
        this.deviceNum = deviceNum;
    }


    public void setDeviceHostname(String deviceHostname) { this.deviceHostname = deviceHostname; }

    public int getDeviceNum() { return deviceNum; }

    public String getDeviceHostname() { return deviceHostname; }

    public String getCurrentFirmware() {
        return currentFirmware;
    }

    public String getCurrentSsid() {
        return currentSsid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setCurrentFirmware(String currentFirmware) {
        this.currentFirmware = currentFirmware;
    }

    public void setCurrentSsid(String currentSsid) {
        this.currentSsid = currentSsid;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
}
