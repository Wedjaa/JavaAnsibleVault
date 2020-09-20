package net.martins.ansible.fx;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point
 * Starts the JavaFX application
 */
@SpringBootApplication
public class BootifulAnsibleVaultFxApplication {

	public static void main(String[] args) {
		// Launches JavaFX application
		Application.launch(AnsibleVaultFxApplication.class, args);
	}

}
 