package io.collective.basic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    private String previousHash;
    private long timestamp;
    private int nonce;
    private String hash;

    public Block(String previousHash, long timestamp, int nonce) throws NoSuchAlgorithmException {
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.hash = this.calculatedHash();
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getNonce() {
        return nonce;
    }

    public String getHash() {
        return hash;
    }

    public String calculatedHash() throws NoSuchAlgorithmException {
        String input = previousHash + timestamp + nonce;
        return calculateHash(input);
    }

    /// Supporting functions that you'll need.

    static String calculateHash(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(string.getBytes());
        return String.format("%064x", new BigInteger(1, digest.digest()));
    }
}