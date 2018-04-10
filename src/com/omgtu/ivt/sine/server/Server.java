package com.omgtu.ivt.sine.server;

import com.omgtu.ivt.sine.ProjectConsts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {

    public Server() {
        connections = Collections.synchronizedList(new ArrayList<Connection>(5));
        try {
            serverSocket = new ServerSocket(ProjectConsts.PORT);
            System.out.println("Server is up @" + serverSocket);
            while (true) {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket);
                connections.add(connection);
                connection.setDaemon(true);
                connection.start();
            }

        } catch (BindException e) {
            System.out.println("Binding error. Another server is already running on port " + ProjectConsts.PORT);
        } catch (IOException e) {
            System.out.println("IO Exception. Closing app...");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            closeAllConnections();
        }
    }

    private void closeAllConnections() {
        try {
            synchronized (connections) {
                for (Connection c : connections) {
                    c.close();
                }
            }
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("Error: Connections was not closed.");
            e.printStackTrace();
        }
    }

    private class Connection extends Thread implements Runnable {

        private String userNickname = null;

        public Connection(Socket socket) {
            this.socket = socket;
            userNickname = "";
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {
            try {
                userNickname = in.readLine();
                System.out.println("Client \""+userNickname+"\" connected : " + socket.getLocalSocketAddress());
                synchronized (connections) {
                    for (Connection c : connections) {
                        c.out.println(userNickname + " joined");
                    }
                }

                String str = "";
                while (true) {
                    str = in.readLine();
                    if (str.equals("exit()")) break;

                    synchronized (connections) {
                        for (Connection c : connections) {
                            c.out.println(userNickname + ": " + str);
                        }
                    }
                }

                synchronized (connections) {
                    for (Connection c : connections) {
                        c.out.println(userNickname + " has left");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        private void close() {
            try {
                in.close();
                out.close();
                socket.close();

                connections.remove(this);
                if (connections.size() == 0) {
                    Server.this.closeAllConnections();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.out.println("Error: Connections was not closed.");
                e.printStackTrace();
            }
        }

        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;
    }

    private List<Connection> connections;
    private ServerSocket serverSocket;
}
