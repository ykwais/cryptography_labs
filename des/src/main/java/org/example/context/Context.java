package org.example.context;

import lombok.extern.slf4j.Slf4j;
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
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;


@Slf4j
public class Context {

    private final CipherMode cipherMode;
    private final PaddingMode paddingMode;
    private final EncryptorDecryptorSymmetric encryptorDecryptorSymmetric;
    private static final int BUFFER_SIZE = 4096;
    private final byte[] initialVector;
    private Integer deltaForRD = null;


    public Context(TypeAlgorithm typeAlgorithm, byte[] key, CipherMode cipherMode, PaddingMode paddingMode, byte[] initializationVector, Object... extras) {
        this.initialVector = initializationVector;
        this.cipherMode = cipherMode;
        this.paddingMode = paddingMode;
        if (Objects.requireNonNull(typeAlgorithm) == TypeAlgorithm.DES) {
            encryptorDecryptorSymmetric = new Des(key, new KeyExpansionImpl(), new FiestelFunction());
        } else {
            throw new UnsupportedOperationException("Unsupported type algorithm: " + typeAlgorithm);
        }
        if (extras != null && extras.length > 0) {
            if (!(extras[0] instanceof Integer)) {
                throw new IllegalArgumentException("First extra parameter must be Integer (for deltaForRD)");
            }

            this.deltaForRD = (Integer) extras[0];

            if (this.deltaForRD < 0) {
                throw new IllegalArgumentException("Delta for RD mode cannot be negative");
            }
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

    public byte[] encryptDecryptInner(byte[] data, boolean isEncrypt) {
        byte[] result = new byte[data.length];
        int amountBlockBy8Bytes = data.length / 8;

        switch(cipherMode) {
            case ECB:
                IntStream.range(0, amountBlockBy8Bytes).parallel().forEach(i -> {
                    int offset = i * 8;
                    byte[] block = Arrays.copyOfRange(data, offset, offset + 8);
                    byte[] preResult = isEncrypt ? encryptorDecryptorSymmetric.encrypt(block) : encryptorDecryptorSymmetric.decrypt(block);
                    System.arraycopy(preResult, 0, result, offset, 8);
                });
                break;
            case CBC:
                if (isEncrypt) {
                    byte[] blockMessage = Arrays.copyOfRange(data,0, 8 );
                    blockMessage = xorByteArrays(blockMessage, initialVector);
                    byte[] resultPrev = encryptorDecryptorSymmetric.encrypt(blockMessage);
                    System.arraycopy(resultPrev, 0, result, 0, 8);
                    for(int i = 8; i < data.length; i += 8) {
                        blockMessage = Arrays.copyOfRange(data, i, i + 8);
                        blockMessage = xorByteArrays(blockMessage, resultPrev);
                        resultPrev = encryptorDecryptorSymmetric.encrypt(blockMessage);
                        System.arraycopy(resultPrev, 0, result, i, 8);
                    }
                } else {
                    byte[] decodedBlocks = new byte[data.length];
                    IntStream.range(0, amountBlockBy8Bytes).parallel().forEach(i -> {
                        int offset = i * 8;
                        byte[] block = Arrays.copyOfRange(data, offset, offset + 8);
                        byte[] preResult = encryptorDecryptorSymmetric.decrypt(block);
                        System.arraycopy(preResult, 0, decodedBlocks, offset, 8);
                    });

                    byte[] arrayCipherTexts = new byte[data.length + 8];
                    System.arraycopy(initialVector, 0, arrayCipherTexts, 0, initialVector.length);
                    System.arraycopy(data, 0, arrayCipherTexts, initialVector.length, data.length);

                    IntStream.range(0, amountBlockBy8Bytes).parallel().forEach(i -> {
                        int offset = i * 8;
                        byte[] firstBlock = Arrays.copyOfRange(arrayCipherTexts, offset, offset + 8);
                        byte[] secondBlock = Arrays.copyOfRange(decodedBlocks, offset, offset + 8);
                        byte[] message = xorByteArrays(firstBlock, secondBlock);
                        System.arraycopy(message, 0, result, offset, 8);
                    });
                }
                break;
            case OFB:
                byte[] previousEncryptedBlockOFB = encryptorDecryptorSymmetric.encrypt(initialVector);
                for(int i = 0; i < amountBlockBy8Bytes; i++) {
                    int offset = i * 8;
                    byte[] block = Arrays.copyOfRange(data, offset, offset + 8);
                    byte[] preResult = xorByteArrays(previousEncryptedBlockOFB, block);
                    System.arraycopy(preResult, 0, result, offset, 8);
                    previousEncryptedBlockOFB = encryptorDecryptorSymmetric.encrypt(previousEncryptedBlockOFB);
                }
                break;
            case CFB:
                if (isEncrypt) {
                    byte[] previousEncryptedBlockCFB = initialVector;
                    for(int i = 0; i < amountBlockBy8Bytes; i++) {
                        int offset = i * 8;
                        byte[] block = Arrays.copyOfRange(data, offset, offset + 8);
                        byte[] preResult = xorByteArrays(encryptorDecryptorSymmetric.encrypt(previousEncryptedBlockCFB), block);
                        System.arraycopy(preResult, 0, result, offset, 8);
                        previousEncryptedBlockCFB = preResult;
                    }
                } else {
                    byte[] cipherTextsArray = new byte[data.length + initialVector.length];
                    System.arraycopy(initialVector, 0, cipherTextsArray, 0, initialVector.length);
                    System.arraycopy(data, 0, cipherTextsArray, initialVector.length, data.length);

                    IntStream.range(0, amountBlockBy8Bytes).parallel().forEach(i -> {
                        int offset = i * 8;
                        byte[] blockFirst = Arrays.copyOfRange(cipherTextsArray, offset, offset + 8);
                        byte[] blockSecond = Arrays.copyOfRange(cipherTextsArray, offset+8, offset + 16);
                        byte[] message = xorByteArrays(encryptorDecryptorSymmetric.encrypt(blockFirst), blockSecond);
                        System.arraycopy(message, 0, result, offset, 8);
                    });
                }
                break;
            case PCBC:
                byte[] previousEncryptedBlockPCBC = initialVector;
                for(int i = 0; i < amountBlockBy8Bytes; i++) {
                    int offset = i * 8;
                    byte[] block = Arrays.copyOfRange(data, offset, offset + 8);
                    byte[] xored = xorByteArrays(previousEncryptedBlockPCBC, isEncrypt ? block : encryptorDecryptorSymmetric.decrypt(block)) ;
                    byte[] preResult = isEncrypt ? encryptorDecryptorSymmetric.encrypt(xored) : xored;
                    System.arraycopy(preResult, 0, result, offset, 8);
                    previousEncryptedBlockPCBC = xorByteArrays(preResult, block);
                }
                break;
            case CTR:
            case RD:
//                byte[] tmp = new byte[] {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF5};

                byte[] iv = new byte[initialVector.length];
                System.arraycopy(initialVector, 0, iv, 0, initialVector.length);
                BigInteger initialCounter = new BigInteger(iv);


//                log.info("my bigint: {}", initialCounter);
//                initialCounter = initialCounter.add(BigInteger.ONE);
//                log.info("my bigint 2: {}", initialCounter);
//                byte[] bytesOfBigInteger = initialCounter.toByteArray();
//                log.info("array of BigInteger: {}", bytesToHex(bytesOfBigInteger));
//                bytesOfBigInteger = Arrays.copyOf(bytesOfBigInteger, 8);
//                log.info("array of BigInteger: {}", bytesToHex(bytesOfBigInteger));


                if (cipherMode == CipherMode.RD && deltaForRD == null) {
                    throw new IllegalArgumentException("deltaForRD is null!!! need to add for constructor of Context");
                }

                int delta = cipherMode == CipherMode.CTR ? 1 : deltaForRD;
                IntStream.range(0, amountBlockBy8Bytes).parallel().forEach(i -> {
                    int offset = i * 8;
                    byte[] block = Arrays.copyOfRange(data, offset, offset + 8);
                    BigInteger currentCounter = initialCounter;
                    BigInteger iNumber = BigInteger.valueOf(i);
                    BigInteger deltaBigInteger = BigInteger.valueOf(delta);
                    currentCounter = currentCounter.add(deltaBigInteger.multiply(iNumber));
                    byte[] bytesOfCurrentCounter = currentCounter.toByteArray();
                    bytesOfCurrentCounter = Arrays.copyOf(bytesOfCurrentCounter, 8);
                    byte[] xored = xorByteArrays(bytesOfCurrentCounter, block);
                    System.arraycopy(xored, 0, result, offset, 8);
                });
                break;
            default:
                throw new UnsupportedOperationException("Unsupported cipher mode: " + cipherMode);
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

    private byte[] xorByteArrays(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays have different lengths");
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; ++i) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

}
