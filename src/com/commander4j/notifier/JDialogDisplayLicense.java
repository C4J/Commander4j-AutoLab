package com.commander4j.notifier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;

import com.commander4j.resources.JRes;

public class JDialogDisplayLicense extends JDialog
{

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JScrollPane scrollPane = new JScrollPane();
	private JTextPane textPane = new JTextPane();
	private ImageIcon img = new ImageIcon("./images/windows/image_sys_control.gif");

	/**
	 * Create the dialog.
	 */
	public JDialogDisplayLicense(JDialog parent, JLicenseInfo license)
	{
		super(parent);

		setIconImage(img.getImage());
		setTitle(license.getDescription() + " (" + license.type + ")");
		setResizable(false);
		setAlwaysOnTop(true);
		setSize(767, 587);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		scrollPane.setBounds(4, 10, 745, 500);
		contentPanel.add(scrollPane);

		textPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textPane.setBackground(new Color(250, 250, 210));
		textPane.setForeground(Color.BLACK);
		textPane.setEditable(false);
		loadLicense(license.licenceFilename);
		scrollPane.setViewportView(textPane);

		JButton okClose = new JButton(JRes.getText("close"));
		okClose.setBounds(330, 518, 106, 29);
		okClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		okClose.setActionCommand("OK");
		contentPanel.add(okClose);
		getRootPane().setDefaultButton(okClose);
	}

	private void loadLicense(String filename)
	{
		String getfilename = "." + File.separator + "lib" + File.separator + "license" + File.separator + filename;

		try
		{
			String content = new String(Files.readAllBytes(Paths.get(getfilename)));

			textPane.setText(content);
			textPane.setCaretPosition(0);
			JViewport jv = scrollPane.getViewport();
			jv.setViewPosition(new Point(0, 0));
		}
		catch (IOException e)
		{
			textPane.setText(e.getMessage());
		}
	}

}
