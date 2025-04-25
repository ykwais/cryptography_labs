package org.example.fiestel;

import lombok.extern.slf4j.Slf4j;
import org.example.utils.Pair;

import static org.example.utils.ToView.intToHex;

@Slf4j
public class FiestelNet {

    public Pair<Integer, Integer> rounds16OfFiestelNet(int l0, int r0, byte[][] roundKeys) {

        FiestelFunction fiestelFunction = new FiestelFunction();

        int l = l0;
        int r = r0;

        for (int i = 0; i < 16; ++i) {

            log.info("ROUND: {}", i+1);

            int lNext = r;

            int rNext = l ^ fiestelFunction.doFunction(r, roundKeys[i]);

            l = lNext;
            r = rNext;

            log.info("L next: {}", intToHex(l));
            log.info("R next: {}", intToHex(r));

        }

        log.info("L last: {}", intToHex(l));
        log.info("R last: {}", intToHex(r));

        return new Pair<>(l,r);
    }

}
