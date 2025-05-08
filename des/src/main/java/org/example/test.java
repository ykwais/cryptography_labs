package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;

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
                "пз_3_менеджмент.pdf",
                //"Архиватор на Go _ Урок #7_ Алгоритм Шеннона-Фано - теория.mp4",
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


                testAlgorithm(TypeAlgorithm.DEAL_128, DES_KEY, DEAL_KEY, inputFile);

            } catch (Exception e) {
                log.error("Ошибка при обработке файла " + filename + e.getMessage());
            }
        }
    }

    private static void testAlgorithm(TypeAlgorithm algorithm,
                                      byte[] key,
                                      byte[] dealKey,
                                      Path inputFile) throws Exception {
        log.info("\nАлгоритм: " + algorithm);

        Context context;

        if (algorithm == TypeAlgorithm.DES) {
            context = new Context(
                    algorithm,
                    key,
                    CipherMode.CBC,
                    PaddingMode.PKCS7,
                    IV_des,
                    DELTA

            );
        } else {
            context = new Context(
                    algorithm,
                    key,
                    CipherMode.CBC,
                    PaddingMode.PKCS7,
                    IV_deal,
                    DELTA,
                    dealKey
            );
        }


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
