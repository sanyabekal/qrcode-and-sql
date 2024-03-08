import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;


public class trying extends JFrame {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/sanu";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sanyabekal";

    private JTextField contentTextField;
    private JTextField nameTextField;
    private JTextField dobTextField;

    private JLabel qrCodeLabel;

    public trying() {
        setTitle("QR Code Generator and MySQL Saver");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 25));
        nameLabel.setForeground(Color.red);
        nameTextField = new JTextField();
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font("Georgia", Font.BOLD, 25));
        dobLabel.setForeground(Color.red);
        dobTextField = new JTextField();
        contentTextField = new JTextField();
        contentTextField.setFont(new Font("Georgia", Font.BOLD, 25));
        JButton generateButton = new JButton("Generate QR Code");
        qrCodeLabel = new JLabel();
        JButton saveButton = new JButton("Save to MySQL");

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateQRCode();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToMySQL();
            }
        });

        JPanel panel = new JPanel();
        panel.setBackground(Color.ORANGE);
        panel.setLayout(new GridLayout(8, 4));
        JLabel formHeader = new JLabel("QR Code Generator Form");
        formHeader.setFont(new Font("Georgia", Font.BOLD, 40));
        formHeader.setForeground(Color.black);
        panel.add(formHeader);
        panel.add(nameLabel);
        panel.add(nameTextField);
        panel.add(dobLabel);
        panel.add(dobTextField);
        panel.add(new JLabel("Content:"));
        panel.add(contentTextField);
        panel.add(generateButton);
        panel.add(qrCodeLabel);
        panel.add(saveButton);

        add(panel);
    }

    private void generateQRCode() {
        try {
            String content = contentTextField.getText();

            // Generate QR code

            if (!content.isEmpty()) {
                // Call your method to generate QR code here
                String imagePath = generateQRCodeImage(content);
                if (imagePath != null) {
                    // Show generated QR code in a new window
                    showQRCode(imagePath);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to generate QR code", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a URL", "Error", JOptionPane.ERROR_MESSAGE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String generateQRCodeImage(String url) {
        String fileName = UUID.randomUUID().toString() + ".png";
        try {
            createQR(url, fileName, "UTF-8", new HashMap<>(), 300, 300);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void showQRCode(String imagePath) {
        // Display the QR code image in a new window
        ImageIcon icon = new ImageIcon(imagePath);
        JLabel label = new JLabel(icon);
        JFrame qrCodeFrame = new JFrame("QR Code");
        qrCodeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        qrCodeFrame.getContentPane().add(label);
        qrCodeFrame.pack();
        qrCodeFrame.setVisible(true);
    }
    public  void createQR(String data, String path,
                          String charset, Map hashMap,
                          int height, int width)
            throws WriterException, IOException
    {

        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(data.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, width, height);

        MatrixToImageWriter.writeToFile(
                matrix,
                path.substring(path.lastIndexOf('.') + 1),
                new File(path));
    }


    private void saveToMySQL() {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            // Prepare the INSERT statement

            String content = contentTextField.getText();
            String name= nameTextField.getText();
            String dob =dobTextField.getText();

            String insertSQL = "INSERT INTO qr_table (name,content,dob) VALUES (name,content,dob)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                // Set the parameter
                preparedStatement.setString(1, nameTextField.getText());
                preparedStatement.setString(1,contentTextField.getText() );
                preparedStatement.setString(1, dobTextField.getText());

                // Execute the INSERT statement
                preparedStatement.executeUpdate();

                System.out.println("Data saved to MySQL successfully!");
            }

            // Close the connection
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new trying().setVisible(true);
            }
        });
    }
}
