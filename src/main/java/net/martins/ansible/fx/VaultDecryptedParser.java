package net.martins.ansible.fx;

import net.wedjaa.ansible.vault.crypto.VaultHandler;
import net.wedjaa.ansible.vault.crypto.data.Util;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parses a clear text ansible vault variable
 * and encrypts it
 */
public class VaultDecryptedParser {

    private static final String COLLON = ":";
    private String variableName;
    private String variableValue;


    /**
     * Parses the context of textArea (encrypted or decrypted vault variable) and
     * stores the fields that is finds
     * @param textAreaContent
     */
    public void parseEncryptedText(String textAreaContent) throws CharacterCodingException {

        final List<String> lines = Arrays.stream(textAreaContent.split(Util.LINE_BREAK))
                .filter(l -> StringUtils.hasText(l))
                .map(l -> l.trim())
                .collect(Collectors.toList());

        String firstLine = lines.get(0);
        if(firstLine.contains(COLLON)) {
            variableName = firstLine.substring(0, firstLine.indexOf(COLLON));
            lines.set(0, firstLine.substring(firstLine.indexOf(COLLON) + 1));
        }
        variableValue = String.join("", lines).trim();
    }

    public String getEncryptedVault(String password) throws IOException {
        StringBuilder variableBuilder = new StringBuilder();

        if(variableName != null) {
            variableBuilder.append(variableName).append(COLLON);
            variableBuilder.append(" ").append(VaultEncryptedParser.VARIABLE_MARKER);
            variableBuilder.append(Util.LINE_BREAK);
        }

        final byte[] clearText = variableValue.getBytes(StandardCharsets.UTF_8);
        final byte [] encryptedText = VaultHandler.encrypt(clearText, password);
        variableBuilder.append(new String(encryptedText, StandardCharsets.UTF_8));
        variableBuilder.append(Util.LINE_BREAK);
        return variableBuilder.toString();
    }
}
