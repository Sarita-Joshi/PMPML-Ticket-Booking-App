package com.example.myApplication.classes;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Ticket {

    private String source;
    private String destination;
    private String busNo;
    private String ticketNo;
    private String tripNo;
    private Timestamp timestamp;
    private String depot;
    private ArrayList<Integer> people;
    private int total;

    public Ticket(String source, String destination, String busNo, String ticketNo, String tripNo, Timestamp timestamp,
                  String depot, ArrayList<Integer> people, int total) {
        this.source = source;
        this.destination = destination;
        this.busNo = busNo;
        this.ticketNo = ticketNo;
        this.tripNo = tripNo;
        this.timestamp = timestamp;
        this.depot = depot;
        this.people = people;
        this.total = total;
    }

    public Ticket(){

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDepot() {
        return depot;
    }

    public void setDepot(String depot) {
        this.depot = depot;
    }

    public String getPeople() {
        return new String(people.get(0) + "\n" + people.get(1));
    }

    public void setPeople(ArrayList<Integer> people) {
        this.people = people;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
