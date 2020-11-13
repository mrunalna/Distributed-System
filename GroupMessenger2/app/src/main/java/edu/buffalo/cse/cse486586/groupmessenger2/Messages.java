package edu.buffalo.cse.cse486586.groupmessenger2;

import java.io.Serializable;

public class Messages {

    String message;
 //   int proposedPriority;
  //  int finalPriority;
    double proposedPriority;
    double finalPriority;
    int uniqueId;
    boolean isDeliverable;
    int squenceNo;
    int port;
    int inPort;

    public int getInPort() {
        return inPort;
    }

    public void setInPort(int inPort) {
        this.inPort = inPort;
    }

    int fifo_counter;
    boolean reply;


    public int getPort() {
        return port;
    }

    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Messages(String msg, int uniqueId, int sequenceNo){

        this.message = msg;
        this.finalPriority = -1;
        this.proposedPriority = -1;
        this.isDeliverable = false;
        this.uniqueId =uniqueId;
        this.squenceNo =sequenceNo;

    }

    public Messages(String msg, int sequenceNo , int uniqueId,int port,int inPort){

        this.message = msg;
        this.finalPriority = -1;
        this.proposedPriority = -1;
        this.isDeliverable = false;
        this.uniqueId =uniqueId;
        this.squenceNo =sequenceNo;
        this.inPort = inPort;
        this.port = port;

    }

 public Messages(String msg,int sequenceNo, int uniqueId, int port){

        this.message = msg;
        this.finalPriority = -1;
        this.proposedPriority = -1;
        this.isDeliverable = false;
        this.uniqueId =uniqueId;
        this.squenceNo =sequenceNo;
        this.port = port;

    }

    public Messages(String msg,int proposedPriority,int sequenceNo, int uniqueId,int inPort,int port){

        this.message = msg;
        this.finalPriority = -1;
        this.proposedPriority = proposedPriority;
        this.isDeliverable = false;
        this.uniqueId =uniqueId;
        this.squenceNo =sequenceNo;
        this.port =port;
        this.reply = false;
        this.inPort = inPort;
    }

    public int getSquenceNo() {
        return squenceNo;
    }

    public void setSquenceNo(int squenceNo) {
        this.squenceNo = squenceNo;
    }

    public  Messages(Messages msgs){

        this.message = msgs.message;
        this.finalPriority = msgs.finalPriority;
        this.proposedPriority = msgs.proposedPriority;
        this.isDeliverable = msgs.isDeliverable;
        this.uniqueId =msgs.uniqueId;
        this.squenceNo = msgs.squenceNo;
        this.reply = msgs.reply;
        this.port = msgs.port;
        this.inPort = msgs.inPort;


    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getProposedPriority() {
        return proposedPriority;
    }

    public void setProposedPriority(double proposedPriority) {
        this.proposedPriority = proposedPriority;
    }

    public double getFinalPriority() {
        return finalPriority;
    }

    public void setFinalPriority(double finalPriority) {
        this.finalPriority = finalPriority;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isDeliverable() {
        return isDeliverable;
    }

    public void setDeliverable(boolean deliverable) {
        isDeliverable = deliverable;
    }

    public String toString(){

        return "message: "+ this.message + " Proposed Priority: "+ this.proposedPriority +" ID: "+ this.squenceNo+"."+this.getUniqueId()
                + " final Priority: "+ this.finalPriority + " isDeliverable: "+ this.isDeliverable;
    }
}


