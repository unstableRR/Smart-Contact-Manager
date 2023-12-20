package com.smartContactManager.model;

public class EmailRequest {

    private String subject;
    private String otp;
    private String to;

    public EmailRequest(){
    }

    public EmailRequest(String subject, String otp, String to) {
        this.subject = subject;
        this.otp = otp;
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "EmailRequest{" +
                "subject='" + subject + '\'' +
                ", otp='" + otp + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
