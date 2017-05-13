package com.paxos.simple;

/**
 * Created by Vincent on 2017/5/4.
 */
public class Application {
    public static void main(String[] args) {
        Network network = new Network(2,3,3);
        Paxos paxos = new Paxos(network);
        paxos.runPaxos();
    }
}
