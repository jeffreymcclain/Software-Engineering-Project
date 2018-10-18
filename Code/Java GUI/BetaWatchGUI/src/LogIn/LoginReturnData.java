/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LogIn;

/**
 *
 * @author patry
 */
public class LoginReturnData {
    public LoginScreen.User userType;
    public String userName;
    public String password;
    public String childName;

    public LoginReturnData(LoginScreen.User userType, String userName, String password, String childName)
    {
        this.userType = userType;
        this.userName = userName;
        this.password = password;
        this.childName = childName;
    }
}
