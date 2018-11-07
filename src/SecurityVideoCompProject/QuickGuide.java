package SecurityVideoCompProject;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;

public class QuickGuide extends JFrame {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5128082105588253429L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					QuickGuide frame = new QuickGuide();
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
	public QuickGuide() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 651, 412);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblImg = new JLabel("Img");
		Image DocImage=new ImageIcon(this.getClass().getResource("/QuickGuide.jpg")).getImage();
		lblImg.setIcon(new ImageIcon(DocImage));
		lblImg.setBounds(0, 0, 635, 306);
		contentPane.add(lblImg);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnClose.setBounds(257, 328, 89, 23);
		contentPane.add(btnClose);
	}

}
