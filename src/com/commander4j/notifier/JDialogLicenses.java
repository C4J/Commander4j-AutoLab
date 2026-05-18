package com.commander4j.notifier;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import com.commander4j.resources.JRes;

public class JDialogLicenses extends JDialog
{

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JList<JLicenseInfo> list = new JList<JLicenseInfo>();
	private ImageIcon img = new ImageIcon("./images/windows/image_sys_control.gif");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			JDialogLicenses dialog = new JDialogLicenses();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public JDialogLicenses()
	{
		JDialogLicenses me = this;

		setIconImage(img.getImage());
		setTitle(JRes.getText("licence_information"));
		setResizable(false);
		setAlwaysOnTop(true);
		setSize(767, 599);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		Font listFont = new Font("Monospaced", Font.PLAIN, 13);

		JLabel lblHeader = new JLabel(header());
		lblHeader.setFont(listFont);
		lblHeader.setBounds(10, 5, 727, 16);
		contentPanel.add(lblHeader);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 24, 745, 492);
		contentPanel.add(scrollPane);

		list.setFont(listFont);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					if (list.isSelectionEmpty() == false)
					{
						JLicenseInfo selected = list.getSelectedValue();
						JDialogDisplayLicense dialog = new JDialogDisplayLicense(me, selected);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					}
				}
			}
		});
		scrollPane.setViewportView(list);

		JButton okClose = new JButton(JRes.getText("close"));
		okClose.setBounds(330, 524, 106, 29);
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

		populateList();
	}

	private String pad(String input, int size)
	{
		String result = input;

		while (result.length() < size)
		{
			result = result + " ";
		}

		return result;
	}

	private String header()
	{
		return pad(JRes.getText("library"), JLicenseInfo.width_description) + pad(JRes.getText("version"), JLicenseInfo.width_version) + JRes.getText("licence");
	}

	private void populateList()
	{
		DefaultListModel<JLicenseInfo> model = new DefaultListModel<JLicenseInfo>();

		JLicenseInfo loader = new JLicenseInfo();

		LinkedList<JLicenseInfo> licenceList = loader.getLicences();

		for (int j = 0; j < licenceList.size(); j++)
		{
			model.addElement(licenceList.get(j));
		}

		list.setModel(model);

		if (model.getSize() > 0)
		{
			list.setSelectedIndex(0);
			list.ensureIndexIsVisible(0);
		}
	}
}
