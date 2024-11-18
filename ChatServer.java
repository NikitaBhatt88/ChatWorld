package ChatWorld;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class ChatServer implements ActionListener {
    JTextField messageField; // TextField for inputting messages
    JPanel messagePanel; // Panel to display messages
    static DataOutputStream outputStream;
    
    static Box messageBox = Box.createVerticalBox(); // Box to hold messages vertically
    static JFrame frame = new JFrame(); // Main window frame
    
    ChatServer() {
       
       frame.setLayout(null);
       
       JPanel headerPanel = new JPanel(); // Header panel for the chat window
       headerPanel.setBackground(Color.BLACK);
       headerPanel.setBounds(0, 0, 450, 70);
       headerPanel.setLayout(null);
       frame.add(headerPanel);
       
       
       // Profile image
       ImageIcon profileIcon = new ImageIcon(ClassLoader.getSystemResource("icons/Nikita.jpeg"));
       Image profileImage = profileIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
       ImageIcon scaledProfileIcon = new ImageIcon(profileImage);
       JLabel profileLabel = new JLabel(scaledProfileIcon);
       profileLabel.setBounds(30, 10, 50, 50);
       headerPanel.add(profileLabel);
       
       
       // User name label
       JLabel userNameLabel = new JLabel("Nikita");
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
       
       // Back button icon
       ImageIcon closeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/close.png"));
       Image closeImage = closeIcon.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
       ImageIcon scaledBackIcon = new ImageIcon(closeImage);
       JLabel closeButton = new JLabel(scaledBackIcon);
       closeButton.setBounds(410, 20, 25, 25);
       headerPanel.add(closeButton);
       
       // Exit action on clicking the back button
       closeButton.addMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent ae) {
               System.exit(0);
           }
       });
       
       messagePanel = new JPanel(); // Panel to display chat messages
       messagePanel.setBounds(5, 75, 440, 570);
       frame.add(messagePanel);
       
       // Add scroll pane for the message panel
       JScrollPane scrollPane = new JScrollPane(messagePanel);
       scrollPane.setBounds(5, 75, 440, 570);
       scrollPane.setBorder(BorderFactory.createEmptyBorder());
       frame.add(scrollPane);
       
       // Message input field
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
       frame.setLocation(200, 50);
       frame.setUndecorated(true);
       frame.getContentPane().setBackground(Color.WHITE);
       frame.setVisible(true);
   }
   
   // Action listener for send button
   public void actionPerformed(ActionEvent ae) {
       try {
           String outgoingMessage = messageField.getText();
           
           JPanel formattedPanel = formatLabel(outgoingMessage, true);
           
           messagePanel.setLayout(new BorderLayout());
           
           JPanel rightPanel = new JPanel(new BorderLayout());
           rightPanel.add(formattedPanel, BorderLayout.LINE_END);
           messageBox.add(rightPanel);
           messageBox.add(Box.createVerticalStrut(15));
           
           messagePanel.add(messageBox, BorderLayout.PAGE_START);
           
           outputStream.writeUTF(outgoingMessage); // Send message to client
           
           messageField.setText("");
           
           frame.repaint();
           frame.invalidate();
           frame.validate();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   
   // Method to format messages
   public static JPanel formatLabel(String message,  boolean isSent) {
       JPanel messageContainer = new JPanel();
       messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
       
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
       
       messageContainer.add(messageLabel);
       
       Calendar calendar = Calendar.getInstance();
       SimpleDateFormat dateFormat = new SimpleDateFormat("HH:MM");
       
       JLabel timeLabel = new JLabel();
       timeLabel.setText(dateFormat.format(calendar.getTime()));
       
       messageContainer.add(timeLabel);
       return messageContainer;
   }
   
   public static void main(String[] args) {
       new ChatServer();
       
       try {
           ServerSocket serverSocket = new ServerSocket(9999); // Server listening on port 9999
           while (true) {
               Socket socket = serverSocket.accept(); // Accept client connection
               DataInputStream inputStream = new DataInputStream(socket.getInputStream());
               outputStream = new DataOutputStream(socket.getOutputStream());
               
               while (true) {
                   String incomingMessage = inputStream.readUTF();
                   JPanel receivedPanel = formatLabel(incomingMessage, false);
                   
                   JPanel leftPanel = new JPanel(new BorderLayout());
                   leftPanel.add(receivedPanel, BorderLayout.LINE_START);
                   messageBox.add(leftPanel);
                   frame.validate();
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}
