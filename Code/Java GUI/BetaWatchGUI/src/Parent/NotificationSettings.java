/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parent;

/**
 *
 * @author Patryk
 */
public class NotificationSettings extends javax.swing.JPanel
{

    /**
     * Creates new form NotificationSettings
     */
    public NotificationSettings()
    {
        initComponents();
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

        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jTextFieldEmailAddress = new javax.swing.JTextField();

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jCheckBox1.setText("SMS:");

        jCheckBox2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jCheckBox2.setText("Email:");

        try
        {
            jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(###)###-####")));
        } catch (java.text.ParseException ex)
        {
            ex.printStackTrace();
        }
        jFormattedTextField2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jTextFieldEmailAddress.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextFieldEmailAddress.setText("beta@betawatch.com");
        jTextFieldEmailAddress.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTextFieldEmailAddressFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt)
            {
                jTextFieldEmailAddressFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldEmailAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField2))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(103, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox2)
                    .addComponent(jTextFieldEmailAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(132, 132, 132))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldEmailAddressFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldEmailAddressFocusGained
    {//GEN-HEADEREND:event_jTextFieldEmailAddressFocusGained
        if(jTextFieldEmailAddress.getText().equals("beta@betawatch.com"))
            jTextFieldEmailAddress.setText("");
    }//GEN-LAST:event_jTextFieldEmailAddressFocusGained

    private void jTextFieldEmailAddressFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldEmailAddressFocusLost
    {//GEN-HEADEREND:event_jTextFieldEmailAddressFocusLost
        if(jTextFieldEmailAddress.getText().equals(""))
            jTextFieldEmailAddress.setText("beta@betawatch.com");
    }//GEN-LAST:event_jTextFieldEmailAddressFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JTextField jTextFieldEmailAddress;
    // End of variables declaration//GEN-END:variables
}
