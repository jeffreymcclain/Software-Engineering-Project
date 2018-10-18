/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parent;

import Main.Callback;
import Main.EmptyCallback;
import Main.WebsocketClient;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Patryk
 */
public class EditWhitelist extends javax.swing.JPanel
{
    public enum type { PROGRAM, WEBSITE };
    public enum action { ADD, REMOVE };
    class setEntry
    {
        public String _name;
        public type _type;
        public action _action;
        public int _hours;
        public int _mins;
        public int _sec;

        public setEntry(String name, type type, action action, int hours, int mins, int sec)
        {
            this._name = name;
            this._type = type;
            this._action = action;
            this._hours = hours;
            this._mins = mins;
            this._sec = sec;
        }
        
        public setEntry(String name, type type, action action)
        {
            this._name = name;
            this._type = type;
            this._action = action;
            this._hours = 0;
            this._mins = 0;
            this._sec = 0;
        }
    }
    
    WebsocketClient _client;
    /**
     * Creates new form EditWhitelist
     */
    
    int selectedInstalledApplicationRow;
    int selectedWhitelistProgramRow;
    int selectedWhitelistWebsiteRow;
    
    List<setEntry> _actionList;
    public EditWhitelist()
    {
        initComponents();
        _actionList = new LinkedList<>();
        try {
            _client = WebsocketClient.getInstace();
        }
        catch (URISyntaxException ex) {
            Logger.getLogger(EditBlacklist.class.getName()).log(Level.SEVERE, null, ex);
        }
        jRadioButtonInstalledApplications.setSelected(true);
        try{
        InitializeInstalledApplicationList();
        InitializeWhiteListedApplications();
        InitializeWhiteListedWebsites();
        }
        catch(Exception ex)
        {
            Logger.getLogger(EditBlacklist.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void InitializeInstalledApplicationList() throws Exception
    {
        DefaultTableModel installedApplicationsModel = new DefaultTableModel(new String[]{"Installed Applications"}, 0){
             @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };
        _client.RequestInstalledProgramList(new Callback()
        {
            @Override
            public void call(String[] params)
            {
                for(String s : params)
                {
                    installedApplicationsModel.addRow(new String[] {s});
                }
                jTableInstalledApplications.setModel(installedApplicationsModel);
                revalidate();
                repaint();
            }
        });        
    }
    
    private void InitializeWhiteListedApplications() throws Exception
    {
        DefaultTableModel whiteListedProgramsModel = new DefaultTableModel(new String[]{"Whitelisted Applications"}, 0){
             @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };
        _client.RequestWhitelistProgramList(new Callback()
        {
            @Override
            public void call(String[] params)
            {
                for(String s : params)
                {
                    if(s.contains("DONOTDELETE"))
                        continue;
                    try {
                        _client.GetAllocatedTimeProgram(s, new Callback()
                        {
                            @Override
                            public void call(String[] params)
                            {
                                int[] time = Seconds2HMS(Integer.parseInt(params[0]));
                                int seconds = time[2];
                                int minutes = time[1];
                                int hours = time[0];
                                whiteListedProgramsModel.addRow(createRowEntry(s.split(";")[0], hours, minutes, seconds));
                            }
                        });
                    }
                    catch (Exception ex) {
                        Logger.getLogger(EditWhitelist.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                jTableWhitelistedApplications.setModel(whiteListedProgramsModel);
                revalidate();
                repaint();
            }
        });
    }
    
    private void InitializeWhiteListedWebsites() throws Exception
    {
        DefaultTableModel whiteListedWebsitesModel = new DefaultTableModel(new String[]{"Whitelisted Websites"}, 0){
             @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };
        _client.RequestWhitelistWebsiteList(new Callback()
        {
            @Override
            public void call(String[] params)
            {
                for(String s : params)
                {
                    if(s.contains("DONOTDELETE"))
                        continue;
                    try {
                        _client.GetAllocatedTimeWebsite(s, new Callback()
                        {
                            @Override
                            public void call(String[] params)
                            {
                                int[] time = Seconds2HMS(Integer.parseInt(params[0]));
                                int seconds = time[2];
                                int minutes = time[1];
                                int hours = time[0];
                                whiteListedWebsitesModel.addRow(createRowEntry(s.split(";")[0], hours, minutes, seconds));
                            }
                        });
                    }
                    catch (Exception ex) {
                        Logger.getLogger(EditWhitelist.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                jTableWhitelistedWebsites.setModel(whiteListedWebsitesModel);
                revalidate();
                repaint();
            }
        });
    }
    
    String[] createRowEntry(String name, int hours, int minutes, int seconds)
    {
        return new String[] {String.format("%s    %s:%s:%s", name, hours<10 ? "0"+ hours : hours+"", minutes<10 ? "0"+ minutes : minutes+"", seconds<10 ? "0"+ seconds : seconds+"")};
    }
    
    String[] createRowEntry(setEntry entry)
    {
        return new String[] {String.format("%s    %s:%s:%s", entry._name, entry._hours<10 ? "0"+ entry._hours : entry._hours+"", entry._mins<10 ? "0"+ entry._mins : entry._mins+"", entry._sec<10 ? "0"+ entry._sec : entry._sec+"")};
    }
    
    setEntry parseRowEntry(String rowEntery)
    {
        
        String[] data = rowEntery.split("    ");
        String[] time = data[1].split(":");
        return new setEntry(data[0], type.WEBSITE, action.ADD, Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
    }
    
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
    
    int HMS2Seconds(int[] HMS)
    {
        HMS[1] += 60 * HMS[0];
        return HMS[2] + (60 * HMS[1]);
    }
    
    int HMS2Seconds(int hours, int minutes, int seconds)
    {
        return HMS2Seconds(new int[] {hours, minutes, seconds});
    }
    
    int HMS2Seconds(String[] sHMS)
    {
        int[] HMS = new int[3];
        for(int i = 0; i<3; i++)
        {
            HMS[i] = Integer.parseInt(sHMS[i]);
        }
        HMS[1] += 60 * HMS[0];
        return HMS[2] + (60 * HMS[1]);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jRadioButtonInstalledApplications = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableInstalledApplications = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableWhitelistedApplications = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableWhitelistedWebsites = new javax.swing.JTable();
        jButtonRemove = new javax.swing.JButton();
        jTextFieldWebsites = new javax.swing.JTextField();
        jRadioButtonWebsites = new javax.swing.JRadioButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonApply = new javax.swing.JButton();
        jLabelTimeConstraint = new javax.swing.JLabel();
        jTextFieldHoursAdd = new javax.swing.JTextField();
        jTextFieldMinsAdd = new javax.swing.JTextField();
        jTextFieldSecsAdd = new javax.swing.JTextField();
        jTextFieldSecsEdit = new javax.swing.JTextField();
        jTextFieldMinsEdit = new javax.swing.JTextField();
        jTextFieldHoursEdit = new javax.swing.JTextField();
        jButtonUpdateTime = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(400, 302));

        buttonGroup1.add(jRadioButtonInstalledApplications);
        jRadioButtonInstalledApplications.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                jRadioButtonInstalledApplicationsStateChanged(evt);
            }
        });

        jTableInstalledApplications.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTableInstalledApplications.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null},
                {null}
            },
            new String []
            {
                "Installed Applications"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }
        });
        jTableInstalledApplications.setRowSelectionAllowed(false);
        jTableInstalledApplications.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTableInstalledApplicationsFocusGained(evt);
            }
        });
        jTableInstalledApplications.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                jTableInstalledApplicationsMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTableInstalledApplications);

        jTableWhitelistedApplications.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTableWhitelistedApplications.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null},
                {null},
                {null},
                {null}
            },
            new String []
            {
                "Whitelisted Applications"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }
        });
        jTableWhitelistedApplications.setRowSelectionAllowed(false);
        jTableWhitelistedApplications.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTableWhitelistedApplicationsFocusGained(evt);
            }
        });
        jTableWhitelistedApplications.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                jTableWhitelistedApplicationsMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTableWhitelistedApplications);

        jTableWhitelistedWebsites.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTableWhitelistedWebsites.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null},
                {null},
                {null},
                {null}
            },
            new String []
            {
                "Whitelisted Websites"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }
        });
        jTableWhitelistedWebsites.setRowSelectionAllowed(false);
        jTableWhitelistedWebsites.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTableWhitelistedWebsitesFocusGained(evt);
            }
        });
        jTableWhitelistedWebsites.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                jTableWhitelistedWebsitesMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jTableWhitelistedWebsites);

        jButtonRemove.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonRemove.setText("Remove");
        jButtonRemove.setEnabled(false);
        jButtonRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonRemoveActionPerformed(evt);
            }
        });

        jTextFieldWebsites.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldWebsites.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTextFieldWebsitesFocusGained(evt);
            }
        });
        jTextFieldWebsites.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                jTextFieldWebsitesKeyReleased(evt);
            }
        });

        buttonGroup1.add(jRadioButtonWebsites);
        jRadioButtonWebsites.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jRadioButtonWebsites.setText("WebSites:");

        jButtonAdd.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonCancel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonApply.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonApply.setText("Apply");
        jButtonApply.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonApplyActionPerformed(evt);
            }
        });

        jLabelTimeConstraint.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelTimeConstraint.setText("TimeConstraint:");

        jTextFieldHoursAdd.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldHoursAdd.setText("Hours");
        jTextFieldHoursAdd.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTextFieldHoursAddFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt)
            {
                jTextFieldHoursAddFocusLost(evt);
            }
        });
        jTextFieldHoursAdd.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                jTextFieldHoursAddKeyReleased(evt);
            }
        });

        jTextFieldMinsAdd.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldMinsAdd.setText("Mins");
        jTextFieldMinsAdd.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTextFieldMinsAddFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt)
            {
                jTextFieldMinsAddFocusLost(evt);
            }
        });
        jTextFieldMinsAdd.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                jTextFieldMinsAddMousePressed(evt);
            }
        });

        jTextFieldSecsAdd.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldSecsAdd.setText("Secs");
        jTextFieldSecsAdd.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusLost(java.awt.event.FocusEvent evt)
            {
                jTextFieldSecsAddFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTextFieldSecsAddFocusGained(evt);
            }
        });
        jTextFieldSecsAdd.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                jTextFieldSecsAddKeyReleased(evt);
            }
        });

        jTextFieldSecsEdit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldSecsEdit.setText("Secs");
        jTextFieldSecsEdit.setEnabled(false);
        jTextFieldSecsEdit.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTextFieldSecsEditFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt)
            {
                jTextFieldSecsEditFocusLost(evt);
            }
        });

        jTextFieldMinsEdit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldMinsEdit.setText("Mins");
        jTextFieldMinsEdit.setEnabled(false);
        jTextFieldMinsEdit.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTextFieldMinsEditFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt)
            {
                jTextFieldMinsEditFocusLost(evt);
            }
        });

        jTextFieldHoursEdit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldHoursEdit.setText("Hours");
        jTextFieldHoursEdit.setEnabled(false);
        jTextFieldHoursEdit.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusLost(java.awt.event.FocusEvent evt)
            {
                jTextFieldHoursEditFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jTextFieldHoursEditFocusGained(evt);
            }
        });

        jButtonUpdateTime.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonUpdateTime.setText("Update Time");
        jButtonUpdateTime.setEnabled(false);
        jButtonUpdateTime.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonUpdateTimeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButtonInstalledApplications)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButtonWebsites)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelTimeConstraint)
                            .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldHoursAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldMinsAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldSecsAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jTextFieldWebsites, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonApply, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonRemove)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonUpdateTime))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jTextFieldHoursEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldMinsEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldSecsEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jRadioButtonInstalledApplications))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldHoursEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldMinsEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldSecsEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonRemove)
                            .addComponent(jButtonUpdateTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonApply)
                            .addComponent(jButtonCancel))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButtonWebsites)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldWebsites, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTimeConstraint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldHoursAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldMinsAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldSecsAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAdd)
                        .addGap(24, 24, 24))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonCancelActionPerformed
    {//GEN-HEADEREND:event_jButtonCancelActionPerformed
        try{
            InitializeWhiteListedApplications();
            InitializeWhiteListedWebsites();
            InitializeInstalledApplicationList();
            _actionList.clear();
            revalidate();
            repaint();
        }
        catch(Exception ex)
        {
            Logger.getLogger(EditBlacklist.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonApplyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonApplyActionPerformed
    {//GEN-HEADEREND:event_jButtonApplyActionPerformed
        try
        {
            for(setEntry entry : _actionList)
            {
                if(entry._action ==action.ADD)
                {
                    if(entry._type == type.PROGRAM)
                    {
                        _client.AddToWhitelistProgram(entry._name, HMS2Seconds(entry._hours, entry._mins, entry._sec), EmptyCallback.GetInstace());
                    }
                    else
                    {
                        _client.AddToWhitelistWebsite(entry._name, HMS2Seconds(entry._hours, entry._mins, entry._sec), EmptyCallback.GetInstace());
                    }
                }
                else
                {
                    if(entry._type == type.PROGRAM)
                    {
                        _client.RemoveFromWhitelistProgram(entry._name, EmptyCallback.GetInstace());
                    }
                    else
                    {
                        _client.RemoveFromWhitelistWebsite(entry._name, EmptyCallback.GetInstace());
                    }
                }
            }
           
            }
        catch (Exception ex) {
            Logger.getLogger(EditBlacklist.class.getName()).log(Level.SEVERE, null, ex);
        }
        _actionList.clear();
    }//GEN-LAST:event_jButtonApplyActionPerformed

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonRemoveActionPerformed
    {//GEN-HEADEREND:event_jButtonRemoveActionPerformed
        if(selectedWhitelistProgramRow != -1)
        {
            DefaultTableModel model = (DefaultTableModel) jTableWhitelistedApplications.getModel();
            setEntry entry = new setEntry(model.getValueAt(selectedWhitelistProgramRow, 0).toString().split("    ")[0], type.PROGRAM, action.REMOVE);
            _actionList.add(entry);
            model.removeRow(selectedWhitelistProgramRow);
            jTableWhitelistedApplications.setModel(model);
        }
        else if(selectedWhitelistWebsiteRow != -1)
        {
            DefaultTableModel model = (DefaultTableModel) jTableWhitelistedWebsites.getModel();
            setEntry entry = new setEntry(model.getValueAt(selectedWhitelistWebsiteRow, 0).toString().split("    ")[0], type.WEBSITE, action.REMOVE);
            _actionList.add(entry);
            model.removeRow(selectedWhitelistWebsiteRow);
            jTableWhitelistedWebsites.setModel(model);
        }
        selectedWhitelistWebsiteRow = -1;
        selectedWhitelistProgramRow = -1;
        updateRemoveButtonEnabled();
        revalidate();
        repaint();
        jTextFieldHoursEdit.setText("Hours");
        jTextFieldMinsEdit.setText("Mins");
        jTextFieldSecsEdit.setText("Secs");
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    private void jTextFieldHoursAddFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldHoursAddFocusGained
    {//GEN-HEADEREND:event_jTextFieldHoursAddFocusGained
        if(jTextFieldHoursAdd.getText().equals("Hours"))
        {
            jTextFieldHoursAdd.setText("");
            jTextFieldMinsAdd.setText("");
            jTextFieldSecsAdd.setText("");
        }
    }//GEN-LAST:event_jTextFieldHoursAddFocusGained

    private void jTextFieldMinsAddFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldMinsAddFocusGained
    {//GEN-HEADEREND:event_jTextFieldMinsAddFocusGained
        if(jTextFieldMinsAdd.getText().equals("Mins"))
        {
            jTextFieldHoursAdd.setText("");
            jTextFieldMinsAdd.setText("");
            jTextFieldSecsAdd.setText("");
        }
    }//GEN-LAST:event_jTextFieldMinsAddFocusGained

    private void jTextFieldHoursEditFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldHoursEditFocusGained
    {//GEN-HEADEREND:event_jTextFieldHoursEditFocusGained
        if(jTextFieldHoursEdit.getText().equals("Hours"))
        {
            jTextFieldHoursEdit.setText("");
            jTextFieldMinsEdit.setText("");
            jTextFieldSecsEdit.setText("");
        }
    }//GEN-LAST:event_jTextFieldHoursEditFocusGained

    private void jTextFieldSecsAddFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldSecsAddFocusGained
    {//GEN-HEADEREND:event_jTextFieldSecsAddFocusGained
        if(jTextFieldSecsAdd.getText().equals("Secs"))
        {
            jTextFieldHoursAdd.setText("");
            jTextFieldMinsAdd.setText("");
            jTextFieldSecsAdd.setText("");
        }
    }//GEN-LAST:event_jTextFieldSecsAddFocusGained

    private void jTextFieldMinsEditFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldMinsEditFocusGained
    {//GEN-HEADEREND:event_jTextFieldMinsEditFocusGained
        if(jTextFieldMinsEdit.getText().equals("Mins"))
        {
            jTextFieldHoursEdit.setText("");
            jTextFieldMinsEdit.setText("");
            jTextFieldSecsEdit.setText("");
        }
    }//GEN-LAST:event_jTextFieldMinsEditFocusGained

    private void jTextFieldSecsEditFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldSecsEditFocusGained
    {//GEN-HEADEREND:event_jTextFieldSecsEditFocusGained
        if(jTextFieldSecsEdit.getText().equals("Secs"))
        {
            jTextFieldHoursEdit.setText("");
            jTextFieldMinsEdit.setText("");
            jTextFieldSecsEdit.setText("");
        }
    }//GEN-LAST:event_jTextFieldSecsEditFocusGained

    private void jTextFieldHoursAddFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldHoursAddFocusLost
    {//GEN-HEADEREND:event_jTextFieldHoursAddFocusLost
        if(jTextFieldHoursAdd.getText().equals("") && jTextFieldMinsAdd.getText().equals("") && jTextFieldSecsAdd.getText().equals(""))
        {
            jTextFieldHoursAdd.setText("Hours");
            jTextFieldMinsAdd.setText("Mins");
            jTextFieldSecsAdd.setText("Secs");
        }
        else
        {
            if(jTextFieldHoursAdd.getText().equals(""))
            {
                jTextFieldHoursAdd.setText("0");
            }
            if(jTextFieldMinsAdd.getText().equals(""))
            {
                jTextFieldMinsAdd.setText("0");
            }
            if(jTextFieldSecsAdd.getText().equals(""))
            {
                jTextFieldSecsAdd.setText("0");
            }
        }
        
    }//GEN-LAST:event_jTextFieldHoursAddFocusLost

    private void jTextFieldMinsAddFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldMinsAddFocusLost
    {//GEN-HEADEREND:event_jTextFieldMinsAddFocusLost
        if(jTextFieldHoursAdd.getText().equals("") && jTextFieldMinsAdd.getText().equals("") && jTextFieldSecsAdd.getText().equals(""))
        {
            jTextFieldHoursAdd.setText("Hours");
            jTextFieldMinsAdd.setText("Mins");
            jTextFieldSecsAdd.setText("Secs");
        }
        else
        {
            if(jTextFieldHoursAdd.getText().equals(""))
            {
                jTextFieldHoursAdd.setText("0");
            }
            if(jTextFieldMinsAdd.getText().equals(""))
            {
                jTextFieldMinsAdd.setText("0");
            }
            if(jTextFieldSecsAdd.getText().equals(""))
            {
                jTextFieldSecsAdd.setText("0");
            }
        }
    }//GEN-LAST:event_jTextFieldMinsAddFocusLost

    private void jTextFieldSecsAddFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldSecsAddFocusLost
    {//GEN-HEADEREND:event_jTextFieldSecsAddFocusLost
        if(jTextFieldHoursAdd.getText().equals("") && jTextFieldMinsAdd.getText().equals("") && jTextFieldSecsAdd.getText().equals(""))
        {
            jTextFieldHoursAdd.setText("Hours");
            jTextFieldMinsAdd.setText("Mins");
            jTextFieldSecsAdd.setText("Secs");
        }
        else
        {
            if(jTextFieldHoursAdd.getText().equals(""))
            {
                jTextFieldHoursAdd.setText("0");
            }
            if(jTextFieldMinsAdd.getText().equals(""))
            {
                jTextFieldMinsAdd.setText("0");
            }
            if(jTextFieldSecsAdd.getText().equals(""))
            {
                jTextFieldSecsAdd.setText("0");
            }
        }
    }//GEN-LAST:event_jTextFieldSecsAddFocusLost

    private void jTextFieldHoursEditFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldHoursEditFocusLost
    {//GEN-HEADEREND:event_jTextFieldHoursEditFocusLost
        if(jTextFieldHoursEdit.getText().equals("") && jTextFieldMinsEdit.getText().equals("") && jTextFieldSecsEdit.getText().equals(""))
        {
            jTextFieldHoursEdit.setText("Hours");
            jTextFieldMinsEdit.setText("Mins");
            jTextFieldSecsEdit.setText("Secs");
        }
    }//GEN-LAST:event_jTextFieldHoursEditFocusLost

    private void jTextFieldMinsEditFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldMinsEditFocusLost
    {//GEN-HEADEREND:event_jTextFieldMinsEditFocusLost
        if(jTextFieldHoursEdit.getText().equals("") && jTextFieldMinsEdit.getText().equals("") && jTextFieldSecsEdit.getText().equals(""))
        {
            jTextFieldHoursEdit.setText("Hours");
            jTextFieldMinsEdit.setText("Mins");
            jTextFieldSecsEdit.setText("Secs");
        }
    }//GEN-LAST:event_jTextFieldMinsEditFocusLost

    private void jTextFieldSecsEditFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldSecsEditFocusLost
    {//GEN-HEADEREND:event_jTextFieldSecsEditFocusLost
        if(jTextFieldHoursEdit.getText().equals("") && jTextFieldMinsEdit.getText().equals("") && jTextFieldSecsEdit.getText().equals(""))
        {
            jTextFieldHoursEdit.setText("Hours");
            jTextFieldMinsEdit.setText("Mins");
            jTextFieldSecsEdit.setText("Secs");
        }
    }//GEN-LAST:event_jTextFieldSecsEditFocusLost

    private void jRadioButtonInstalledApplicationsStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jRadioButtonInstalledApplicationsStateChanged
    {//GEN-HEADEREND:event_jRadioButtonInstalledApplicationsStateChanged
        updateAddButtonEnabled();
    }//GEN-LAST:event_jRadioButtonInstalledApplicationsStateChanged

    private void jTextFieldWebsitesKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_jTextFieldWebsitesKeyReleased
    {//GEN-HEADEREND:event_jTextFieldWebsitesKeyReleased
        updateAddButtonEnabled();
    }//GEN-LAST:event_jTextFieldWebsitesKeyReleased

    private void jTableInstalledApplicationsMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jTableInstalledApplicationsMouseReleased
    {//GEN-HEADEREND:event_jTableInstalledApplicationsMouseReleased
        if(!jRadioButtonInstalledApplications.isSelected())
            return;
        selectedInstalledApplicationRow = jTableInstalledApplications.getSelectedRow();
        updateAddButtonEnabled();
    }//GEN-LAST:event_jTableInstalledApplicationsMouseReleased

    private void jTableInstalledApplicationsFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTableInstalledApplicationsFocusGained
    {//GEN-HEADEREND:event_jTableInstalledApplicationsFocusGained
        selectedWhitelistWebsiteRow = -1;
        selectedWhitelistProgramRow = -1;
        jTextFieldHoursEdit.setText("Hours");
        jTextFieldMinsEdit.setText("Mins");
        jTextFieldSecsEdit.setText("Secs");
        updateRemoveButtonEnabled();
    }//GEN-LAST:event_jTableInstalledApplicationsFocusGained

    private void jTextFieldWebsitesFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTextFieldWebsitesFocusGained
    {//GEN-HEADEREND:event_jTextFieldWebsitesFocusGained
        selectedWhitelistWebsiteRow = -1;
        selectedWhitelistProgramRow = -1;
        jTextFieldHoursEdit.setText("Hours");
        jTextFieldMinsEdit.setText("Mins");
        jTextFieldSecsEdit.setText("Secs");
        updateRemoveButtonEnabled();
    }//GEN-LAST:event_jTextFieldWebsitesFocusGained

    private void jTableWhitelistedApplicationsFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTableWhitelistedApplicationsFocusGained
    {//GEN-HEADEREND:event_jTableWhitelistedApplicationsFocusGained
        selectedInstalledApplicationRow = -1;
        updateAddButtonEnabled();
    }//GEN-LAST:event_jTableWhitelistedApplicationsFocusGained

    private void jTableWhitelistedWebsitesFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jTableWhitelistedWebsitesFocusGained
    {//GEN-HEADEREND:event_jTableWhitelistedWebsitesFocusGained
        selectedInstalledApplicationRow = -1;
        updateAddButtonEnabled();
    }//GEN-LAST:event_jTableWhitelistedWebsitesFocusGained

    private void jButtonUpdateTimeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonUpdateTimeActionPerformed
    {//GEN-HEADEREND:event_jButtonUpdateTimeActionPerformed
        if(jTextFieldMinsEdit.getText().equals(""))
            jTextFieldMinsEdit.setText("0");
        if(jTextFieldHoursEdit.getText().equals(""))
            jTextFieldHoursEdit.setText("0");
        if(jTextFieldSecsEdit.getText().equals(""))
            jTextFieldSecsEdit.setText("0");
        if(selectedWhitelistProgramRow != -1)
        {
            DefaultTableModel model = (DefaultTableModel) jTableWhitelistedApplications.getModel();
            
            setEntry entry = new setEntry(model.getValueAt(selectedWhitelistProgramRow, 0).toString().split("    ")[0], type.PROGRAM, action.REMOVE);
            model.removeRow(selectedWhitelistProgramRow);
            _actionList.add(entry);
            setEntry addEntry = new setEntry(entry._name, entry._type, action.ADD, Integer.parseInt(jTextFieldHoursEdit.getText()),Integer.parseInt(jTextFieldMinsEdit.getText()),Integer.parseInt(jTextFieldSecsEdit.getText()));
            _actionList.add(addEntry);
            model.addRow(createRowEntry(addEntry));
            jTableWhitelistedApplications.setModel(model);
        }
        else if(selectedWhitelistWebsiteRow != -1)
        {
            DefaultTableModel model = (DefaultTableModel) jTableWhitelistedWebsites.getModel();
            setEntry entry = new setEntry(model.getValueAt(selectedWhitelistWebsiteRow, 0).toString().split("    ")[0], type.WEBSITE, action.REMOVE);
            model.removeRow(selectedWhitelistWebsiteRow);
            _actionList.add(entry);
             setEntry addEntry = new setEntry(entry._name, entry._type, action.ADD, Integer.parseInt(jTextFieldHoursEdit.getText()),Integer.parseInt(jTextFieldMinsEdit.getText()),Integer.parseInt(jTextFieldSecsEdit.getText()));
           _actionList.add(addEntry);
            model.addRow(createRowEntry(addEntry));
            jTableWhitelistedWebsites.setModel(model);
        }
        selectedWhitelistWebsiteRow = -1;
        selectedWhitelistProgramRow = -1;
        updateRemoveButtonEnabled();
        revalidate();
        repaint();
        jTextFieldHoursEdit.setText("Hours");
        jTextFieldMinsEdit.setText("Mins");
        jTextFieldSecsEdit.setText("Secs");
    }//GEN-LAST:event_jButtonUpdateTimeActionPerformed

    private void jTableWhitelistedWebsitesMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jTableWhitelistedWebsitesMouseReleased
    {//GEN-HEADEREND:event_jTableWhitelistedWebsitesMouseReleased
        selectedWhitelistProgramRow = -1;
        selectedWhitelistWebsiteRow = jTableWhitelistedWebsites.getSelectedRow();
        updateRemoveButtonEnabled();
        setEntry entry = parseRowEntry(jTableWhitelistedWebsites.getValueAt(selectedWhitelistWebsiteRow, 0).toString());
        jTextFieldHoursEdit.setText(entry._hours + "");
        jTextFieldMinsEdit.setText(entry._mins + "");
        jTextFieldSecsEdit.setText(entry._sec + "");
        revalidate();
        repaint();
    }//GEN-LAST:event_jTableWhitelistedWebsitesMouseReleased

    private void jTableWhitelistedApplicationsMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jTableWhitelistedApplicationsMouseReleased
    {//GEN-HEADEREND:event_jTableWhitelistedApplicationsMouseReleased
        selectedWhitelistWebsiteRow = -1;
        selectedWhitelistProgramRow = jTableWhitelistedApplications.getSelectedRow();
        updateRemoveButtonEnabled();
        setEntry entry = parseRowEntry(jTableWhitelistedApplications.getValueAt(selectedWhitelistProgramRow, 0).toString());
        jTextFieldHoursEdit.setText(entry._hours + "");
        jTextFieldMinsEdit.setText(entry._mins + "");
        jTextFieldSecsEdit.setText(entry._sec + "");
        revalidate();
        repaint();
    }//GEN-LAST:event_jTableWhitelistedApplicationsMouseReleased

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAddActionPerformed
    {//GEN-HEADEREND:event_jButtonAddActionPerformed
        if(jTextFieldMinsAdd.getText().equals(""))
            jTextFieldMinsAdd.setText("0");
        if(jTextFieldHoursAdd.getText().equals(""))
            jTextFieldHoursAdd.setText("0");
        if(jTextFieldSecsAdd.getText().equals(""))
            jTextFieldSecsAdd.setText("0");
        if(jRadioButtonInstalledApplications.isSelected())
        {
            if(selectedInstalledApplicationRow != -1)
            {
                DefaultTableModel model = (DefaultTableModel) jTableWhitelistedApplications.getModel();
                model.addRow(createRowEntry(jTableInstalledApplications.getValueAt(selectedInstalledApplicationRow, 0).toString(),Integer.parseInt(jTextFieldHoursAdd.getText()),Integer.parseInt(jTextFieldMinsAdd.getText()),Integer.parseInt(jTextFieldSecsAdd.getText())));
                setEntry entry = new setEntry(jTableInstalledApplications.getValueAt(selectedInstalledApplicationRow, 0).toString(),type.PROGRAM, action.ADD, Integer.parseInt(jTextFieldHoursAdd.getText()),Integer.parseInt(jTextFieldMinsAdd.getText()),Integer.parseInt(jTextFieldSecsAdd.getText()));
                _actionList.add(entry);
                
                jTableWhitelistedApplications.setModel(model);
            }
        }
        else
        {
            DefaultTableModel model = (DefaultTableModel) jTableWhitelistedWebsites.getModel();
            model.addRow(createRowEntry(jTextFieldWebsites.getText(),Integer.parseInt(jTextFieldHoursAdd.getText()),Integer.parseInt(jTextFieldMinsAdd.getText()),Integer.parseInt(jTextFieldSecsAdd.getText())));
            setEntry entry = new setEntry(jTextFieldWebsites.getText(), type.WEBSITE, action.ADD, Integer.parseInt(jTextFieldHoursAdd.getText()),Integer.parseInt(jTextFieldMinsAdd.getText()),Integer.parseInt(jTextFieldSecsAdd.getText()));
            _actionList.add(entry);
            
            jTableWhitelistedWebsites.setModel(model);
        }
        jButtonAdd.setEnabled(false);
        jTextFieldHoursAdd.setText("Hours");
        jTextFieldMinsAdd.setText("Mins");
        jTextFieldSecsAdd.setText("Secs");
        revalidate();
        repaint();
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jTextFieldHoursAddKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_jTextFieldHoursAddKeyReleased
    {//GEN-HEADEREND:event_jTextFieldHoursAddKeyReleased
        updateAddButtonEnabled();
    }//GEN-LAST:event_jTextFieldHoursAddKeyReleased

    private void jTextFieldMinsAddMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jTextFieldMinsAddMousePressed
    {//GEN-HEADEREND:event_jTextFieldMinsAddMousePressed
        updateAddButtonEnabled();
    }//GEN-LAST:event_jTextFieldMinsAddMousePressed

    private void jTextFieldSecsAddKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_jTextFieldSecsAddKeyReleased
    {//GEN-HEADEREND:event_jTextFieldSecsAddKeyReleased
        updateAddButtonEnabled();
    }//GEN-LAST:event_jTextFieldSecsAddKeyReleased

    private void updateAddButtonEnabled()
    {
        if(jTextFieldHoursAdd.getText().equals("Hours") || jTextFieldMinsAdd.getText().equals("Mins") || jTextFieldSecsAdd.getText().equals("Secs"))
        {
            jButtonAdd.setEnabled(false);
            return;
        }
        if(jRadioButtonInstalledApplications.isSelected())
        {
            jButtonAdd.setEnabled(selectedInstalledApplicationRow != -1);
        }
        else
        {
            String url = jTextFieldWebsites.getText();
            boolean hasPeroid = url.contains(".");
            String[] spilitt = url.split("\\.");
            boolean has2Parts = spilitt.length >= 2;
            jButtonAdd.setEnabled(hasPeroid && has2Parts);
        }
    }
    
    private void updateRemoveButtonEnabled()
    {
        updateUpdateButtonEnabled();
        jButtonRemove.setEnabled(selectedWhitelistProgramRow != -1 || selectedWhitelistWebsiteRow != -1);
        jTextFieldHoursEdit.setEnabled(selectedWhitelistProgramRow != -1 || selectedWhitelistWebsiteRow != -1);
        jTextFieldMinsEdit.setEnabled(selectedWhitelistProgramRow != -1 || selectedWhitelistWebsiteRow != -1);
        jTextFieldSecsEdit.setEnabled(selectedWhitelistProgramRow != -1 || selectedWhitelistWebsiteRow != -1);
        
    }
    
    private void updateUpdateButtonEnabled()
    {
        jButtonUpdateTime.setEnabled(selectedWhitelistProgramRow != -1 || selectedWhitelistWebsiteRow != -1);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonApply;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonUpdateTime;
    private javax.swing.JLabel jLabelTimeConstraint;
    private javax.swing.JRadioButton jRadioButtonInstalledApplications;
    private javax.swing.JRadioButton jRadioButtonWebsites;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableInstalledApplications;
    private javax.swing.JTable jTableWhitelistedApplications;
    private javax.swing.JTable jTableWhitelistedWebsites;
    private javax.swing.JTextField jTextFieldHoursAdd;
    private javax.swing.JTextField jTextFieldHoursEdit;
    private javax.swing.JTextField jTextFieldMinsAdd;
    private javax.swing.JTextField jTextFieldMinsEdit;
    private javax.swing.JTextField jTextFieldSecsAdd;
    private javax.swing.JTextField jTextFieldSecsEdit;
    private javax.swing.JTextField jTextFieldWebsites;
    // End of variables declaration//GEN-END:variables
}
