package startkill;

import java.io.IOException;

public class test 
{
    //Test: Kill Running Chrome browser
    public static void killProgram() throws InterruptedException
    {   
        try 
        {
            //Get current Java Runtime Environment, to run external program: CMD
            //Use TASKKILL command to FORCEFULLY(/F) close open Chrome browser
            Runtime.getRuntime().exec("TASKKILL /F /IM chrome.exe");
        }
        catch(IOException ex) 
        {
            ex.printStackTrace();
        }
    }
    
    //Test: Start Chrome Browser
    public static void startProgram() throws InterruptedException
    {
        try 
        {
            //Open up Chrome browser
            //Filepath can be different on each PC
            Runtime.getRuntime().exec("\"/Program Files (x86)/Google/Chrome/Application/chrome.exe\"");
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
