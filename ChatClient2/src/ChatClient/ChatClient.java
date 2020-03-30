/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author andik
 */
public class ChatClient extends javax.swing.JFrame {

    /**
     * Creates new form ChatClient
     */
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    private String server, username;
    private int port;
    private List<String> clients;
    private List<PMDialog> dialogs;

    public ChatClient() {
        clients = new ArrayList<>();
        dialogs = new ArrayList<>();
        initComponents();
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (Exception ec) {
            System.out.println("Error connectiong to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(msg);

        try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            System.out.println("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        new ChatClient.ListenFromServer().start();

        try {
            output.writeObject("login~" + username + "~" + username + " sedang login...~server~\n");
            output.writeObject("list~" + username + "~" + username + " sedang login...~server~\n");

        } catch (IOException eIO) {
            System.out.println("Exception doing login : " + eIO);
            disconnect();
            return false;
        }

        return true;
    }

    private void disconnect() {
        try {
            // TODO add your handling code here:
            output.writeObject("logout~" + username + "~" + username + " sudah logout...~Server~\n");
        } catch (IOException ex) {
            //Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (Exception e) {
        }
        try {
            if (output != null) {
                output.close();
            }
        } catch (Exception e) {
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
        }
    }

    class ListenFromServer extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    String msg = (String) input.readObject();
                    String res;
                    String type = msg.split("~")[0];
                    String pengirim = msg.split("~")[1];
                    String text = msg.split("~")[2];
                    String kepada = msg.split("~")[3];
                    switch (type) {
                        case "recieveText":
                            res = pengirim + ": " + text;
                            viewTextArea.setText(viewTextArea.getText() + res + "\n");
                            break;
                        case "recievePrivateText":
                            res = pengirim + ": " + text;
                            
                            if (kepada.equals(username)) {
                                for (PMDialog pMDialog : dialogs) {
                                    if (pMDialog.getName().equals(pengirim)) {
                                        pMDialog.display(res);
                                        pMDialog.setVisible(true);
                                        break;
                                    }
                                }
                            }

                            break;
                        case "login":
                            viewTextArea.setText(viewTextArea.getText() + pengirim + " sedah login..." + "\n");
                            clients.add(pengirim);
                            break;
                        case "logout":
                            viewTextArea.setText(viewTextArea.getText() + pengirim + " telah logout..." + "\n");
                            clients.remove(pengirim);
                            for (PMDialog pMDialog : dialogs) {
                                if (pMDialog.getName().equals(pengirim)) {
                                    dialogs.remove(pMDialog);
                                    break;
                                }
                            }
                            break;
                        case "list":
                            setTable(text);
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("Server has close the connection: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                }
            }
        }

        private void setTable(String text) {
            int rows = text.split(":").length - 1;
            Object[][] data = new Object[rows][1];

            for (int i = 0; i < rows; i++) {
                String t = text.split(":")[i];
                data[i][0] = t;

                boolean ada = false;
                for (PMDialog pMDialog : dialogs) {
                    if (pMDialog.getName().equals(t)) {
                        ada = true;
                    }
                }

                if (!ada) {
                    PMDialog pmd = new PMDialog(ChatClient.this, socket, input, output, username, t);
                    pmd.setName(t);
                    pmd.setTitle(username + "/" + t);
                    dialogs.add(pmd);
                }
            }

            String[] header = {"Clients"};

            clientTable.setModel(new DefaultTableModel(data, header) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        viewTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        clientTable = new javax.swing.JTable();
        postTextField = new javax.swing.JTextField();
        kirimButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        serverTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        portTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        usernameTextField = new javax.swing.JTextField();
        masukButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        viewTextArea.setColumns(20);
        viewTextArea.setRows(5);
        jScrollPane1.setViewportView(viewTextArea);

        clientTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clientTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clientTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(clientTable);

        postTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postTextFieldActionPerformed(evt);
            }
        });

        kirimButton.setBackground(new java.awt.Color(51, 153, 255));
        kirimButton.setForeground(new java.awt.Color(255, 255, 255));
        kirimButton.setText("Send");
        kirimButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kirimButtonActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 102, 153));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Server");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Port");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Username");

        masukButton.setBackground(new java.awt.Color(0, 153, 255));
        masukButton.setForeground(new java.awt.Color(255, 255, 255));
        masukButton.setText("Search");
        masukButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                masukButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(serverTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(masukButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(serverTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(masukButton))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(postTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kirimButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(kirimButton)
                            .addComponent(postTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void masukButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_masukButtonActionPerformed
        // TODO add your handling code here:
        this.server = serverTextField.getText();
        this.port = new Integer(portTextField.getText());
        this.username = usernameTextField.getText();
        setTitle(server + ":" + port + "/" + username);
        start();
    }//GEN-LAST:event_masukButtonActionPerformed

    private void kirimButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kirimButtonActionPerformed
        // TODO add your handling code here:
        try {
            String message = "postText~" + username + "~" + postTextField.getText() + "~all~\n";
            output.writeObject(message);
            postTextField.setText("");
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_kirimButtonActionPerformed

    private void postTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postTextFieldActionPerformed
        // TODO add your handling code here:
        kirimButtonActionPerformed(evt);
    }//GEN-LAST:event_postTextFieldActionPerformed

    private void clientTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientTableMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2 && clientTable.getSelectedRow() >= 0) {
            String kepada = (String) clientTable.getValueAt(clientTable.getSelectedRow(), 0);
            for (PMDialog pMDialog : dialogs) {
                if (pMDialog.getName().equals(kepada) && !kepada.equals(username)) {
                    pMDialog.setTitle(username + "/" + kepada);
                    pMDialog.display("Silakan tulis pesan kepada " + kepada);
                    pMDialog.setVisible(true);
                    return;
                }
            }
        }
    }//GEN-LAST:event_clientTableMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable clientTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton kirimButton;
    private javax.swing.JButton masukButton;
    private javax.swing.JTextField portTextField;
    private javax.swing.JTextField postTextField;
    private javax.swing.JTextField serverTextField;
    private javax.swing.JTextField usernameTextField;
    private javax.swing.JTextArea viewTextArea;
    // End of variables declaration//GEN-END:variables
}
