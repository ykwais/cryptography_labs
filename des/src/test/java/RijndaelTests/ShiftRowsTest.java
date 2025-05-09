package RijndaelTests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ShiftRowsTest {


    @Test
    void testShiftRows_AllZeros() {

        byte[] input = new byte[16];
        byte[] expected = new byte[16];

        byte[] actual = input.clone();
        shiftRows(actual);

        assertArrayEquals(expected, actual, "Массив из нулей не должен изменяться");
    }

    @Test
    void testShiftRows_ColumnMajorOrder() {

        byte[] state = {
                0x00, 0x01, 0x02, 0x03,
                0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B,
                0x0C, 0x0D, 0x0E, 0x0F
        };


        byte[] expected = {
                0x00, 0x05, 0x0A, 0x0F,
                0x04, 0x09, 0x0E, 0x03,
                0x08, 0x0D, 0x02, 0x07,
                0x0C, 0x01, 0x06, 0x0B
        };

        shiftRows(state);
        assertArrayEquals(expected, state);
    }

    @Test
    void testShiftRows_Row1Shift() {

        byte[] state = {
                0x00, 0x01, 0x00, 0x00,
                0x00, 0x02, 0x00, 0x00,
                0x00, 0x03, 0x00, 0x00,
                0x00, 0x04, 0x00, 0x00
        };


        byte[] expected = {
                0x00, 0x02, 0x00, 0x00,
                0x00, 0x03, 0x00, 0x00,
                0x00, 0x04, 0x00, 0x00,
                0x00, 0x01, 0x00, 0x00
        };

        shiftRows(state);
        assertArrayEquals(expected, state);
    }


    @Test
    void testShiftRows_Row3Shift() {
        byte[] state = {
                0x00, 0x00, 0x00, 0x03,
                0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x05,
                0x00, 0x00, 0x00, 0x06
        };

        byte[] expected = {
                0x00, 0x00, 0x00, 0x06,
                0x00, 0x00, 0x00, 0x03,
                0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x05
        };

        shiftRows(state);
        assertArrayEquals(expected, state);
    }


    @Test
    void testShiftRows_AllRows() {
        byte[] state = {
                0x00, 0x10, 0x20, 0x30,
                0x01, 0x11, 0x21, 0x31,
                0x02, 0x12, 0x22, 0x32,
                0x03, 0x13, 0x23, 0x33
        };

        byte[] expected = {
                0x00, 0x11, 0x22, 0x33,
                0x01, 0x12, 0x23, 0x30,
                0x02, 0x13, 0x20, 0x31,
                0x03, 0x10, 0x21, 0x32
        };

        shiftRows(state);
        assertArrayEquals(expected, state);
    }


    private void shiftRows(byte[] state) {
        byte[] temp = state.clone();
        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int newPos = (col - row + 4) % 4;
                state[row + 4 * newPos] = temp[row + 4 * col];
            }
        }
    }

    private void shiftRows6(byte[] state) {
        byte[] temp = state.clone();
        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < 6; col++) {
                int newPos = (col - row + 6) % 6;
                state[row + 4 * newPos] = temp[row + 4 * col];
            }
        }
    }

    private void invShiftRows6(byte[] state) {
        byte[] temp = Arrays.copyOf(state, state.length);


        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < 6; col++) {
                int newPos = (col + row) % 6;
                state[row + 4 * newPos] = temp[row + 4 * col];
            }
        }
    }

    @Test
    void testShiftRowsNb6() {

        byte[] state = {
                0,  1,  2,  3,
                4,  5,  6,  7,
                8,  9, 10, 11,
                12, 13, 14, 15,
                16, 17, 18, 19,
                20, 21, 22, 23
        };

        byte[] st2 = state.clone();


        byte[] expected = {
                0,  5, 10, 15,
                4,  9, 14, 19,
                8, 13, 18, 23,
                12, 17, 22,  3,
                16, 21,  2,  7,
                20,  1,  6, 11
        };

        shiftRows6(state);

        assertArrayEquals(expected, state);

        invShiftRows6(state);

        assertArrayEquals(st2, state);
    }


    private void shiftRows8(byte[] state) {
        byte[] temp = state.clone();
        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < 8; col++) {
                int newPos = (col - row + 8) % 8;
                state[row + 4 * newPos] = temp[row + 4 * col];
            }
        }
    }

    @Test
    void testShiftRowsNb8() {

        byte[] state = {
                0,  1,  2,  3,   4,  5,  6,  7,
                8,  9, 10, 11,  12, 13, 14, 15,
                16, 17, 18, 19,  20, 21, 22, 23,
                24, 25, 26, 27,  28, 29, 30, 31
        };


        byte[] expected = {
                0,  5, 10, 15, 4, 9, 14, 19,
                8, 13, 18, 23, 12,  17, 22, 27,
                16, 21, 26, 31,  20,  25, 30, 3,
                24, 29,  2,  7, 28, 1, 6, 11
        };

        shiftRows8(state);

        assertArrayEquals(expected, state);
    }
}
