package Main;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Patryk
 */
public class WebsocketClient extends WebSocketClient
{
    private static WebsocketClient _singleInstance = null;
    public static final String _serverLocation = "ws://localhost:4649/test";
    byte _currentCommandNumber;
    Callback[] _messagesSent;
    
    private WebsocketClient(URI serverURI) throws URISyntaxException {
        super(serverURI);

        _currentCommandNumber = Byte.MIN_VALUE;
        _messagesSent = new Callback[256];
    }
    
    public static WebsocketClient getInstace() throws URISyntaxException
    {
        if(_singleInstance == null)
            _singleInstance = new WebsocketClient(new URI(_serverLocation));
        return _singleInstance;
    }
    
    public void ValidateCredentials(String Username, String Password, Callback callback)
    {
        callback.call(new String[]{"true"});
    }
    
    public void RequestInstalledProgramList(Callback callback) throws Exception
    {
        SendMessage("request_programlist", callback);
    }
    
    public void RequestBlacklistProgramList(Callback callback) throws Exception
    {
        SendMessage("request_blacklistprograms", callback);
    }
    
    public void RequestBlacklistWebsiteList(Callback callback) throws Exception
    {
        SendMessage("request_blacklistwebsites", callback);
    }
    
    public void RequestWhitelistProgramList(Callback callback) throws Exception
    {
        SendMessage("request_whitelistprograms", callback);
    }
    
    public void RequestWhitelistWebsiteList(Callback callback) throws Exception
    {
        SendMessage("request_whitelistwebsites", callback);
    }
    
    public void GetAllocatedTimeProgram(String name, Callback callback) throws Exception
    {
        SendMessage("request_totaltimeallocatedprogram", name, callback);
    }
    
    public void GetAllocatedTimeWebsite(String name, Callback callback) throws Exception
    {
        SendMessage("request_totaltimeallocatedwebsite", name, callback);
    }
    
    public void GetTimeRemainingProgram(String name, Callback callback) throws Exception
    {
        SendMessage("request_timeremainingprogram", name, callback);
    }
    
    public void GetTimeRemainingWebsite(String name, Callback callback) throws Exception
    {
        SendMessage("request_timeremainingwebsite", name, callback);
    }
    
    public void GetIsRunningProgram(String name, Callback callback) throws Exception
    {
        SendMessage("request_programisrunning", name, callback);
    }
    
    public void AddToBlacklistProgram(String name, Callback callback) throws Exception
    {
        SendMessage("request_addtoblacklistprograms", name, callback);
    }
    public void AddToBlacklistWebsite(String name, Callback callback) throws Exception
    {
        SendMessage("request_addtoblacklistwebsites", name, callback);
    }
    
    public void RemoveFromBlacklistProgram(String name, Callback callback) throws Exception
    {
        SendMessage("request_removefromblacklistprograms", name, callback);
    }
    public void RemoveFromBlacklistWebsite(String name, Callback callback) throws Exception
    {
        SendMessage("request_removefromblacklistwebsites", name, callback);
    }
    
    public void AddToWhitelistProgram(String name, int seconds, Callback callback) throws Exception
    {
        SendMessage("request_addtowhitelistprograms", new String[]{ name, seconds + ""}, callback);
    }
    public void AddToWhitelistWebsite(String name, int seconds, Callback callback) throws Exception
    {
        SendMessage("request_addtowhitelistwebsites", new String[]{ name, seconds + ""}, callback);
    }
    
    public void RemoveFromWhitelistProgram(String name, Callback callback) throws Exception
    {
        SendMessage("request_removefromwhitelistprograms", name, callback);
    }
    
    public void RemoveFromWhitelistWebsite(String name, Callback callback) throws Exception
    {
        SendMessage("request_removefromwhitelistwebsites", name, callback);
    }
    
    public void RequestProgramsUsed(Callback callback) throws Exception
    {
        SendMessage("request_programsused", callback);
    }
    
    public void RequestWebsitesVisited(Callback callback) throws Exception
    {
        SendMessage("request_websitesvisited", callback);
    }
    
    public void RequestProgramTimeSpent(String name, Callback callback) throws Exception
    {
        SendMessage("request_timespentprogram", name, callback);
    }
    
    public void RequestWebsiteTimeSpent(String name, Callback callback) throws Exception
    {
        SendMessage("request_timespentwebsite", name, callback);
    }
    
    public void RequestTotalComputerTime(Callback callback) throws Exception
    {
        SendMessage("request_totalcomputertime", callback);
    }
    
    public void RequestRemainingComputerTime(Callback callback) throws Exception
    {
        SendMessage("request_remainingcomputertime", callback);
    }
    
    public void SetTotalComputerTime(int time, Callback callback) throws Exception
    {
        SendMessage("set_totalcomputertime", time+"", callback);
    }
    
    public void RequestProgramAddTime(String name, int time, Callback callback) throws Exception
    {
        SendMessage("request_addTime", new String[]{name, time+""}, callback);
    }
    
    private void SendMessage(String message, Callback callback) throws Exception
    {
        String outgoingMessage = message+":"+(128+_currentCommandNumber);
        PushMessageSent(callback);
        send("GUI:"+outgoingMessage);
    }
    
    private void SendMessage(String message, String[] args, Callback callback) throws Exception
    {
        String outgoingMessage = message+":"+(128+_currentCommandNumber) + ":";
        for(int i = 0; i < args.length; i++)
        {
            outgoingMessage += (i < args.length-1) ? args[i] + "," : args[i];
        }
        PushMessageSent(callback);
        send("GUI:"+outgoingMessage);
    }
    
    private void SendMessage(String message, String arg, Callback callback) throws Exception
    {
        String outgoingMessage = message+":"+(128+_currentCommandNumber) + ":" + arg;
        PushMessageSent(callback);
        send("GUI:"+outgoingMessage);
    }
    
    private void PushMessageSent(Callback callback) throws Exception
    {
        if(_messagesSent[128+_currentCommandNumber] == null)
            _messagesSent[128+_currentCommandNumber++] = callback;
        else
        {
            close();
            throw new Exception("MessageBufferFull!");
        }
    }
    
    private void ProcessMessage(String message)
    {
        String[] components = message.split(":");
        Callback toCall = PopMessageSent(Integer.parseInt(components[2]));
//        Runnable r = () -> {
//            toCall.call(components[components.length-1].split(","));
//        };
//        Thread t = new Thread(r);
//        t.start();
        switch(components[1])
        {
            case "request_programlist":
                toCall.call(components[3].split(","));
                break;
            case "request_blacklistprograms":
                toCall.call(components[3].split(","));
                break;
            case "request_blacklistwebsites":
                toCall.call(components[3].split(","));
                break;
            case "request_whitelistprograms":
                toCall.call(components[3].split(","));
                break;
            case "request_whitelistwebsites":
                toCall.call(components[3].split(","));
                break;
            case "request_whitelistprogramswithtime":
                toCall.call(components[3].split(","));
                break;
            case "request_whitelistwebsiteswithtime":
                toCall.call(components[3].split(","));
                break;
            case "request_totaltimeallocatedprogram":
                toCall.call(components[4].split(","));
                break;
            case "request_totaltimeallocatedwebsite":
                toCall.call(components[4].split(","));
                break;
            case "request_programsused":
                toCall.call(components[3].split(","));
                break;
            case "request_websitesvisited":
                toCall.call(components[3].split(","));
                break;
            case "request_timespentprogram":
                toCall.call(components[4].split(","));
                break;
            case "request_timespentwebsite":
                toCall.call(components[4].split(","));
                break;
            case "request_timeremainingprogram":
                toCall.call(components[4].split(","));
                break;
            case "request_timeremainingwebsite":
                toCall.call(components[4].split(","));
                break;
            case "request_programisrunning":
                toCall.call(components[4].split(","));
                break;
            case "request_totalcomputertime":
                toCall.call(components[3].split(","));
                break;
            case "request_remainingcomputertime":
                toCall.call(components[3].split(","));
                break;
            default:
                toCall.call(new String[]{});
                break;

              
        }
    }
    
    private Callback PopMessageSent(int messageNumber)
    {
        Callback call = null;
        try{
            call = _messagesSent[messageNumber];
            _messagesSent[messageNumber] = null;
        }
        catch(Exception ex)
        {
            System.out.println("Error: " + ex.getMessage());
        }
        return call;
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
        //System.out.println("Recieved: " + message);
        try{
            ProcessMessage(message);
        }
        catch(Exception ex)
        {
            System.out.print("Error "+ ex.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.out.println("Connection Closed");
    }
    
}
