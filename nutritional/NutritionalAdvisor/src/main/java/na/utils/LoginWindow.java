package na.utils;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import na.widgets.label.AdaptiveLabel;
import na.widgets.panel.AdaptivePanel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URL;

@SuppressWarnings("serial") 
public class LoginWindow extends AdaptivePanel {
	
	protected AdaptiveLabel label;
	private JButton image;
	
	public LoginWindow() {
		this.setBorder(BorderFactory.createEmptyBorder());
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{15, 0, 15, 0};
		gridBagLayout.rowHeights = new int[]{15, 0, 15, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
		setLayout(gridBagLayout);
		{
			AdaptivePanel panel = new AdaptivePanel();
			panel.setBorder(BorderFactory.createEmptyBorder());
			GridBagLayout gridBagLayout_1 = new GridBagLayout();
			gridBagLayout_1.columnWidths = new int[]{0, 0};
			gridBagLayout_1.rowHeights = new int[]{80, 0, 3, 0, 0};
			gridBagLayout_1.columnWeights = new double[]{1.0, 1.0E-4};
			gridBagLayout_1.rowWeights = new double[]{1.0, 1.0, 0.0, 0.0, 1.0E-4};
			panel.setLayout(gridBagLayout_1);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 0, 5, 5);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = 1;
			add(panel, gbc);
			{
				image = new JButton();
				GridBagConstraints gbc_1 = new GridBagConstraints();
				gbc_1.anchor = GridBagConstraints.SOUTH;
				gbc_1.insets = new Insets(0, 0, 5, 0);
				gbc_1.gridx = 0;
				gbc_1.gridy = 1;
				panel.add(image, gbc_1);
				URL picURL = this.getClass().getResource("userNotLogged.jpg");
				ImageIcon j = new ImageIcon(picURL);
				image.setIcon(j);
				image.setSize(j.getIconWidth(), j.getIconHeight());
			}
			{
				label = new AdaptiveLabel();
				label.setText("New label");
				GridBagConstraints gbc_1 = new GridBagConstraints();
				gbc_1.anchor = GridBagConstraints.NORTH;
				gbc_1.gridx = 0;
				gbc_1.gridy = 3;
				panel.add(label, gbc_1);
			}
		}
	}

	public void setLabel(String text) {
		this.label.setText(text);
	}
}