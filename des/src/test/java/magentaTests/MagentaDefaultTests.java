package magentaTests;

import org.example.magenta.supply.GeneratorSBlock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MagentaDefaultTests {
    @Test
    void testSBoxInversionCorrectness() {

        GeneratorSBlock generator = new GeneratorSBlock((byte) 0x65);
        byte[] sBox = generator.getSBlock();

        System.out.println("S-Box in HEX format:");
        for (int i = 0; i < 32; i++) {

            for (int j = 0; j < 8; j++) {
                System.out.printf(Integer.toUnsignedLong(sBox[i * 8 + j] & 0xFF) + " ");
            }
            System.out.println();
        }

    }
}
