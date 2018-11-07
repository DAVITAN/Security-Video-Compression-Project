package SecurityVideoCompProject;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class About extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					About frame = new About();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public About() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 399, 253);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(10, 11, 100, 100);
		contentPane.add(lblNewLabel);
		Image aboutImage=new ImageIcon(this.getClass().getResource("/compress_big.png")).getImage();
		lblNewLabel.setIcon(new ImageIcon(aboutImage));
		JLabel lblSecurityVideoCompression = new JLabel("Security Video Compression App");
		lblSecurityVideoCompression.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, 17));
		lblSecurityVideoCompression.setBounds(120, 11, 279, 21);
		contentPane.add(lblSecurityVideoCompression);
		
		JLabel lblCreatedBy = new JLabel("Created by:");
		lblCreatedBy.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblCreatedBy.setBounds(174, 57, 84, 14);
		contentPane.add(lblCreatedBy);
		
		JLabel lblDorAvitan = new JLabel("Dor Avitan");
		lblDorAvitan.setBounds(186, 82, 74, 14);
		contentPane.add(lblDorAvitan);
		
		JLabel lblOmerSirpad = new JLabel("Omer Sirpad");
		lblOmerSirpad.setBounds(186, 101, 74, 14);
		contentPane.add(lblOmerSirpad);
		
		JLabel lblOmerAmsalem = new JLabel("Omer Amsalem");
		lblOmerAmsalem.setBounds(186, 122, 104, 14);
		contentPane.add(lblOmerAmsalem);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnClose.setBounds(174, 164, 84, 44);
		contentPane.add(btnClose);
	}

}
