package org.example.context;

import org.example.constants.PaddingMode;
import org.example.constants.CipherMode;
import org.example.constants.TypeAlgorithm;
import org.example.des.Des;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public class Context {

    private final CipherMode cipherMode;
    private final PaddingMode paddingMode;
    private final EncryptorDecryptorSymmetric encryptorDecryptorSymmetric;
    private static final int BUFFER_SIZE = 512;


    public Context(TypeAlgorithm typeAlgorithm, byte[] key, CipherMode cipherMode, PaddingMode paddingMode) {

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
            byte[] lastBlock = null;

            while ((bytesRead = in.read(buffer)) != -1) {
                if (lastBlock != null) {
                    out.write(encryptDecryptInner(lastBlock, true));
                }
                lastBlock = Arrays.copyOf(buffer, bytesRead);
            }

            if (lastBlock != null) {
                byte[] paddedBlock = addPadding(lastBlock);
                out.write(encryptDecryptInner(paddedBlock, true));
            } else {
                byte[] emptyPadded = addPadding(new byte[0]);
                out.write(encryptDecryptInner(emptyPadded, true));
            }
        }
    }

    public void decrypt(Path encryptedFilePath, Path decryptedFilePath)
            throws IOException, IllegalArgumentException {
        try (InputStream in = Files.newInputStream(encryptedFilePath);
             OutputStream out = Files.newOutputStream(decryptedFilePath)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            byte[] lastBlock = null;
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                if (lastBlock != null) {
                    out.write(lastBlock);
                }
                lastBlock = encryptDecryptInner(Arrays.copyOf(buffer, bytesRead), false);
            }

            if (lastBlock != null) {
                byte[] unpadded = removePadding(lastBlock);
                out.write(unpadded);
            }
        }
    }

//    public void encrypt(Path inputPath, Path encryptedPath) throws IOException {
//        try (InputStream in = Files.newInputStream(inputPath);
//            OutputStream out = Files.newOutputStream(encryptedPath)) {
//
//            byte[] buffer = new byte[BUFFER_SIZE];
//            int bytesRead;
//            while ((bytesRead = in.read(buffer)) != -1) {
//                byte[] paddedBuffer = Arrays.copyOf(buffer, bytesRead);
//                if (bytesRead < BUFFER_SIZE) {
//                    paddedBuffer = addPadding(Arrays.copyOf(buffer, bytesRead));
//                }
//                byte[] cipherBlock = encryptDecryptInner(paddedBuffer, true);
//                out.write(cipherBlock);
//            }
//        }
//    }
//
//    public void decrypt(Path encryptedFilePath, Path decryptedFilePath) throws IOException, IllegalArgumentException {
//        try (InputStream in = Files.newInputStream(encryptedFilePath);
//             OutputStream out = Files.newOutputStream(decryptedFilePath)) {
//
//            byte[] buffer = new byte[BUFFER_SIZE];
//            byte[] lastBlock = null;
//            int bytesRead;
//
//            while ((bytesRead = in.read(buffer)) != -1) {
//                byte[] decrypted = encryptDecryptInner(Arrays.copyOf(buffer, bytesRead), false);
//
//                if (lastBlock != null) {
//                    out.write(lastBlock);
//                }
//
//                lastBlock = decrypted;
//            }
//
//            if (lastBlock != null) {
//                byte[] finalData = removePadding(lastBlock);
//                out.write(finalData);
//            }
//        }
//    }


    //            byte[] buffer = new byte[BUFFER_SIZE];
//            int bytesRead;
//            while ((bytesRead = in.read(buffer)) != -1) {
//                byte[] cipherBlock = encryptDecryptInner(Arrays.copyOf(buffer, bytesRead), false);
//                if (bytesRead < BUFFER_SIZE) {
//                    cipherBlock = removePadding(cipherBlock);
//                }
//                byte[] removedPadding = removePadding(cipherBlock);
//                out.write(removedPadding);
//            }


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

    private byte[] addPadding(byte[] data) {
        switch (paddingMode) {
            case ZEROS -> { return addZerosPadding(data); }
            case PKCS7 -> { return addPkcs7Padding(data); }
            case ANSI_X923 -> { return addAnsiX923Padding(data); }
            case ISO_10126 -> { return addIso10126Padding(data); }
            default -> { return data; }
        }
    }

    private byte[] removePadding(byte[] data) throws IllegalArgumentException {
        switch (paddingMode) {
            case ZEROS -> {return removeZerosPadding(data); }
            case PKCS7 -> { return removePkcs7Padding(data); }
            case ISO_10126 -> { return removeIso10126Padding(data); }
            case ANSI_X923 -> { return removeAnsiX923Padding(data); }
            default -> { return data; }
        }
    }


    public static byte[] addIso10126Padding(byte[] data) {
        int paddingLength = 8 - (data.length % 8);
        byte[] paddedData = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        SecureRandom random = new SecureRandom();
        for (int i = data.length; i < paddedData.length-1; i++) {
            paddedData[i] = (byte) random.nextInt(256);
        }
        paddedData[paddedData.length-1] = (byte) paddingLength;
        return paddedData;
    }

    public static byte[] removeIso10126Padding(byte[] data) {
        return removePkcs7Padding(data);
    }


    public static byte[] addAnsiX923Padding(byte[] data) {
        int paddingLength = 8 - (data.length % 8);
        byte[] paddedData = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        paddedData[paddedData.length-1] = (byte) paddingLength;
        return paddedData;
    }

    public static byte[] removeAnsiX923Padding(byte[] data) {
        return removePkcs7Padding(data);
    }


    public static byte[] addPkcs7Padding(byte[] data) {
        int paddingLength = 8 - (data.length % 8);
        byte[] paddedArray = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedArray, 0, data.length);
        for (int i = data.length; i < paddedArray.length; i++) {
            paddedArray[i] = (byte) paddingLength;
        }
        return paddedArray;
    }

    public static byte[] removePkcs7Padding(byte[] data) {
        int paddingLength = data[data.length - 1];
        if (paddingLength > 8 || paddingLength < 0) {
            throw new IllegalArgumentException("Invalid padding length: " + paddingLength);
        }
        byte[] unpadded = new byte[data.length - paddingLength];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }

    public static byte[] addZerosPadding(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data is null or empty");
        }
        int paddingLength = (8 - (data.length % 8)) % 8;
        byte[] paddedArray = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedArray, 0, data.length);
        return paddedArray;
    }

    public static byte[] removeZerosPadding(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data is null or empty");
        }
        int length = data.length;
        for (int i = 1; i < 8; ++i) {
            if (data[data.length - i] == 0) {
                length--;
            } else {
                break;
            }
        }
        byte[] unpadded = new byte[length];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }

}
