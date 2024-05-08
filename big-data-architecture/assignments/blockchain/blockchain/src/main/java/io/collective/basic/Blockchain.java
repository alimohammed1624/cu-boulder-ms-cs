package io.collective.basic;

import java.util.ArrayList;

import java.security.NoSuchAlgorithmException;

public class Blockchain {
    
    private ArrayList<Block> chain;
    
    public Blockchain(){
        this.chain = new ArrayList<>();
    }
    
    public boolean isEmpty() {
        if(size() == 0){
            return true;
        }
        else{
            return false;
        }
    }
    
    public void add(Block block){
        chain.add(block);
    }
    
    public int size() {
        return chain.size();
    }
    
    public boolean isValid() throws NoSuchAlgorithmException {
        
        // todo - check an empty chain
        if(size() == 0){
            return true;
        }
        // todo - check a chain of one
        for(int i = size() - 1; i >= 0; i--){
            if(chain.get(i).getHash().equals(chain.get(i).calculatedHash()) == false){  
                return false;
            }
            if(isMined(chain.get(i)) == false){
                return false;
            }
            if(i > 0 && chain.get(i).getPreviousHash().equals(chain.get(i-1).getHash()) == false){
                return false;
            }
        }
        return true;
    }
    
    /// Supporting functions that you'll need.
    
    public static Block mine(Block block) throws NoSuchAlgorithmException {
        Block mined = new Block(block.getPreviousHash(), block.getTimestamp(), block.getNonce());
        
        while (!isMined(mined)) {
            mined = new Block(mined.getPreviousHash(), mined.getTimestamp(), mined.getNonce() + 1);
        }
        return mined;
    }
    
    public static boolean isMined(Block minedBlock) {
        return minedBlock.getHash().startsWith("00");
    }
}