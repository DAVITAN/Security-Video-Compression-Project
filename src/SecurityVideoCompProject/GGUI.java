package SecurityVideoCompProject;




import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmNew;
	private JMenuItem mntmExit;
	private File file=null;
	private File outFolder=null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GGUI frame = new GGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
	/**
	 * Create the frame.
	 */
	public GGUI() {
		String workingDir = System.getProperty("user.dir");
		
		final JFileChooser fc = new JFileChooser(workingDir);
		final JFileChooser dc = new JFileChooser(workingDir);
		//Set mode to select output FOLDER and not file.
		dc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//Make the fileChooser show only some types of files
		FileNameExtensionFilter vidfilter = new FileNameExtensionFilter(
			     "Video Files (*.mp4,*.avi,*.mov)", "mp4","avi","mov");
		FileNameExtensionFilter odofilter = new FileNameExtensionFilter(
			     "compressed odo files", "odo");
		setTitle("ODO Inc. - Security Video Compressor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 581, 451);
		
		menuBar = new JMenuBar();
		menuBar.setForeground(new Color(255, 255, 255));
		menuBar.setBackground(new Color(30, 144, 255));
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		mnFile.setForeground(new Color(255, 255, 255));
		menuBar.add(mnFile);
		
		mntmNew = new JMenuItem("New");
		
		mntmNew.setHorizontalTextPosition(SwingConstants.LEADING);
		mntmNew.setHorizontalAlignment(SwingConstants.LEFT);
		mnFile.add(mntmNew);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnNewMenu = new JMenu("Help");
		mnNewMenu.setForeground(new Color(255, 255, 255));
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmQuickGuide = new JMenuItem("Quick Guide");
		mntmQuickGuide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QuickGuide quickg=new QuickGuide();
				quickg.setVisible(true);
			}
		});
		mnNewMenu.add(mntmQuickGuide);
		
		JMenuItem mntmDocumentation = new JMenuItem("Documentation");
		
		mnNewMenu.add(mntmDocumentation);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				About about=new About();
				about.setVisible(true);
			}
		});
		mnNewMenu.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		
		
		JTextPane txtpnCreatedByOdo = new JTextPane();
		txtpnCreatedByOdo.setBounds(0, 371, 419, 20);
		txtpnCreatedByOdo.setBackground(new Color(128, 128, 128));
		txtpnCreatedByOdo.setText("Created by O.D.O");
		
		JButton btnDecompress = new JButton("Decompress");
		btnDecompress.setEnabled(false);
		btnDecompress.setBounds(454, 215, 107, 52);
		
		JButton btnDecompressFile = new JButton("Decompress a File");
		btnDecompressFile.setBounds(156, 12, 160, 35);
		btnDecompressFile.setHorizontalAlignment(SwingConstants.LEFT);
		btnDecompressFile.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnDecompressFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		JButton btnCompressFile = new JButton("Compress a File");
		btnCompressFile.setBounds(5, 11, 141, 35);
		
		btnCompressFile.setHorizontalAlignment(SwingConstants.LEFT);
		btnCompressFile.setFont(new Font("Tahoma", Font.PLAIN, 10));
		Image img = new ImageIcon(this.getClass().getResource("/compress.png")).getImage();
		btnCompressFile.setIcon(new ImageIcon(img));
		btnDecompressFile.setRolloverIcon(new ImageIcon(img));
		btnDecompressFile.setPressedIcon(new ImageIcon(img));
		img=new ImageIcon(this.getClass().getResource("/compress_pressed.png")).getImage();
		btnDecompressFile.setIcon(new ImageIcon(img));
		btnCompressFile.setPressedIcon(new ImageIcon(img));
		btnCompressFile.setRolloverIcon(new ImageIcon(img));;
		
		

		
		JLabel label = new JLabel("");
		label.setBounds(156, 70, 0, 0);
		contentPane.setLayout(null);
		contentPane.add(txtpnCreatedByOdo);
		contentPane.add(btnDecompress);
		contentPane.add(btnCompressFile);
		contentPane.add(label);
		
		
		
		JPanel dataPanel = new JPanel();
		dataPanel.setVisible(false);
		dataPanel.setBackground(new Color(255, 255, 255));
		dataPanel.setBounds(15, 57, 429, 123);
		contentPane.add(dataPanel);
		dataPanel.setLayout(null);
		
		JLabel fNameLbl = new JLabel("Filename");
		fNameLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		fNameLbl.setBounds(10, 23, 62, 14);
		dataPanel.add(fNameLbl);
		
		JLabel fSizeLbl = new JLabel("Size");
		fSizeLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		fSizeLbl.setBounds(10, 48, 42, 14);
		dataPanel.add(fSizeLbl);
		
		JLabel fTypeLbl = new JLabel("Type");
		fTypeLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		fTypeLbl.setBounds(10, 73, 42, 14);
		dataPanel.add(fTypeLbl);
		
		JLabel fNamedata = new JLabel(".");
		fNamedata.setBounds(98, 23, 125, 14);
		dataPanel.add(fNamedata);
		
		JLabel fSizeData = new JLabel(".");
		fSizeData.setBounds(98, 48, 125, 14);
		dataPanel.add(fSizeData);
		
		JLabel Ftypedata = new JLabel(".");
		Ftypedata.setBounds(98, 73, 125, 14);
		dataPanel.add(Ftypedata);
		
		JLabel outputFLbl = new JLabel("Output Folder");
		outputFLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		outputFLbl.setBounds(10, 98, 89, 14);
		dataPanel.add(outputFLbl);
		
		JLabel outputFData = new JLabel(".");
		outputFData.setBounds(98, 98, 321, 14);
		dataPanel.add(outputFData);
		
		JSpinner compLvl = new JSpinner();
		compLvl.setVisible(false);
		compLvl.setModel(new SpinnerNumberModel(1, 1, 7, 1));
		compLvl.setBounds(233, 45, 29, 20);
		dataPanel.add(compLvl);
		
		JLabel comLvlLbl = new JLabel("Compression Level (1-7)");
		comLvlLbl.setVisible(false);
		comLvlLbl.setBounds(199, 23, 140, 14);
		dataPanel.add(comLvlLbl);
		
		JButton btnCompress = new JButton("Compress");
		btnCompress.setEnabled(false);
		btnCompress.setBounds(454, 152, 107, 52);
		
		contentPane.add(btnDecompressFile);
		contentPane.add(btnCompress);
		
		JPanel finPanel = new JPanel();
		finPanel.setBackground(new Color(153, 255, 204));
		finPanel.setVisible(false);
		finPanel.setBounds(15, 191, 429, 52);
		contentPane.add(finPanel);
		finPanel.setLayout(null);
		
		JLabel lblCompressedSize = new JLabel("Compressed Size: ");
		lblCompressedSize.setBounds(10, 11, 111, 14);
		finPanel.add(lblCompressedSize);
		
		JLabel lblCompressed = new JLabel("Compressed %: ");
		lblCompressed.setBounds(10, 30, 130, 14);
		finPanel.add(lblCompressed);
		
		JLabel compSize = new JLabel(".");
		compSize.setBounds(150, 11, 147, 14);
		finPanel.add(compSize);
		
		JLabel compPrecentage = new JLabel(".");
		compPrecentage.setBounds(150, 30, 147, 14);
		finPanel.add(compPrecentage);
		
		JPanel timePanel = new JPanel();
		timePanel.setVisible(false);
		timePanel.setBackground(new Color(153, 255, 204));
		timePanel.setBounds(15, 254, 429, 35);
		contentPane.add(timePanel);
		timePanel.setLayout(null);
		
		JLabel lblTimeTaken = new JLabel("Time taken:");
		lblTimeTaken.setBounds(10, 11, 84, 14);
		timePanel.add(lblTimeTaken);
		
		JLabel timeTakenLbl = new JLabel(".");
		timeTakenLbl.setBounds(150, 11, 147, 14);
		timePanel.add(timeTakenLbl);
		
		JLabel lblBgimg = new JLabel("BGImg");
		lblBgimg.setBounds(0, 0, 565, 391);
		contentPane.add(lblBgimg);
		img=new ImageIcon(this.getClass().getResource("/ODO_TM.jpg")).getImage();
		lblBgimg.setIcon(new ImageIcon(img));
		
		btnCompressFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				finPanel.setVisible(false);
				timePanel.setVisible(false);
				fc.setDialogTitle("Choose a video file");
				fc.setFileFilter(vidfilter);
				int retVal = fc.showOpenDialog(GGUI.this);
				if (retVal == JFileChooser.APPROVE_OPTION) {
		            file = fc.getSelectedFile();
		            infoBox("Please choose an output folder", "Choose output folder");
		            dc.setDialogTitle("Choose output folder for the encoded file");
		            int retValOutput=dc.showOpenDialog(fc);
					if (retValOutput == JFileChooser.APPROVE_OPTION) {
						btnCompress.setEnabled(true);
						outFolder=dc.getSelectedFile();
						dataPanel.setVisible(true);
						fNamedata.setText(file.getName().substring(0, file.getName().length() - 4));
						Ftypedata.setText(file.getName().substring(file.getName().length() - 4));
						fSizeData.setText(Integer.toString((int) (file.length() / 1024)) + " KB");
						outputFData.setText(outFolder.getPath());
						compLvl.setVisible(true);
						comLvlLbl.setVisible(true);
						btnDecompress.setEnabled(false);
		            }else{
		            	infoBox("No output folder selected, aborting", "Warning: no folder selected");
		            }
		            
		            
		        } else {
		        	infoBox("Cancelled :( ", "Cancelled by user");
		        }
				
				
			}
		});
		btnDecompressFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				finPanel.setVisible(false);
				timePanel.setVisible(false);
				compLvl.setVisible(false);
				comLvlLbl.setVisible(false);
				fc.setDialogTitle("Choose an odo compressed file");
				fc.setFileFilter(odofilter);
				int retVal = fc.showOpenDialog(GGUI.this);
				if (retVal == JFileChooser.APPROVE_OPTION) {
		            file = fc.getSelectedFile();
		            infoBox("Please choose an output folder", "Choose output folder");
		            dc.setDialogTitle("Choose output folder for the decoded file");
		            int retValOutput=dc.showOpenDialog(fc);
					if (retValOutput == JFileChooser.APPROVE_OPTION) {
						btnDecompress.setEnabled(true);
						outFolder=dc.getSelectedFile();
						dataPanel.setVisible(true);
						fNamedata.setText(file.getName().substring(0, file.getName().length() - 4));
						Ftypedata.setText(file.getName().substring(file.getName().length() - 4));
						fSizeData.setText(Integer.toString((int) (file.length() / 1024)) + " KB");
						outputFData.setText(outFolder.getPath());
						btnCompress.setEnabled(false);
		            }else{
		            	infoBox("No output folder selected, aborting", "Warning: no folder selected");
		            }
		            
		        } else {
		        	infoBox("Cancelled :( ", "Cancelled by user");
		        }
				
				
			}
		});

		btnCompress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				long sTime=System.currentTimeMillis();
				  String toCompress = file.getName();
				  String path=file.getPath();
				  infoBox("compressing to : " + outFolder.getPath()+". Please be patient, may take a while.. Click OK to continue", "compressing");
				  VidCompressor compressor = new VidCompressor(toCompress,path,(int) compLvl.getValue(),outFolder.getPath());
				  System.out.println((int) compLvl.getValue());
			      int compressedSize=compressor.compress(); 
			      compSize.setText(Integer.toString(compressedSize/1024)+"KB");
			      
			      compPrecentage.setText(Double.toString(100-(((double)compressedSize/file.length())*100)).substring(0, 5)+"%");
			      long eTime=System.currentTimeMillis();
			      infoBox("Done! \n compressed to "+outFolder.getPath()+"\\"+file.getName().replace(".mp4", "_encoded.odo"), "Done");
			      timeTakenLbl.setText(Double.toString(((double)eTime-sTime)/1000)+"s"); 
			      timePanel.setVisible(true);
			      finPanel.setVisible(true);
			      btnCompress.setEnabled(false);
			      
			}
		});
		
		btnDecompress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long sTime = System.currentTimeMillis();
				String toDecompress = file.getPath();
				infoBox("decompressing to : " + outFolder.getPath()+". \n Please be patient, may take a while.. \n Click OK to continue", "decompressing");
				VidDecompressor deComp = new VidDecompressor(toDecompress,outFolder.getPath());
				deComp.decompress();
				btnDecompress.setEnabled(false);
				
				long eTime = System.currentTimeMillis();
				infoBox("Done! \n decompressed to "+outFolder.getPath()+"\\"+file.getName().replace("_encoded.odo","_Decoded.mp4"), "Done");
				timePanel.setVisible(true);
				timeTakenLbl.setText(Double.toString(((double) eTime - sTime) / 1000)+"s");
			}
		});
		
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finPanel.setVisible(false);
				dataPanel.setVisible(false);
				timePanel.setVisible(false);
			}
		});
		
		mntmDocumentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
				    try {
				    	String workingDir = System.getProperty("user.dir");
						String pathDoc=workingDir + "/Documentation.pdf";
				        File myFile = new File(pathDoc);
				        Desktop.getDesktop().open(myFile);
				    } catch (IOException ex) {
				        // no application registered for PDFs
				    }
				}
			}
		});
	}
}
