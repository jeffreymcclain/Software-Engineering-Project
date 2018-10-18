/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

/**
 *
 * @author Patryk
 */
public class EmptyCallback implements Callback
{

    private static EmptyCallback _instance = null;
    private EmptyCallback()
    {
    }
    
    public static EmptyCallback GetInstace()
    {
        if(_instance == null)
            _instance = new EmptyCallback();
        return _instance;
    }

    @Override
    public void call(String[] params)
    {
    }
    
}
