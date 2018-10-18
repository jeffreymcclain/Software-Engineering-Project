/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocketserverclient;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import org.java_websocket.WebSocket;

/**
 *
 * @author Patryk
 */
public class GUI extends javax.swing.JFrame
{

    private String _clientLocation;
    private WebsocketServer _webServer;
    private WebsocketClient _webClient;
    private int _port;
    private HashMap<String, String> _autoResponses;
    private boolean _isConnected;

    /**
     * Creates new form GUI
     */
    public GUI()
    {
        _isConnected = false;
        initComponents();
        jCheckBoxUnknownCommand.setEnabled(_isConnected);
        _autoResponses = new HashMap<>();
        CellEditorListener ChangeNotification = new CellEditorListener()
        {
            public void editingCanceled(ChangeEvent e)
            {
                updateHashMap();
            }

            public void editingStopped(ChangeEvent e)
            {
                updateHashMap();
            }

            public void updateHashMap()
            {
                _autoResponses.clear();
                for (int row = 0; row < jTableAutoResponse.getRowCount(); row++) {
                    if (_autoResponses.containsKey(jTableAutoResponse.getValueAt(row, 0).toString())) {
                        JOptionPane.showMessageDialog(null, "Cannot have duplicate Keys!", "Error!", JOptionPane.ERROR_MESSAGE);
                    }
                    _autoResponses.put(jTableAutoResponse.getValueAt(row, 0).toString(), jTableAutoResponse.getValueAt(row, 1).toString());
                }
                
            }
        };
        
        jTableAutoResponse.getDefaultEditor(String.class).addCellEditorListener(ChangeNotification);
        
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jToggleServer = new javax.swing.JToggleButton();
        jLabelAddress = new javax.swing.JLabel();
        jButtonConnect = new javax.swing.JButton();
        jTextFieldMessage = new javax.swing.JTextField();
        jButtonSend = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaMessages = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableAutoResponse = new javax.swing.JTable();
        jComboBoxResponseType = new javax.swing.JComboBox<>();
        jCheckBoxUnknownCommand = new javax.swing.JCheckBox();
        jTextFieldUnknownCommandResponse = new javax.swing.JTextField();
        jTextFieldAddress = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WebSocket Client");

        jToggleServer.setText("Server");
        jToggleServer.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jToggleServerActionPerformed(evt);
            }
        });

        jLabelAddress.setText("Address");

        jButtonConnect.setText("Connect");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonConnectActionPerformed(evt);
            }
        });

        jButtonSend.setText("Send");
        jButtonSend.setEnabled(false);
        jButtonSend.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonSendActionPerformed(evt);
            }
        });

        jTabbedPane1.setName(""); // NOI18N

        jTextAreaMessages.setColumns(20);
        jTextAreaMessages.setRows(5);
        jScrollPane1.setViewportView(jTextAreaMessages);

        jTabbedPane1.addTab("Messages", jScrollPane1);

        jTableAutoResponse.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String []
            {
                "Input", "Output"
            }
        ));
        jScrollPane2.setViewportView(jTableAutoResponse);

        jTabbedPane1.addTab("Auto Responses", jScrollPane2);

        jComboBoxResponseType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No Auto Response", "Echo Response", "Response from Table" }));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jToggleServer, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jComboBoxResponseType, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jComboBoxResponseType.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                jComboBoxResponseTypeItemStateChanged(evt);
            }
        });

        jCheckBoxUnknownCommand.setText("Unknown Command Responce");

        jTextFieldUnknownCommandResponse.setText("Unknown Command");

        jTextFieldAddress.setText("ws://localhost:4649/test");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSend, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxResponseType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBoxUnknownCommand)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldUnknownCommandResponse)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jToggleServer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAddress)
                    .addComponent(jButtonConnect)
                    .addComponent(jComboBoxResponseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxUnknownCommand)
                    .addComponent(jTextFieldUnknownCommandResponse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSend))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Messages\n");

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleServerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jToggleServerActionPerformed
    {//GEN-HEADEREND:event_jToggleServerActionPerformed
        jCheckBoxUnknownCommand.setEnabled(jToggleServer.isSelected() && jComboBoxResponseType.getSelectedItem().equals("Response from Table"));
        if (jToggleServer.isSelected()) {
            jButtonConnect.setText("Start");
            this.setTitle("WebSocket Server");
        }
        else {
            jButtonConnect.setText("Connect");
            this.setTitle("WebSocket Client");
        }
        
    }//GEN-LAST:event_jToggleServerActionPerformed

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonConnectActionPerformed
    {//GEN-HEADEREND:event_jButtonConnectActionPerformed
        try {
            _clientLocation = jTextFieldAddress.getText();
            String portstr = _clientLocation.replace("ws://localhost:", "");
            portstr = portstr.split("/")[0];
            _port = Integer.valueOf(portstr);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to Parse Port Number", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (jToggleServer.isSelected()) {
            activateServer();
        }
        else {
            activateClient();
        }
        jToggleServer.setEnabled(!_isConnected);
        jButtonSend.setEnabled(_isConnected);
        jTextFieldAddress.setEditable(!_isConnected);
        if (!_isConnected) {
            if (jToggleServer.isSelected()) {
                jButtonConnect.setText("Start");
            }
            else {
                jButtonConnect.setText("Connect");
            }
        }
        else {
            if (jToggleServer.isSelected()) {
                jButtonConnect.setText("Close");
            }
            else {
                jButtonConnect.setText("Disconnect");
            }
        }
    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void activateClient()
    {
        if (_webClient == null) {
            try {
                _webClient = new WebsocketClient(new URI(_clientLocation))
                {
                    @Override
                    public void onMessage(String message)
                    {
                        jTextAreaMessages.append("Server: " + message + "\n");
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote)
                    {
                        _isConnected = false;
                        jToggleServer.setEnabled(!_isConnected);
                        jButtonSend.setEnabled(_isConnected);
                        jTextFieldAddress.setEditable(!_isConnected);
                        if (!_isConnected) {
                            if (jToggleServer.isSelected()) {
                                jButtonConnect.setText("Start");
                            }
                            else {
                                jButtonConnect.setText("Connect");
                            }
                        }
                        else {
                            if (jToggleServer.isSelected()) {
                                jButtonConnect.setText("Close");
                            }
                            else {
                                jButtonConnect.setText("Disconnect");
                            }
                        }
                        jTextAreaMessages.append("Client Closed" + "\n");
                    }

                    @Override
                    public void send(String message)
                    {
                        jTextAreaMessages.append("Client: " + message + "\n");
                        super.send(message);
                    }

                    @Override
                    public void send(byte[] message)
                    {
                        jTextAreaMessages.append("Client: " + Arrays.toString(message) + "\n");
                        super.send(message);
                    }
                };
                _webClient.connect();
                jTextAreaMessages.append("Connected to Server" + "\n");
                _isConnected = true;
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unable to Connect to Server", "Error!", JOptionPane.ERROR_MESSAGE);
            }

        }
        else {
            _webClient.close();
            jTextAreaMessages.append("Disconnected from Server" + "\n");
            _isConnected = false;
            _webClient = null;
        }
    }

    private void activateServer()
    {
        if (_webServer == null) {
            try {
                _webServer = new WebsocketServer(_port)
                {
                    public void respond(String message)
                    {
                         String command = message.split(":")[1];
                        
                        if (_autoResponses.containsKey(command)) {
                            broadcast((message + ":"+ _autoResponses.get(command)).replace(" ", ""));
                        }
                        else {
                            if (jCheckBoxUnknownCommand.isSelected()) {
                                broadcast(jTextFieldUnknownCommandResponse.getText());
                            }
                        }
                        
//                        if (_autoResponses.containsKey(message)) {
//                            broadcast(_autoResponses.get(message));
//                        }
//                        else {
//                            if (jCheckBoxUnknownCommand.isSelected()) {
//                                broadcast(jTextFieldUnknownCommandResponse.getText());
//                            }
//                        }
                    }
                    

                    @Override
                    public void onMessage(WebSocket conn, String message)
                    {
                        jTextAreaMessages.append("Client: " + message + "\n");
                        switch (jComboBoxResponseType.getSelectedIndex()) {
                            case 0: //Do Nothing
                                break;
                            case 1:
                                broadcast(message);
                                break;
                            case 2:
                                respond(message);
                                break;
                            default:
                                break;
                        }
                        System.out.println(conn + ": " + message);
                    }

                    @Override
                    public void onClose(WebSocket conn, int code, String reason, boolean remote)
                    {
                        jTextAreaMessages.append(conn + " Disconnected" + "\n");
                        System.out.println(conn + " Disconnected");
                        
                    }

                    @Override
                    public void broadcast(String message)
                    {
                        super.broadcast(message);
                        jTextAreaMessages.append("Server: " + message + "\n");
                    }

                    @Override
                    public void broadcast(byte[] data)
                    {
                        super.broadcast(data);
                        jTextAreaMessages.append("Server: " + data + "\n");
                    }

                    @Override
                    public void onMessage(WebSocket conn, ByteBuffer message)
                    {
                        jTextAreaMessages.append("Client: " + message + "\n");
                        switch (jComboBoxResponseType.getSelectedIndex()) {
                            case 0: //Do Nothing
                                break;
                            case 1:
                                broadcast(message.array());
                                break;
                            case 2:
                                respond(message.toString());
                                break;
                            default:
                                break;
                        }
                        System.out.println(conn + ": " + message);
                    }
                };
                _webServer.start();
                jTextAreaMessages.append("Server Started" + "\n");
                _isConnected = true;

            }
            catch (UnknownHostException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Unable to Start Server", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            try {
                _webServer.stop();
                jTextAreaMessages.append("Server Stopped" + "\n");
                _isConnected = false;
                _webServer = null;
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jComboBoxResponseTypeItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_jComboBoxResponseTypeItemStateChanged
    {//GEN-HEADEREND:event_jComboBoxResponseTypeItemStateChanged
        jCheckBoxUnknownCommand.setEnabled(jComboBoxResponseType.getSelectedItem().equals("Response from Table"));
    }//GEN-LAST:event_jComboBoxResponseTypeItemStateChanged

    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonSendActionPerformed
    {//GEN-HEADEREND:event_jButtonSendActionPerformed
        if (jToggleServer.isSelected()) {
            _webServer.broadcast(jTextFieldMessage.getText());
        }
        else {
            _webClient.send(jTextFieldMessage.getText());
        }

    }//GEN-LAST:event_jButtonSendActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JCheckBox jCheckBoxUnknownCommand;
    private javax.swing.JComboBox<String> jComboBoxResponseType;
    private javax.swing.JLabel jLabelAddress;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableAutoResponse;
    private javax.swing.JTextArea jTextAreaMessages;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldMessage;
    private javax.swing.JTextField jTextFieldUnknownCommandResponse;
    private javax.swing.JToggleButton jToggleServer;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
