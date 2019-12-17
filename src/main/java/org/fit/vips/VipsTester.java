package org.fit.vips;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class VipsTester{

	public static void main(String[] args)
	{
		new VipsTester();
	}

	VipsTester(){

		JFrame f = new JFrame("Segmenter");
		f.getContentPane().setBackground(new Color(55,71,79));

		JLabel label = new JLabel("Input a valid URL");
		label.setBounds(100, 70, 200, 30);
		label.setFont(new Font("Century Gothic", Font.PLAIN, 14));
		label.setForeground(Color.white);
		f.add(label);

		JTextField textField = new JTextField();
		textField.setBounds(100,100,200,30);
		f.add(textField);

		JButton button = new JButton("GO");
		button.setBounds(150,155,100,30);
		button.setBackground(new Color(77,182,172));
		button.setFont(new Font("Century Gothic", Font.BOLD, 14));
		f.add(button);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent me) {
				button.setBackground(new Color(0, 158, 143));
			}

			@Override
			public void mouseExited(MouseEvent me){
				button.setBackground((new Color(77,182,172)));
			}
		});

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String url = textField.getText();

				try
				{
					JOptionPane.showOptionDialog(null, "Loading...","Loading Window", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);

					Vips vips = new Vips();
					vips.enableGraphicsOutput(true);
					vips.enableOutputToFolder(true);
					vips.setPredefinedDoC(8);
					vips.startSegmentation(url);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		f.setSize(400, 300);
		f.setLocationRelativeTo(null);
		f.setLayout(null);
		f.setResizable(false);
		f.setVisible(true);
	}
}
