package ru.sibsutis.cryptomethods.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@Getter
@Setter
public class EuclideanResult {
    private BigInteger gcd;
    private BigInteger x;
    private BigInteger y;
}
