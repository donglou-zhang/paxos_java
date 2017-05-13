package com.paxos.simple;

/**
 * Created by Vincent on 2017/5/4.
 */
public class Acceptor implements Runnable{
    private Channel channel;
    private Network network;
    private String message;
    private int index;

    /* hPrep - highest prepare request to which it is already responded, 如hPrep=1表示已经回复了Proposer1的提议，
    下次如果收到Proposer0的提议，将不会回复
	   highAcc - highest proposal it has accepted
	 */
    private int hPrep;
    private int highAcc;

    // highAcc's corresponding value!!
    private int val;

    public Acceptor(Network network, int index) {
        this.network = network;
        channel = network.getChannel(index);
        this.index = index;
        hPrep = -1;
        val = -1;
        highAcc = -1;
        System.out.println("Acceptor " + index + " is created");
    }

    public void run() {
        String tokens[];
        int i, n;
        while(true) {
            while((message=channel.receiveMessage()) == null);

            System.out.println("Acceptor" + index + " received the message, and the message is: " + message);
            tokens = message.split(" ");

            if(tokens.length == 1) {
                n = Integer.parseInt(message);
                // promise the proposer!
                if(n > hPrep) {
                    message = highAcc + " " + val + " " + index + " " + n;
                    channel.sendMessage((n%network.getNumProposers()), message);
                    System.out.println("Acceptor" + index + " reply Proposer" + (n%network.getNumProposers()) + ", the response is: "+ message);
                    hPrep = n;
                }
            } else {
                n = Integer.parseInt(tokens[0]);
                if(n >= hPrep) {
                    highAcc = n;
                    val = Integer.parseInt(tokens[1]);
                    message = tokens[1] + " " + index;
                    for(i=network.getNumProposers()+network.getNumAcceptors();i<network.getTotalProcesses();i++) {
                        channel.sendMessage(i, message);
                        System.out.println("Acceptor" + index + " reply learner" + (i+network.getNumProposers()+network.getNumAcceptors()) + ", the response is: "+ message);
                    }
                }
            }
        }
    }
}
