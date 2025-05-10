package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.BitsInKeysOfDeal;
import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;
import org.example.deal.Deal;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.rijnadael.Rijndael;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class test {
    private static final byte[] DES_KEY = "12345678".getBytes();
    private static final byte[] DEAL_KEY = "16byteDEALkey123".getBytes();
    private static final byte[] IV_des = new byte[8];
    private static final byte[] IV_deal = new byte[16];

    private static final int DELTA = 53;

    public static void main(String[] args) {

        String[] testFiles = {
               // "test.txt",
                //"пз_3_менеджмент.pdf",
                "Архиватор на Go _ Урок #7_ Алгоритм Шеннона-Фано - теория.mp4",
                //"test.jpg"
        };

        for (String filename : testFiles) {
            try {
                Path inputFile = Paths.get("src/main/resources", filename);
                if (!Files.exists(inputFile)) {
                    log.info("Файл не найден: {}", inputFile);
                    continue;
                }

                log.info("\n=== Тестируем файл: " + filename + " ===");


                //testAlgorithm(TypeAlgorithm.DES, DES_KEY, null, inputFile);

                //EncryptorDecryptorSymmetric algo = new Deal(BitsInKeysOfDeal.BIT_128, DES_KEY, DEAL_KEY);
                EncryptorDecryptorSymmetric algo = new Rijndael(RijndaelKeyLength.KEY_128, RijndaelBlockLength.BLOCK_128, DEAL_KEY);
                testAlgorithm(algo, inputFile);

            } catch (Exception e) {
                log.error("Ошибка при обработке файла " + filename + e.getMessage());
            }
        }
    }

    private static void testAlgorithm(EncryptorDecryptorSymmetric algo, Path inputFile) throws Exception {
        log.info("\nАлгоритм: " + algo.getClass().getName());

        Context context = new Context(algo, CipherMode.RD, PaddingMode.ANSI_X923, IV_deal, DELTA);


        Path encryptedFile = Paths.get("src/main/resources", "_encrypted.bin");
        Path decryptedFile = Paths.get("src/main/resources", "_decrypted.mp4");

        context.encrypt(inputFile, encryptedFile);
        log.info("Зашифровано в: " + encryptedFile.getFileName());


        context.decrypt(encryptedFile, decryptedFile);
        log.info("Дешифровано в: " + decryptedFile.getFileName());

        long mismatch = Files.mismatch(inputFile, decryptedFile);
        log.info("Результат проверки: " +
                (mismatch == -1 ? "OK" : "Ошибка в позиции " + mismatch));

        if (decryptedFile.endsWith(".txt")) {
            log.info("\nОригинал:\n" + Files.readString(inputFile));
            log.info("\nДешифрованный:\n" + Files.readString(decryptedFile));
        }
    }
}
