package Child;


import CustomDialogs.Quiz;
import Main.Callback;
import Main.WebsocketClient;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URISyntaxException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Patryk
 */
public class ChildViewRestricted extends javax.swing.JPanel
{
    WebsocketClient _client;
    ScheduledExecutorService _executor;
    Map<String, ApplicationListEntry> _whitelistPrograms;
    Map<String, ApplicationListEntry> _whitelistWebsites;
    
    /**
     * Creates new form ChildViewRestricted
     */
    public ChildViewRestricted()
    {
        initComponents();
        _whitelistPrograms = new HashMap<>();
        _whitelistWebsites = new HashMap<>();
        try {
            _client = WebsocketClient.getInstace();
        }
        catch (URISyntaxException ex) {
            Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        _executor = Executors.newScheduledThreadPool(1);
        //_executor.scheduleAtFixedRate(TimerTick, 100, 1000000, TimeUnit.MILLISECONDS);
        _executor.scheduleAtFixedRate(TimerTick, 100, 1000, TimeUnit.MILLISECONDS);
    }
    
    private void updatePanelLength()
    {
        int requiedLength = 0;
        for(Component c : jPanelWhitelistList.getComponents())
        {
            requiedLength += c.getMinimumSize().height;
            //System.out.println(c.getLocation());
        }
        this.jPanelWhitelistList.setPreferredSize(new Dimension(this.jPanelWhitelistList.getWidth(), requiedLength));
        this.jPanelWhitelistList.revalidate();
        this.jPanelWhitelistList.updateUI();
        this.jPanelWhitelistList.repaint();
        this.jScrollPane.revalidate();
        this.jScrollPane.updateUI();
        this.jScrollPane.repaint();
        
    }
    

    private void updatePanel() throws Exception
    {
        _client.RequestRemainingComputerTime(new Callback()
            {
                @Override
                public void call(String[] params)
                {
                    int[] time = Seconds2HMS(Integer.parseInt(params[0].split("\\.")[0]));
                    int hours = time[0];
                    int minutes = time[1];
                    int seconds = time[2];
                    jLabelTimeRemaining.setText(String.format("Time Remaining: %s:%s:%s", hours<10 ? "0"+ hours : hours+"", minutes<10 ? "0"+ minutes : minutes+"", seconds<10 ? "0"+ seconds : seconds+""));
                }
            });
        _client.RequestWhitelistProgramList(new Callback()
        {
            @Override
            public void call(String[] programNames)
            {
                for(String program : programNames)
                {
                    
                    if(program.contains("DONOTDELETE"))
                        continue;
                    try {
                        _client.GetAllocatedTimeProgram(program, new Callback()
                        {
                            @Override
                            public void call(String[] timeAllocated)
                            {
                                int[] time = Seconds2HMS(Integer.parseInt(timeAllocated[0].split("\\.")[0]));
                                int seconds = time[2];
                                int minutes = time[1];
                                int hours = time[0];
                                Time totalAllowedTime = new Time(hours, minutes, seconds);
                                try{
                                _client.GetTimeRemainingProgram(program, new Callback()
                                {
                                    @Override
                                    public void call(String[] TimeRemaining)
                                    {
                                        Time timeRemaining;
                                        if(TimeRemaining[0].contains("not found"))
                                        {
                                            timeRemaining = totalAllowedTime;
                                        }
                                        else
                                        { 
                                        
                                        int[] time = Seconds2HMS(Integer.parseInt(TimeRemaining[0].split("\\.")[0]));
                                        int seconds = time[2];
                                        int minutes = time[1];
                                        int hours = time[0];
                                        timeRemaining = new Time(hours, minutes, seconds);
                                        }
                                        
                                        try{
                                            _client.GetIsRunningProgram(program, new Callback()
                                            {
                                                @Override
                                                public void call(String[] isRunning)
                                                {
                                                    if(_whitelistPrograms.containsKey(program))
                                                    {
                                                        _whitelistPrograms.get(program).SetIsRunning(isRunning[0].toLowerCase().contains("tr"));
                                                        _whitelistPrograms.get(program).SetRemainingTime(timeRemaining);
                                                        _whitelistPrograms.get(program).SetTotalAllotedTime(totalAllowedTime);
                                                    }
                                                    else
                                                    {
                                                        _whitelistPrograms.put(program, new ApplicationListEntry(program, totalAllowedTime, timeRemaining, true, isRunning[0].toLowerCase().contains("tr")));
                                                        jPanelWhitelistList.add(_whitelistPrograms.get(program));
                                                        updatePanelLength();
                                                    }
                                                    revalidate();
                                                    repaint();
                                                            
                                                }
                                            });
                                        }
                                        catch(Exception ex)
                                        {
                                            Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });
                                }
                                catch(Exception ex)
                                {
                                    Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                    }
                    catch (Exception ex) {
                        Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for(String key : _whitelistPrograms.keySet())
                {
                    boolean stillOnWhitelist = false;
                    for(String program : programNames)
                    {
                        stillOnWhitelist = stillOnWhitelist || key.equals(program);
                        if(stillOnWhitelist)
                            break;
                    }
                    if(!stillOnWhitelist)
                    {
                        _whitelistPrograms.get(key).SetIsRunning(false);
                    }
                }
            }
        });
        
        _client.RequestWhitelistWebsiteList(new Callback()
        {
            @Override
            public void call(String[] WebsiteNames)
            {
                for(String website : WebsiteNames)
                {
                    
                    if(website.contains("DONOTDELETE"))
                        continue;
                    try {
                        _client.GetAllocatedTimeWebsite(website, new Callback()
                        {
                            @Override
                            public void call(String[] timeAllocated)
                            {
                                int[] time = Seconds2HMS(Integer.parseInt(timeAllocated[0].split("\\.")[0]));
                                int seconds = time[2];
                                int minutes = time[1];
                                int hours = time[0];
                                Time totalAllowedTime = new Time(hours, minutes, seconds);
                                try{
                                _client.GetTimeRemainingWebsite(website, new Callback()
                                {
                                    @Override
                                    public void call(String[] TimeRemaining)
                                    {
                                        Time timeRemaining;
                                        if(TimeRemaining[0].contains("not found"))
                                        {
                                            timeRemaining = totalAllowedTime;
                                        }
                                        else
                                        {
                                            int[] time = Seconds2HMS(Integer.parseInt(TimeRemaining[0].split("\\.")[0]));
                                            int seconds = time[2];
                                            int minutes = time[1];
                                            int hours = time[0];
                                            timeRemaining = new Time(hours, minutes, seconds);
                                        }
                                        if(_whitelistWebsites.containsKey(website))
                                        {
                                            _whitelistWebsites.get(website).SetIsRunning(true);
                                            _whitelistWebsites.get(website).SetRemainingTime(timeRemaining);
                                            _whitelistWebsites.get(website).SetTotalAllotedTime(totalAllowedTime);
                                        }
                                        else
                                        {
                                            _whitelistWebsites.put(website, new ApplicationListEntry(website, totalAllowedTime, timeRemaining, true, true));
                                            jPanelWhitelistList.add(_whitelistWebsites.get(website));
                                            updatePanelLength();
                                        }
                                        revalidate();
                                        repaint();
                                                     
                                        
                                    }
                                });
                                }
                                catch(Exception ex)
                                {
                                    Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                    }
                    catch (Exception ex) {
                        Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
    }
    
    
    Runnable TimerTick = new Runnable() {
        public void run() {
            try{
                updatePanel();
            }
            catch(org.java_websocket.exceptions.WebsocketNotConnectedException ex)
            {
                _executor.shutdownNow();
                System.exit(1);
            }
            catch(Exception ex)
            {
                Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
            }
                
        }
    };
    
    int[] Seconds2HMS(int seconds)
    {
        int[] output = new int[3]; //[Hours, minutes, seconds]
        output[2] = seconds;
        output[1] = output[2]/60;
        output[2] = output[2]%60;
        output[0] = output[1]/60;
        output[1] = output[1]%60;
        return output; 
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

        jScrollPane = new javax.swing.JScrollPane();
        jPanelWhitelistList = new javax.swing.JPanel();
        jLabelTimeRemaining = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButtonEarnMoreTime = new javax.swing.JButton();

        jScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setMaximumSize(new java.awt.Dimension(250, 500));
        jScrollPane.setMinimumSize(new java.awt.Dimension(250, 500));
        jScrollPane.setPreferredSize(new java.awt.Dimension(250, 500));

        jPanelWhitelistList.setAutoscrolls(true);
        jPanelWhitelistList.setMaximumSize(new java.awt.Dimension(250, 1000));
        jPanelWhitelistList.setMinimumSize(new java.awt.Dimension(250, 75));
        jPanelWhitelistList.setPreferredSize(new java.awt.Dimension(250, 75));
        jPanelWhitelistList.addContainerListener(new java.awt.event.ContainerAdapter()
        {
            public void componentAdded(java.awt.event.ContainerEvent evt)
            {
                jPanelWhitelistListComponentAdded(evt);
            }
            public void componentRemoved(java.awt.event.ContainerEvent evt)
            {
                jPanelWhitelistListComponentRemoved(evt);
            }
        });
        jPanelWhitelistList.setLayout(new javax.swing.BoxLayout(jPanelWhitelistList, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane.setViewportView(jPanelWhitelistList);

        jLabelTimeRemaining.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelTimeRemaining.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTimeRemaining.setText("Time Remaining 0:00:00");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("White Listed Programs");

        jButtonEarnMoreTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonEarnMoreTime.setText("Earn More Time");
        jButtonEarnMoreTime.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonEarnMoreTimeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonEarnMoreTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelTimeRemaining, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonEarnMoreTime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTimeRemaining)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jPanelWhitelistListComponentAdded(java.awt.event.ContainerEvent evt)//GEN-FIRST:event_jPanelWhitelistListComponentAdded
    {//GEN-HEADEREND:event_jPanelWhitelistListComponentAdded

    }//GEN-LAST:event_jPanelWhitelistListComponentAdded

    private void jPanelWhitelistListComponentRemoved(java.awt.event.ContainerEvent evt)//GEN-FIRST:event_jPanelWhitelistListComponentRemoved
    {//GEN-HEADEREND:event_jPanelWhitelistListComponentRemoved

    }//GEN-LAST:event_jPanelWhitelistListComponentRemoved

    private void jButtonEarnMoreTimeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonEarnMoreTimeActionPerformed
    {//GEN-HEADEREND:event_jButtonEarnMoreTimeActionPerformed
        Quiz quiz = new Quiz(Main.MainGUI.getInstance(), true, "Which team made the best project this quarter?", "Team Beta", new String[]{"Team Alpha", "Team Delta", "Team Gamma"});
        if(quiz.showDialog())
        {
            for(String entry : _whitelistPrograms.keySet())
            {
                try {
                    _client.RemoveFromBlacklistProgram(entry, (String[] params) -> {
                        try {
                            _client.RemoveFromWhitelistProgram(entry, (String[] params1) -> {
                                Time allocated = _whitelistPrograms.get(entry).GetTotalAllotedTime();
                                int Aseconds = allocated.getSeconds();
                                int Aminutes = allocated.getMinutes() + 5;
                                int Ahours = allocated.getHours();
                                try {
                                    _client.AddToWhitelistProgram(entry, Aseconds + (60* (Aminutes + (60*Ahours))), Main.EmptyCallback.GetInstace());
                                }
                                catch (Exception ex) {
                                    Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                                }
//                                Time remaining = _whitelistPrograms.get(entry).GetRemainingTime();
//                                int Rseconds = remaining.getSeconds();
//                                int Rminutes = allocated.getMinutes();
//                                int Rhours = allocated.getHours();
//                                if(Rseconds != 0 && Rminutes != 0 && Rseconds !=0)
//                                {
//                                    try {
//                                        _client.RequestProgramAddTime(entry, 5*60, Main.EmptyCallback.GetInstace());
//                                    }
//                                    catch (Exception ex) {
//                                        Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
//                                    }
//                                }
                            });
                        }
                        catch (Exception ex) {
                            Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
                catch (Exception ex) {
                    Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
            
            for(String entry : _whitelistWebsites.keySet())
            {
                try {
                    _client.RemoveFromBlacklistWebsite(entry, (String[] params) -> {
                        try {
                            _client.RemoveFromWhitelistWebsite(entry, (String[] params1) -> {
                                Time remaining = _whitelistWebsites.get(entry).GetRemainingTime();
                                int seconds = remaining.getSeconds();
                                int minutes = remaining.getMinutes() + 5;
                                int hours = remaining.getHours();
                                
                                try {
                                    _client.AddToWhitelistWebsite(entry, seconds + (60* (minutes + (60*hours))), Main.EmptyCallback.GetInstace());
                                }
                                catch (Exception ex) {
                                    Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        }
                        catch (Exception ex) {
                            Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
                catch (Exception ex) {
                    Logger.getLogger(ChildViewRestricted.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
        }
        jButtonEarnMoreTime.setEnabled(false);
    }//GEN-LAST:event_jButtonEarnMoreTimeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonEarnMoreTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTimeRemaining;
    private javax.swing.JPanel jPanelWhitelistList;
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables
}
