package com.paxos.simple;

/**
 * Created by Vincent on 2017/5/4.
 */
public class Paxos {
    private Network network;

    public Paxos(Network network) {
        this.network = network;
    }

    public void runPaxos() {
        int i;
        //例如0-2：Proposer  3-6：Acceptor  7-9：Learner
        for(i = 0; i < network.getNumProposers(); i++)
        {
            (new Thread(new Proposer(network, i))).start();
        }
        for(i = network.getNumProposers(); i < network.getNumProposers() + network.getNumAcceptors(); i++)
        {
            (new Thread(new Acceptor(network, i))).start();
        }
        for(i = network.getNumProposers() + network.getNumAcceptors(); i < network.getTotalProcesses(); i++)
        {
            (new Thread(new Learner(network, i))).start();
        }
    }


}
