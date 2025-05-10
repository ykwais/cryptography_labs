
import org.example.interfaces.impl.KeyExpansionImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorKeysTests {


    @Test
    void testCycleLeftShift() {
        KeyExpansionImpl keyExpansion = new KeyExpansionImpl();

        int value = 0x0FFFFFF;

        int shifted1 = keyExpansion.cycleLeftShiftFor28BitValue(value, 1);
        assertEquals(0x1FFFFFE, shifted1);

        int shifted2 = keyExpansion.cycleLeftShiftFor28BitValue(value, 2);
        assertEquals(0x3FFFFFC, shifted2);
    }

    @Test
    void testWithKnownTestVector() {

        byte[] key = {
                (byte) 0x13, (byte) 0x34, (byte) 0x57, (byte) 0x79,
                (byte) 0x9B, (byte) 0xBC, (byte) 0xDF, (byte) 0xF1
        };

        KeyExpansionImpl keyExpansion = new KeyExpansionImpl();
        byte[][] roundKeys = keyExpansion.generateRoundKeys(key);

        assertEquals(16, roundKeys.length);

        for (byte[] roundKey : roundKeys) {
            assertEquals(6, roundKey.length);
        }


        byte[] expectedRound1Key = {
                (byte) 0x1B, (byte) 0x02, (byte) 0xEF,
                (byte) 0xFC, (byte) 0x70, (byte) 0x72
        };
        assertArrayEquals(expectedRound1Key, roundKeys[0]);


        byte[] expectedRound16Key = {
                (byte) 0xCB, (byte) 0x3D, (byte) 0x8B,
                (byte) 0x0E, (byte) 0x17, (byte) 0xF5
        };
        assertArrayEquals(expectedRound16Key, roundKeys[15]);
    }

    @Test
    void testEdgeCases() { // крайние случаи чекнем
        KeyExpansionImpl keyExpansion = new KeyExpansionImpl();

        byte[] zeroKey = new byte[8];
        byte[][] zeroRoundKeys = keyExpansion.generateRoundKeys(zeroKey);

        byte[] onesKey = new byte[8];
        Arrays.fill(onesKey, (byte) 0xFF);
        byte[][] onesRoundKeys = keyExpansion.generateRoundKeys(onesKey);


        for (int i = 0; i < 16; i++) {
            assertEquals(6, zeroRoundKeys[i].length);
            assertEquals(6, onesRoundKeys[i].length);
        }

        assertFalse(Arrays.equals(zeroRoundKeys[0], onesRoundKeys[0]));
    }


}
