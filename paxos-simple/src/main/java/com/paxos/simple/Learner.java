package com.paxos.simple;

/**
 * Created by Vincent on 2017/5/4.
 */
public class Learner implements Runnable{
    private Channel channel;
    private Network network;
    private int index;
    private String tokens[];
    private String message;

    public Learner(Network network, int index) {
        this.network = network;
        this.channel = network.getChannel(index);
        this.index = index;
        System.out.println("Learner " + index + " is created");
    }

    public void run() {
        int i, value, acc_no, count;
        int from_acc[] = new int[network.getNumAcceptors()];
        for(i=0;i<network.getNumAcceptors();i++) {
            from_acc[i] = -1;
        }
        while(true) {
            while((message = channel.receiveMessage()) == null);
            tokens = message.split(" ");
            value = Integer.parseInt(tokens[0]);
            acc_no = Integer.parseInt(tokens[1]);
            from_acc[acc_no - network.getNumProposers()] = value;
            count = 0;
            for(i=0; i<network.getNumAcceptors(); i++) {
                if(value == from_acc[i]) {
                    count++;
                }
            }
            if(count >= (network.getNumAcceptors()/2)+1) {
                channel.decide(value);
            }
        }
    }
}
