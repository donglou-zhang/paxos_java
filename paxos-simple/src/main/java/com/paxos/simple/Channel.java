package com.paxos.simple;

/**
 * Created by Vincent on 2017/5/4.
 */
public class Channel {
    private Network network;
    private int index;

    public Channel() {

    }

    /**
     * 将消息发送到指定进程的消息队列
     * @param destination
     * @param message
     */
    public void sendMessage(int destination, String message) {
        synchronized (network.getQueues()[destination]) {
            network.getQueues()[destination].add(message);
        }
    }

    /**
     * 从消息队列中读取消息
     * @return
     */
    public String receiveMessage() {
        synchronized (network.getQueues()[index]) {
            if(!network.getQueues()[index].isEmpty()) {
                return network.getQueues()[index].remove();
            } else {
                return null;
            }
        }
    }

    /**
     * 判断是否是提议者
     * @return
     */
    public boolean isProposer() {
        if(index < network.getNumProposers()) {
            return true;
        } else if(index >= network.getNumProposers()){
            throw new Error("Non-proposers should not be asking whether they are distinguished");
        }
        return false;
    }

    public void decide(int decision) {
        if(index < (network.getNumProposers() + network.getNumAcceptors())) {
            throw new Error("Non-learner should not be deciding a value");
        }

        if(decision >= network.getNumProposers()) {
            throw new Error("The decided value was not an initial value...");
        }

        synchronized (network) {
            if(network.getDecision() == -1) {
                network.setDecision(decision);
            } else {
                if(network.getDecision() != decision) {
                    System.out.println("Disagreement between Learners");
                }
            }
        }
    }

    public int getInitialValueOfProposer() {
        if(index >= network.getNumProposers()) {
            throw new Error("Non-proposers should not be asking for initial value");
        }
        return index;
    }

    public void sleep(int millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch(InterruptedException e) {
            System.out.println("Interrupted");
        }
    }

    public void setNetwork(Network network){
        this.network = network;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
