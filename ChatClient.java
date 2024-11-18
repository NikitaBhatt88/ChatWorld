package ChatWorld;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class ChatClient implements ActionListener {
    JTextField messageField;
    static JPanel messagePanel;
    static DataOutputStream dataOut;
    
    static JFrame frame = new JFrame();
    static Box messageContainer = Box.createVerticalBox();
    
    ChatClient(){
       
       frame.setLayout(null);
       
       JPanel headerPanel = new JPanel(); // Header panel for title, profile, icons
       headerPanel.setBackground(Color.BLACK);
       headerPanel.setBounds(0, 0, 450, 70);
       headerPanel.setLayout(null);
       frame.add(headerPanel);
       
       
       
       // Profile image
       ImageIcon profileIcon = new ImageIcon(ClassLoader.getSystemResource("icons/Roshni.jpeg"));
       Image profileImage = profileIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
       ImageIcon scaledProfileIcon = new ImageIcon(profileImage);
       JLabel profileLabel = new JLabel(scaledProfileIcon);
       profileLabel.setBounds(40, 10, 50, 50);
       headerPanel.add(profileLabel);
       
       
       // User name label
       JLabel userNameLabel = new JLabel("Roshni");
       userNameLabel.setBounds(110, 15, 100, 18);
       userNameLabel.setForeground(Color.WHITE);
       userNameLabel.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
       headerPanel.add(userNameLabel);
       
       // User status label
       JLabel userStatusLabel = new JLabel("Active now");
       userStatusLabel.setBounds(110, 35, 100, 18);
       userStatusLabel.setForeground(Color.WHITE);
       userStatusLabel.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
       headerPanel.add(userStatusLabel);
       
       
       // Back button
       ImageIcon closeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/close.png"));
       Image closeImage = closeIcon.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
       ImageIcon scaledBackIcon = new ImageIcon(closeImage);
       JLabel closeLabel = new JLabel(scaledBackIcon);
       closeLabel.setBounds(410, 20, 25, 25);
       headerPanel.add(closeLabel);
       
       closeLabel.addMouseListener(new MouseAdapter(){
           public void mouseClicked(MouseEvent ae){
               System.exit(0);
           }
       });
       
       messagePanel = new JPanel();
       messagePanel.setBounds(5, 75, 440, 570);
       frame.add(messagePanel);
       
       // Add scroll pane for the message panel
       JScrollPane scrollPane = new JScrollPane(messagePanel);
       scrollPane.setBounds(5, 75, 440, 570);
       scrollPane.setBorder(BorderFactory.createEmptyBorder());
       frame.add(scrollPane);
       
       // Input text field for messages
       messageField = new JTextField();
       messageField.setBounds(5, 655, 310, 40);
       messageField.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
       frame.add(messageField);
       
       // Send button
       JButton sendButton = new JButton("Send");
       sendButton.setBounds(320, 655, 123, 40);
       sendButton.setBackground(Color.BLACK);
       sendButton.setForeground(Color.WHITE);
       sendButton.addActionListener(this);
       sendButton.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
       frame.add(sendButton);

       frame.setSize(450, 700);
       frame.setLocation(800, 50);
       frame.setUndecorated(true);
       frame.getContentPane().setBackground(Color.WHITE);
       frame.setVisible(true);
   }
   
   public void actionPerformed(ActionEvent ae){
       try {
           String outgoingMessage = messageField.getText();
           
           JPanel formattedPanel = formatLabel(outgoingMessage, true);
           
           messagePanel.setLayout(new BorderLayout());
           
           JPanel rightPanel = new JPanel(new BorderLayout());
           rightPanel.add(formattedPanel, BorderLayout.LINE_END);
           messageContainer.add(rightPanel);
           messageContainer.add(Box.createVerticalStrut(15));
           
           messagePanel.add(messageContainer, BorderLayout.PAGE_START);
           
           dataOut.writeUTF(outgoingMessage); // Send message to the server
           
           messageField.setText("");
           
           frame.repaint();
           frame.invalidate();
           frame.validate();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   
   public static JPanel formatLabel(String message, boolean isSent){
       JPanel messageBox = new JPanel();
       messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
       
       JLabel messageLabel = new JLabel("<html><p style=\"width: 150\">" + message + "</p></html>");
       messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
       
       // Change background color based on whether the message is sent or received
       if (isSent) {
           messageLabel.setBackground(Color.PINK);
       } else {
           messageLabel.setBackground(Color.LIGHT_GRAY);
       }
       
       messageLabel.setOpaque(true);
       messageLabel.setBorder(new EmptyBorder(15, 15, 15, 50));
       
       messageBox.add(messageLabel);
       
       Calendar calendar = Calendar.getInstance();
       SimpleDateFormat dateFormat = new SimpleDateFormat("HH:MM");
       
       JLabel timeLabel = new JLabel();
       timeLabel.setText(dateFormat.format(calendar.getTime()));
       
       messageBox.add(timeLabel);
       return messageBox;
   }
   
   public static void main(String[] args) {
       new ChatClient();
       
       try {
           Socket socket = new Socket("127.0.0.1", 9999);
           DataInputStream dataIn = new DataInputStream(socket.getInputStream());
           dataOut = new DataOutputStream(socket.getOutputStream());
           
           while (true) {
               messagePanel.setLayout(new BorderLayout());
               String incomingMessage = dataIn.readUTF();
               JPanel receivedPanel = formatLabel(incomingMessage, false);
               
               JPanel leftPanel = new JPanel(new BorderLayout());
               leftPanel.add(receivedPanel, BorderLayout.LINE_START);
               messageContainer.add(leftPanel);
               
               messageContainer.add(Box.createVerticalStrut(15));
               messagePanel.add(messageContainer, BorderLayout.PAGE_START);
               frame.validate();
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}
