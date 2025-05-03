package org.example.attacks;

import javafx.util.Pair;
import org.example.models.ResultWiener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AttackWiener {

    public ResultWiener calculate(BigInteger e, BigInteger n) {
        List<Pair<BigInteger, BigInteger>> resultsGoodFractions = new ArrayList<>();
        List<BigInteger> coeffs = getFraction(e, n);

        for (int i = 1; i < coeffs.size(); i++) {
            Pair<BigInteger, BigInteger> goodFrac = buildFraction(coeffs.subList(0, i+1));
            resultsGoodFractions.add(goodFrac);

            BigInteger ki = goodFrac.getKey();
            BigInteger di = goodFrac.getValue();
            BigInteger phi = e.multiply(di).subtract(BigInteger.ONE).divide(ki);
            BigInteger b = n.subtract(phi).add(BigInteger.ONE);
            BigInteger D = b.pow(2).subtract(n.multiply(BigInteger.valueOf(4)));
            if (D.signum() >= 0 && D.sqrt().multiply(D.sqrt()).equals(D)) {
                return new ResultWiener(di, phi, resultsGoodFractions);
            }
        }
        return null;
    }




    public static List<BigInteger> getFraction(BigInteger a, BigInteger b) {
        List<BigInteger> result = new ArrayList<>();
        while (b.compareTo(BigInteger.ZERO) > 0) {
            result.add(a.divide(b));
            BigInteger tmp = b;
            b = a.mod(b);
            a = tmp;
        }
        return result;
    }

    public static Pair<BigInteger, BigInteger> buildFraction(List<BigInteger> arrayCoeffs) {

        if (arrayCoeffs.isEmpty() || arrayCoeffs == null) {
            throw new IllegalArgumentException("arrayCoeffs is empty");
        }

        if (arrayCoeffs.size() < 2) {
            return new Pair<>(arrayCoeffs.getFirst(), BigInteger.ONE);
        }

        List<BigInteger> reversed = arrayCoeffs.reversed();
        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = reversed.getFirst();

        for (int i = 1; i < reversed.size()-1; i++) {
            BigInteger tmp = denominator;
            denominator = denominator.multiply(reversed.get(i)).add(numerator);
            numerator = tmp;
        }

        numerator = denominator.multiply(reversed.get(reversed.size()-1)).add(numerator);

        return new Pair<>(numerator, denominator);
    }
}
