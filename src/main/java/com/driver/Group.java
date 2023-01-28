package com.driver;

public class Group {
    private String name;
    private int numberOfParticipants;

    public Group(){}
    public Group(String name, int noOfParticipants){
        this.name = name;
        this.numberOfParticipants = noOfParticipants;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getNumberOfParticipants(){
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants){
        this.numberOfParticipants = numberOfParticipants;
    }

}
