package org.example.models;

import javafx.util.Pair;

import java.math.BigInteger;
import java.util.List;

public record ResultWiener(BigInteger d, BigInteger phi, List<Pair<BigInteger, BigInteger>> coeffs) {}
