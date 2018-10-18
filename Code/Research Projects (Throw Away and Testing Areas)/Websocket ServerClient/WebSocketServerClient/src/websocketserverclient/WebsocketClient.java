/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocketserverclient;


import java.net.URI;
import javax.swing.JOptionPane;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;


/**
 *
 * @author Patryk
 */
public class WebsocketClient extends WebSocketClient
{

    public WebsocketClient( URI serverURI ) {
		super( serverURI );
	}

    @Override
    public void onError(Exception ex)
    {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, ex, "Error!", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        System.out.println("Connection Opened");
    }

    @Override
    public void onMessage(String message)
    {
        System.out.println("Recieved: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.out.println("Connection Closed");
    }
    
}
