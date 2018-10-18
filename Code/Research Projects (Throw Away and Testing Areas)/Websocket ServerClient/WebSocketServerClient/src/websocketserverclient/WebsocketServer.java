/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocketserverclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author Patryk
 */
public class WebsocketServer extends WebSocketServer
{

    public WebsocketServer(int port) throws UnknownHostException
    {
        super(new InetSocketAddress(port));
    }

    public WebsocketServer(InetSocketAddress address)
    {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        broadcast("new connection: " + handshake.getResourceDescriptor());
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " Connected");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        broadcast(conn + " Disconnected");
        System.out.println(conn + " Disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        broadcast( message );
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message)
    {
        broadcast(message.array());
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onFragment(WebSocket conn, Framedata fragment)
    {
        System.out.println("received fragment: " + fragment);
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart()
    {
        System.out.println("Server started!");
    }

}
