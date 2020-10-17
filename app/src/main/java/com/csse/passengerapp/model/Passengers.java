package com.csse.passengerapp.model;

/**
 * This is the model class for passenger application
 *
 * @author Dimuthu Abeysinghe
 * @version 1.0
 */

public class Passengers {
    private String passengerName;
    private String passengerAddress;
    private Integer passengerMobileNo;
    private String passengerPassword;
    private String passengerNIC;
    private Double passengerCreditLimit;
    private String passengerType;
    private String registeredDate;

    public Passengers(String passengerName, String passengerAddress, Integer passengerMobileNo, String passengerPassword, String passengerNIC, Double passengerCreditLimit, String passengerType, String registeredDate) {
        this.passengerName = passengerName;
        this.passengerAddress = passengerAddress;
        this.passengerMobileNo = passengerMobileNo;
        this.passengerPassword = passengerPassword;
        this.passengerNIC = passengerNIC;
        this.passengerCreditLimit = passengerCreditLimit;
        this.passengerType = passengerType;
        this.registeredDate = registeredDate;
    }

    public Passengers() {
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerAddress() {
        return passengerAddress;
    }

    public void setPassengerAddress(String passengerAddress) {
        this.passengerAddress = passengerAddress;
    }

    public Integer getPassengerMobileNo() {
        return passengerMobileNo;
    }

    public void setPassengerMobileNo(Integer passengerMobileNo) {
        this.passengerMobileNo = passengerMobileNo;
    }

    public String getPassengerPassword() {
        return passengerPassword;
    }

    public void setPassengerPassword(String passengerPassword) {
        this.passengerPassword = passengerPassword;
    }

    public String getPassengerNIC() {
        return passengerNIC;
    }

    public void setPassengerNIC(String passengerNIC) {
        this.passengerNIC = passengerNIC;
    }

    public Double getPassengerCreditLimit() {
        return passengerCreditLimit;
    }

    public void setPassengerCreditLimit(Double passengerCreditLimit) {
        this.passengerCreditLimit = passengerCreditLimit;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
