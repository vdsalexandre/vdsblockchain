package com.vds.local.blockchain;

import java.util.ArrayList;
import java.util.Date;

import com.vds.local.utils.Digest;
import com.vds.local.utils.Utils;
import com.vds.local.wallet.Transaction;

public class Block {
	private String hash;
	private String previousHash;
	private String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<>();

	private long timeStamp;
	private int nonce;
	
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash();
	}
	
	public String calculateHash() {
		return Utils.getDigest(Digest.SHA512, previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);	
	}
	
	public void mineBlock(int difficulty) {
		merkleRoot = Utils.getMerkleRoot(transactions);
		String target = Utils.getDifficultyString(difficulty);
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		
		System.out.println("Block mined !! : " + hash);
	}
	
	// add transaction to this block
	public boolean addTransaction(Transaction transaction) {
		if (transaction == null) return false;
		
		if (previousHash != "0") {
			if ((transaction.processTransaction() != true))
				return false;
		}
		
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to block");
		return true;
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public String getPreviousHash() {
		return this.previousHash;
	}
	
	public String getMerkleRoot() {
		return this.merkleRoot;
	}
}
