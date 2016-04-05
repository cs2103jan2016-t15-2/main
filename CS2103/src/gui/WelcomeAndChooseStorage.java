package gui;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import netscape.javascript.JSObject;
import main.App;

/**
 * @@author A0149527W
 */
public class WelcomeAndChooseStorage extends AppPage {
	
	public WelcomeAndChooseStorage() {
		super("/view/html/launch.html");
		JSObject win = (JSObject) webEngine
				.executeScript("window");
		win.setMember("app", new WelcomeBridge());
	}

	// JavaScript interface object
	public class WelcomeBridge {

		public void chooseFolder()  {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fileChooser.showOpenDialog(fileChooser);
			if (returnVal == JFileChooser.APPROVE_OPTION) {				
				App.filePath=fileChooser.getSelectedFile().getAbsolutePath()+"\\J.Listee.txt";
				// create file under the file folder chosen by user
				try {
					GUIController.createFile(App.filePath);
					//display starting page
					GUIController.initializeList(App.filePath);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(fileChooser, e.getMessage());
				}
			}
		}
	}

}
