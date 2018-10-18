package Child;


import java.awt.Color;
import java.awt.Image;
import java.awt.LayoutManager;
import java.sql.Time;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Patryk
 */
public class ApplicationListEntry extends javax.swing.JPanel
{
    private String _programName;
    private Icon _programIcon;
    private Time _totalAllowedTime;
    private Time _timeRemaining;
    private boolean _showProgressBar;
    private boolean _isRunning;
    /**
     * Creates new form ApplicationListEntry
     */

    public ApplicationListEntry(String programName, Icon programIcon, Time totalAllowedTime, Time timeRemaining, boolean showProgressBar, boolean isRunning)
    {
        initComponents();
        SetProgramIcon((Image) programIcon);
        SetProgramName(programName);
        SetTotalAllotedTime(totalAllowedTime);
        SetRemainingTime(timeRemaining);
        SetShowProgressBar(showProgressBar);
        SetIsRunning(isRunning);
        updateProgressBar();
    }
    
    public ApplicationListEntry(String programName, ImageIcon programIcon, Time totalAllowedTime, Time timeRemaining, boolean showProgressBar, boolean isRunning)
    {
        initComponents();
        SetProgramIcon(programIcon);
        SetProgramName(programName);
        SetTotalAllotedTime(totalAllowedTime);
        SetRemainingTime(timeRemaining);
        SetShowProgressBar(showProgressBar);
        SetIsRunning(isRunning);
        updateProgressBar();
    }
    
    public ApplicationListEntry(String programName, Time totalAllowedTime, Time timeRemaining, boolean showProgressBar, boolean isRunning)
    {
        initComponents();
        SetProgramIcon(new javax.swing.ImageIcon(getClass().getResource("/app-icon.png")));
        SetProgramName(programName);
        SetTotalAllotedTime(totalAllowedTime);
        SetRemainingTime(timeRemaining);
        SetShowProgressBar(showProgressBar);
        SetIsRunning(isRunning);
        updateProgressBar();
    }
    
    public ApplicationListEntry(boolean showProgressBar, boolean isRunning)
    {
        this("Program Name", new Time(2, 0, 0), new Time(1, 0, 0), showProgressBar, isRunning);
    }
    
    public ApplicationListEntry()
    {
        this(true, true);
    }
    
    public boolean GetIsRunning()
    {
        return _isRunning;
    }
    
    public void SetIsRunning(boolean isRunning)
    {
        this._isRunning = isRunning;
        jLabelProgramIcon.setEnabled(isRunning);
        jLabelProgramName.setEnabled(isRunning);
        jLabelTimeRemainingLabel.setEnabled(isRunning);
        jLabelTimeRemainingTime.setEnabled(isRunning);
        jProgressBarTimeRemaining.setEnabled(isRunning);
        updateProgressBar();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Get/Set Program Icon">
    public Icon GetProgramIcon()
    {
        return this._programIcon;
    }
    public void SetProgramIcon(ImageIcon icon)
    {
        Image scaled = icon.getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
        this._programIcon = new ImageIcon(scaled);
        jLabelProgramIcon.setIcon(this._programIcon);
    } 
    public void SetProgramIcon(Image icon)
    {
        this._programIcon = new ImageIcon(icon.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));
        jLabelProgramIcon.setIcon(this._programIcon);
    } 
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="Get/Set Program Name">
    public String GetProgramName()
    {
        return this._programName;
    }
    
    public void SetProgramName(String name)
    {
        this._programName = name;
        jLabelProgramName.setText(this._programName);
    }
    // </editor-fold> 
    
    public Time GetRemainingTime()
    {
        return this._timeRemaining;
    }
    
    public void SetRemainingTime(Time remainingTime)
    {
        this._timeRemaining = remainingTime;
        jLabelTimeRemainingTime.setText(this._timeRemaining.toString());
        updateProgressBar();
    }
    
    public Time GetTotalAllotedTime()
    {
        return this._totalAllowedTime;
    }
    
    public void SetTotalAllotedTime(Time totalTime)
    {
        this._totalAllowedTime = totalTime;
        updateProgressBar();
    }
    
    public boolean GetShowProgressBar()
    {
        return this._showProgressBar;
    }
    
    public void SetShowProgressBar(boolean showProgressBar)
    {
        this._showProgressBar = showProgressBar;
        jProgressBarTimeRemaining.setVisible(showProgressBar);
        updateProgressBar();
    }
    
    private void updateProgressBar()
    {
        if(this._timeRemaining == null || this._totalAllowedTime == null || !this._showProgressBar ) //|| !this._isRunning)
            return;
        
        jProgressBarTimeRemaining.setMaximum(TimeInSeconds(this._totalAllowedTime));
        jProgressBarTimeRemaining.setValue(TimeInSeconds(this._totalAllowedTime) - TimeInSeconds(this._timeRemaining));
    }
    
    private int TimeInSeconds(Time time)
    {
        int seconds = 60 * 60* time.getHours();
        seconds += 60 * time.getMinutes();
        seconds += time.getSeconds();
        return seconds;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    //@SuppressWarnings("unchecked");
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelProgramIcon = new javax.swing.JLabel();
        jProgressBarTimeRemaining = new javax.swing.JProgressBar();
        jLabelProgramName = new javax.swing.JLabel();
        jLabelTimeRemainingLabel = new javax.swing.JLabel();
        jLabelTimeRemainingTime = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(250, 75));
        setMinimumSize(new java.awt.Dimension(250, 75));
        setPreferredSize(new java.awt.Dimension(250, 75));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        jLabelProgramIcon.setPreferredSize(new java.awt.Dimension(32, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(11, 10, 0, 0);
        add(jLabelProgramIcon, gridBagConstraints);

        jProgressBarTimeRemaining.setForeground(java.awt.Color.green);
        jProgressBarTimeRemaining.setValue(50);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.ipadx = 219;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 6, 10);
        add(jProgressBarTimeRemaining, gridBagConstraints);

        jLabelProgramName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelProgramName.setText("Program Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 6, 0, 0);
        add(jLabelProgramName, gridBagConstraints);

        jLabelTimeRemainingLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelTimeRemainingLabel.setText("Time Remaining:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        add(jLabelTimeRemainingLabel, gridBagConstraints);

        jLabelTimeRemainingTime.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelTimeRemainingTime.setText("HH:MM:SS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        add(jLabelTimeRemainingTime, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelProgramIcon;
    private javax.swing.JLabel jLabelProgramName;
    private javax.swing.JLabel jLabelTimeRemainingLabel;
    javax.swing.JLabel jLabelTimeRemainingTime;
    private javax.swing.JProgressBar jProgressBarTimeRemaining;
    // End of variables declaration//GEN-END:variables
}
