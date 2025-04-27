package org.example.context;

import org.example.constants.PaddingMode;
import org.example.constants.CipherMode;
import org.example.constants.TypeAlgorithm;
import org.example.des.Des;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public class Context {

    private byte[] key;
    private CipherMode cipherMode;
    private PaddingMode paddingMode;
    private byte[][] roundKeys;
    private EncryptorDecryptorSymmetric encryptorDecryptorSymmetric;
    private final int BUFFER_SIZE = 512;


    public Context(TypeAlgorithm typeAlgorithm, byte[] key, CipherMode cipherMode, PaddingMode paddingMode) {
        this.key = key;
        this.cipherMode = cipherMode;
        this.paddingMode = paddingMode;
        if (Objects.requireNonNull(typeAlgorithm) == TypeAlgorithm.DES) {
            encryptorDecryptorSymmetric = new Des(key, new KeyExpansionImpl(), new FiestelFunction());
        } else {
            throw new UnsupportedOperationException("Unsupported type algorithm: " + typeAlgorithm);
        }
    }

    public void encrypt(Path inputPath, Path encryptedPath) throws IOException {
        try (InputStream in = Files.newInputStream(inputPath);
            OutputStream out = Files.newOutputStream(encryptedPath)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] paddedBuffer = addPkcs7Padding(Arrays.copyOf(buffer, bytesRead));
                byte[] cipherBlock = encryptDecryptInner(paddedBuffer, true);
                out.write(cipherBlock);
            }
        }
    }

    public void decrypt(Path encryptedFilePath, Path decryptedFilePath) throws IOException {
        try (InputStream in = Files.newInputStream(encryptedFilePath);
             OutputStream out = Files.newOutputStream(decryptedFilePath)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] cipherBlock = encryptDecryptInner(Arrays.copyOf(buffer, bytesRead), false);
                byte[] removedPadding = removePkcs7Padding(cipherBlock);
                out.write(removedPadding);
            }
        }
    }


    public byte[] encryptDecryptInner(byte[] data, boolean isEncrypt) {
        byte[] result = new byte[data.length];
        int amountBlockBy8Bytes = data.length / 8;

        if (cipherMode == CipherMode.ECB) {
            IntStream.range(0, amountBlockBy8Bytes).parallel().forEach(i -> {
                int offset = i * 8;
                byte[] block = Arrays.copyOfRange(data, offset, offset + 8);
                byte[] preResult = isEncrypt ? encryptorDecryptorSymmetric.encrypt(block) : encryptorDecryptorSymmetric.decrypt(block);
                System.arraycopy(preResult, 0, result, offset, 8);
            });
        }

        return result;
    }


    public static byte[] addPkcs7Padding(byte[] data) {
        int padLength = 8 - (data.length % 8);
        byte[] padded = new byte[data.length + padLength];
        System.arraycopy(data, 0, padded, 0, data.length);
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padLength;
        }
        return padded;
    }

    public static byte[] removePkcs7Padding(byte[] data) {
        int padLength = data[data.length - 1];
        byte[] unpadded = new byte[data.length - padLength];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
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
