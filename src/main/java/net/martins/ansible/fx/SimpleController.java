package net.martins.ansible.fx;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;

@Component
public class SimpleController {

    private final HostServices hostServices;

    @FXML
    public TextArea textArea;

    @FXML
    public TextField passwordTextField;

    @FXML
    public Label label;

    @FXML
    public Button encryptButton;

    @FXML
    public Button decryptButton;

    public SimpleController(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void decryptTextArea() {
        VaultEncryptedParser vaultEncryptedParser = new VaultEncryptedParser();
        try {
            vaultEncryptedParser.parseEncryptedText(textArea.getText());
            textArea.setText(vaultEncryptedParser.getDecryptedVault(passwordTextField.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encryptTextArea() {
        VaultDecryptedParser vaultDecryptedParser = new VaultDecryptedParser();
        try {
            vaultDecryptedParser.parseEncryptedText(textArea.getText());
            textArea.setText(vaultDecryptedParser.getEncryptedVault(passwordTextField.getText()));
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        this.encryptButton.setOnAction(actionEvent -> this.encryptTextArea());
        this.decryptButton.setOnAction(actionEvent -> this.decryptTextArea());
    }
}
