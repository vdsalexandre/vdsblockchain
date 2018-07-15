package com.vds.local.main;

import java.security.Security;
import java.util.HashMap;

import com.vds.local.blockchain.Block;
import com.vds.local.blockchain.BlockChain;
import com.vds.local.wallet.Transaction;
import com.vds.local.wallet.TransactionOutput;
import com.vds.local.wallet.Wallet;

public class VdsBlockChain {
	
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
	public static Wallet walletA;
	public static Wallet walletB;
	public static float minimumTransaction = 0.1f;
	public static Transaction genesisTransaction; // transaction de départ pour alimenter le walletA
	public static Block genesis; // block de départ

	public static void main(String[] args) {
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinbase = new Wallet();
		
		// create genesis transaction which sends 100 vdscoins to walletA
		genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		genesisTransaction.generateSignature(coinbase.getPrivateKey());
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		System.out.println("Creating and Mining Genesis block ...");
		genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		BlockChain.addToChain(genesis);
		
		// testing
		Block b1 = new Block(genesis.getHash());
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is attempting to send funds (40) to walletB ...");
		b1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
		BlockChain.addToChain(b1);		
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletB's balance is: " + walletB.getBalance());
		
		Block b2 = new Block(b1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		b2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
		BlockChain.addToChain(b2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block b3 = new Block(b2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		b3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		System.out.println("Chain valid: " + BlockChain.isValidChain());
		
	}
}
