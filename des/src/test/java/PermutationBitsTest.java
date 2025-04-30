import lombok.extern.slf4j.Slf4j;
import org.example.utils.PermutationBits;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class PermutationBitsTest {

    @Test
    void testSimpleIdentityPermutation() {
        byte[] input = {(byte) 0b11001010};
        log.info("BIN: {}", Integer.toBinaryString(input[0] & 0xFF));
        int[] pBlock = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

        byte[] result = PermutationBits.permute(input, pBlock, true, false);

        assertEquals(1, result.length);
        log.info("BIN: {}", Integer.toBinaryString(input[0]));
        log.info("BIN: {}", Integer.toBinaryString(result[0]));
        assertEquals(input[0], result[0]);
    }

    @Test
    void testReversePermutation() {
        byte[] input = {(byte) 0b11001010};
        int[] pBlock = new int[]{7, 6, 5, 4, 3, 2, 1, 0};

        byte[] result = PermutationBits.permute(input, pBlock, true, false);

        assertEquals((byte) 0b01010011, result[0]);
    }

    @Test
    void testTwoByteCrossingPermutation() {
        byte[] input = {(byte) 0b00001111, (byte) 0b11110000};
        int[] pBlock = new int[]{
                0, 1, 2, 3, 4, 5, 6, 7,
                8, 9, 10, 11, 12, 13, 14, 15
        };

        byte[] result = PermutationBits.permute(input, pBlock, true, false);

        assertArrayEquals(input, result);
    }

    @Test
    void testStartIndexFromOne() {
        byte[] input = {(byte) 0b10000000};
        int[] pBlock = new int[]{8};

        byte[] result = PermutationBits.permute(input, pBlock, false, true);

        assertEquals((byte) 0b10000000, result[0]);
    }

    @Test
    void testStartIndexFromOne2() {
        byte[] input = {(byte) 0b10000010};
        int[] pBlock = new int[]{2};

        byte[] result = PermutationBits.permute(input, pBlock, false, true);

        assertEquals((byte) 0b10000000, result[0]);
    }

    @Test
    void testZeroOutputBits() {
        byte[] input = {(byte) 0xFF};
        int[] pBlock = new int[0];

        byte[] result = PermutationBits.permute(input, pBlock, true, false);

        assertEquals(0, result.length);
    }




    // ================================================
    // Тесты для направления isLeftToRight=true
    // ================================================
    @Test
    void testPermute_EmptyInput_FromBiggestToSmallest() {
        byte[] result = PermutationBits.permute(
                new byte[]{},
                new int[]{},
                true,
                false
        );
        assertArrayEquals(new byte[]{}, result);
    }

    @Test
    void testPermute_LeftToRight_Start0_FirstNibble() {
        byte[] result = PermutationBits.permute(
                new byte[]{(byte) 0xF0},
                new int[]{0, 1, 2, 3},
                true,
                false
        );
        assertArrayEquals(new byte[]{(byte) 0xF0}, result);
    }

    @Test
    void testPermute_LeftToRight_Start0_LastNibble() {
        byte[] result = PermutationBits.permute(
                new byte[]{(byte) 0x0F},
                new int[]{0, 1, 2, 3},
                true,
                false
        );
        assertArrayEquals(new byte[]{0x00}, result);
    }

    @Test
    void testPermute_LeftToRight_Start0_AlternatingBits() {
        byte[] result = PermutationBits.permute(
                new byte[]{(byte) 0x0F},
                new int[]{0, 7, 0, 7, 0, 7, 0, 7},
                true,
                false
        );
        assertArrayEquals(new byte[]{85}, result); // 85 = 0b01010101
    }

    // ================================================
    // Тесты для направления  isLeftToRight=false
    // ================================================
    @Test
    void testPermute_EmptyInput_FromSmallestToBiggest() {
        byte[] result = PermutationBits.permute(
                new byte[]{},
                new int[]{},
                false,
                false
        );
        assertArrayEquals(new byte[]{}, result);
    }

    @Test
    void testPermute_RightToLeft_Start0_FirstNibble() {
        byte[] result = PermutationBits.permute(
                new byte[]{(byte) 0xF0},
                new int[]{0, 1, 2, 3},
                false,
                false
        );
        assertArrayEquals(new byte[]{0x00}, result);
    }

    @Test
    void testPermute_RightToLeft_Start0_LastNibble() {
        byte[] result = PermutationBits.permute(
                new byte[]{(byte) 0x0F},
                new int[]{7, 7, 7, 7, 0, 1, 2, 3},
                false,
                false
        );
        assertArrayEquals(new byte[]{(byte) 0x0F}, result);
    }

    // ================================================
    // Тесты для начального индекса 1
    // ================================================
    @Test
    void testPermute_EmptyInput_StartIndex1() {
        byte[] result = PermutationBits.permute(
                new byte[]{},
                new int[]{},
                false,
                true
        );
        assertArrayEquals(new byte[]{}, result);
    }

    @Test
    void testPermute_RightToLeft_Start1_FirstNibble() {
        byte[] result = PermutationBits.permute(
                new byte[]{(byte) 0xF0},
                new int[]{1, 2, 3, 4},
                false,
                true
        );
        assertArrayEquals(new byte[]{0x00}, result);
    }

    @Test
    void testPermute_RightToLeft_Start1_LastNibble() {
        byte[] result = PermutationBits.permute(
                new byte[]{(byte) 0x0F},
                new int[]{1, 2, 3, 4},
                false,
                true
        );
        assertArrayEquals(new byte[]{(byte) 0xF0}, result);
    }

    @Test
    void testPermute_3Bytes_LeftToRight_ComplexPattern() {
        byte[] input = {
                (byte) 0b10101010,
                (byte) 0b11001100,
                (byte) 0b11110000
        };


        int[] pBlock = {0, 3, 8, 10, 15, 16, 20, 23};

        byte[] result = PermutationBits.permute(input, pBlock, true, false);

        assertArrayEquals(new byte[]{(byte) 0xA4}, result);
    }


    @Test
    void testPermute_3Bytes_RightToLeft_ComplexPattern() {
        byte[] input = {
                (byte) 0b10101010,
                (byte) 0b11001100,
                (byte) 0b11110000
        };

        int[] pBlock = {0, 7, 8, 15, 16, 23};

        byte[] result = PermutationBits.permute(input, pBlock, false, false);

        assertArrayEquals(new byte[]{(byte) 0x54}, result);
    }

    @Test
    void testPermute_3Bytes_RightToLeft_StartIndex1() {
        byte[] input = {
                (byte) 0b10101010,
                (byte) 0b11001100,
                (byte) 0b11110000
        };

        int[] pBlock = {1, 8, 9, 16, 17, 24};

        byte[] result = PermutationBits.permute(input, pBlock, false, true);

        assertArrayEquals(new byte[]{(byte) 0x54}, result);
    }
}



