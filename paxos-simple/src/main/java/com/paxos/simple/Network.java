package com.paxos.simple;

import javax.swing.event.ChangeEvent;
import java.util.LinkedList;

/**
 * Created by Vincent on 2017/5/4.
 */
public class Network {
    private int totalProcesses;
    private int numProposers;
    private int numAcceptors;
    private int numLearners;
    private int decision = -1;

    private LinkedList<String>[] queues;

    public Network(int numProposers, int numAcceptors, int numLearners) {
        totalProcesses = numAcceptors + numProposers + numLearners;
        queues = new LinkedList[totalProcesses];
        int i;
        for(i = 0;i < totalProcesses;i++) {
            queues[i] = new LinkedList<String>();
        }
        this.numAcceptors = numAcceptors;
        this.numProposers = numProposers;
        this.numLearners = numLearners;
    }

    public int getNumProposers() {
        return numProposers;
    }

    public int getNumAcceptors() {
        return numAcceptors;
    }

    public int getNumLearners() {
        return numLearners;
    }

    public int getTotalProcesses() {
        return totalProcesses;
    }

    public LinkedList<String>[] getQueues() {
        return queues;
    }

    public int getDecision() {
        return decision;
    }

    public void setDecision(int decision) {
        this.decision = decision;
    }

    public Channel getChannel(int processID) {
        if(processID < 0 || processID >= totalProcesses) {
            throw new Error("Invalid process ID.");
        }
        Channel channel = new Channel();
        channel.setIndex(processID);
        channel.setNetwork(this);
        return channel;
    }
}
