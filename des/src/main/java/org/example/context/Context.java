package org.example.context;

import org.example.constants.PaddingMode;
import org.example.constants.CipherMode;
import org.example.interfaces.EncryptorDecryptorSymmetric;

import java.nio.file.Path;

public class Context {

    private byte[] key;
    private CipherMode cipherMode;
    private PaddingMode paddingMode;
    private byte[][] roundKeys;
    private EncryptorDecryptorSymmetric encryptorDecryptorSymmetric;


    public Context(EncryptorDecryptorSymmetric encryptorDecryptorSymmetric, byte[] key, CipherMode cipherMode, PaddingMode paddingMode) {
        this.key = key;
        this.cipherMode = cipherMode;
        this.paddingMode = paddingMode;
        this.encryptorDecryptorSymmetric = encryptorDecryptorSymmetric;
    }

    public Path encrypt(Path path) {

        Path encryptedFilePath = getOutputFilePath(path);


        return encryptedFilePath;
    }

    public Path decrypt(Path encryptedFilePath) {

        Path decryptedFilePath = getOutputFilePath(encryptedFilePath);
        return decryptedFilePath;
    }




    private Path getOutputFilePath(Path inputPath) {
        String fileName = inputPath.getFileName().toString();
        int firstDotIndex = fileName.indexOf('.'); // перед первым расширением!

        String baseName;
        String extensions;

        if (firstDotIndex > 0) {
            baseName = fileName.substring(0, firstDotIndex);
            extensions = fileName.substring(firstDotIndex);
        } else {
            baseName = fileName;
            extensions = "";
        }

        String outputFileName = baseName + "_out" + extensions;
        return inputPath.resolveSibling(outputFileName);
    }

}
