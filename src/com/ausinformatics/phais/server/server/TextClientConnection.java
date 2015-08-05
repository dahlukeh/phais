package com.ausinformatics.phais.server.server;

import java.io.IOException;
import java.net.Socket;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

public class TextClientConnection implements ClientConnection {

    private int timeoutTime;
    private SocketTransport output;
    private InputPoller input;
    private boolean connected;

    private final static long PING_TIMEOUT = 3000;

    public TextClientConnection(int timeout, Socket socket) {
        timeoutTime = timeout;
        connected = true;
        try {
            output = new SocketTransport(socket);
            input = new InputPoller(output);
            new Thread(input).start();
        } catch (Exception e) {
            disconnect();
        }
    }

    @Override
    public void sendInfo(String s) {
        output.write(s);
    }

    @Override
    public void sendInfo(int i) {
        sendInfo(((Integer) i).toString());
    }

    @Override
    public String getStrInput() throws DisconnectedException {
        // We need to check for two things. First, that they don't time out.
        // Second, that they haven't disconnected.
        if (!isConnected()) {
            drop();
        }
        long curTime = System.currentTimeMillis();
        long lastPingTime = System.currentTimeMillis();
        String in = null;
        while (in == null) {
            if (System.currentTimeMillis() - curTime > timeoutTime) {
                sendInfo("ERROR Client took too long to respond");
                drop();
            }
            if (System.currentTimeMillis() - lastPingTime > PING_TIMEOUT) {
                sendInfo("ERROR Ping timeout");
                drop();
            }
            try {
                if (input.errorOccured()) {
                    drop();
                }
                if (input.inputAvailable()) {
                    in = input.getInput();
                    lastPingTime = System.currentTimeMillis();
                } else {
                    Thread.sleep(5);
                }
            } catch (Exception e) {
                e.printStackTrace();
                drop();
            }
        }
        return in;
    }

    @Override
    public int getIntInput() throws DisconnectedException {
        return Integer.parseInt(getStrInput());
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void disconnect() {
        try {
            connected = false;
            input.stop();
            output.close();
        } catch (IOException e) {}
    }

    @Override
    public String getAsync() {
        if (input.inputAvailable()) {
            return input.getInput();
        } else {
            return "";
        }
    }

    @Override
    public void sendMessage(Message message) {
        throw new UnsupportedOperationException("protobuf data transport not implemented in "
                + this.getClass().getCanonicalName());
    }

    @Override
    public void recvMessage(Builder builder) throws DisconnectedException {
        throw new UnsupportedOperationException("protobuf data transport not implemented in "
                + this.getClass().getCanonicalName());
    }

    @Override
    public void sendFatal(String s) {
        sendInfo("ERROR " + s);
        disconnect();
    }

    private void drop() throws DisconnectedException {
        disconnect();
        throw new DisconnectedException(this);
    }
}