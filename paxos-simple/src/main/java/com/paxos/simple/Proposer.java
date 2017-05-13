package com.paxos.simple;

/**
 * Created by Vincent on 2017/5/4.
 */
public class Proposer implements Runnable {
    private Channel channel;
    private Network network;
    private int pNum, index;//pNum：提议者向接收者发出提议的编号，故初始化为提议者自身的编号
    private String message;

    public Proposer(Network network, int index) {
        this.network = network;
        channel = network.getChannel(index);
        pNum = index;
        this.index = index;
        System.out.println("Proposer " + index + " is created");
    }

    public void run() {
        int i;
        String tokens[];
        int highNa = -1, valA, nA, value = -1;
        int [] dest = new int[network.getNumAcceptors()];
        int timeout;
        while(true) {
            while(channel.isProposer() == false);
            //向所有的Acceptor发送提议
            for(i=network.getNumProposers();i<network.getNumAcceptors()+network.getNumProposers(); i++) {
                message = Integer.toString(pNum);
                channel.sendMessage(i, message);
                System.out.println("Proposer" + index + " send request to Acceptor" + i + ", and the request is: " + message);
            }

            i = 0;
            timeout = 10;
            do{
                while(timeout != 0) {
                    if((message = channel.receiveMessage()) == null) {
                        timeout--;
                        channel.sleep(500);
                        continue;
                    }
                    break;
                }
                if(timeout == 0) break;

                System.out.println("Proposer" + index + " received message, and the message is: " + message);
                tokens = message.split(" ");

                nA = Integer.parseInt(tokens[0]);
                valA = Integer.parseInt(tokens[1]);

                if(dest[Integer.parseInt(tokens[2])-network.getNumProposers()] == 0 && Integer.parseInt(tokens[3]) == pNum) {
                    dest[Integer.parseInt(tokens[2])-network.getNumProposers()] = Integer.parseInt(tokens[2]);
                    i++;
                    if(nA > highNa) {
                        value = valA;
                        highNa = nA;
                    }
                }
            } while(i < (network.getNumAcceptors()/2) +1);

            if(timeout == 0) {
                //进入确认提议阶段后，proposer向Acceptor确认提议编号，如果无法得到确认，则Proposer继续提议，编号自增
                // （自增量为Proposer的总数量，方便确认提议是由哪个proposer产生的）
                pNum += network.getNumProposers();
                continue;
            }

            if(value == -1) {
                value = channel.getInitialValueOfProposer();
            }

            message = pNum + " " + value;

            for(i=0;i<network.getNumAcceptors();i++) {
                if(dest[i] > 0) {
                    channel.sendMessage(dest[i], message);
                }
            }

            pNum += network.getNumProposers();
        }
    }
}
