package com.example.rx8;

import android.graphics.Color;

public class Employee {
    private String name;
    private String cpf;
    private String dateOfAdmission;
    private String position;
    private double salary;
    private String paymentStatus;

    private String id;

    // Construtor
    public Employee(String name, String cpf, String dateOfAdmission, String position, double salary, String paymentStatus, String id) {
        this.name = name;
        this.cpf = cpf;
        this.dateOfAdmission = dateOfAdmission;
        this.position = position;
        this.salary = salary;
        this.paymentStatus = paymentStatus;
        this.id = id;
    }

    // Getters e setters (você pode gerar automaticamente no Android Studio)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDateOfAdmission() {
        return dateOfAdmission;
    }

    public void setDateOfAdmission(String dateOfAdmission) {
        this.dateOfAdmission = dateOfAdmission;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
