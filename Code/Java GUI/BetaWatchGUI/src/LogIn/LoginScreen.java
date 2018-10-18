package LogIn;

import Main.WebsocketClient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Patryk
 */
public class LoginScreen extends javax.swing.JDialog
{
    public enum User {CHILD, PARENT, ERROR}
    private User _user;
    private String _userName;
    private String _password;
    private String _childName;
    /**
     * Creates new form LoginScreen
     */
    public LoginScreen(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        _user = User.ERROR;
        this.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/betaSymble.png")).getImage());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        modeSelectScreen = new LogIn.ModeSelectScreen();
        loginPrompt = new LogIn.LoginPrompt();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        loginPrompt.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                loginPromptPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(modeSelectScreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loginPrompt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(loginPrompt, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(modeSelectScreen, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginPromptPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_loginPromptPropertyChange
    {//GEN-HEADEREND:event_loginPromptPropertyChange
        modeSelectScreen.setVisible(!loginPrompt.isVisible());
        this.setSize(450, 250);
    }//GEN-LAST:event_loginPromptPropertyChange

    /**
     * @param args the command line arguments
     */
    
    public void setUser(User user)
    {
        _user = user;
    }
    
    public void setChildName(String childName)
    {
        _childName = childName;
    }
    
    public void SetUserNamePassword(String userName, String password)
    {
        _userName = userName;
        _password = password;
    }
    
    public LoginReturnData showDialog() {
    setVisible(true);
    return new LoginReturnData(_user, _userName, _password, _childName);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private LogIn.LoginPrompt loginPrompt;
    private LogIn.ModeSelectScreen modeSelectScreen;
    // End of variables declaration//GEN-END:variables
}
