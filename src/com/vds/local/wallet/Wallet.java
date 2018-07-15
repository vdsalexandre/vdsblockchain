package com.vds.local.wallet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vds.local.main.VdsBlockChain;

public class Wallet {
	private PrivateKey privateKey;
	private PublicKey publicKey;
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
	
	public Wallet() {
		generateKeyPair();
	}
	
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public float getBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : VdsBlockChain.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			
			if (UTXO.isMine(publicKey)) { // if output belongs to me (coins)
				UTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			}
		}
		
		return total;
	}
	
	public Transaction sendFunds(PublicKey _recipient, float value) {
		if (getBalance() < value) //gather balance and check funds.
			return null;
		
		//create array list of inputs
		ArrayList<TransactionInput> inputs = new ArrayList<>();
    
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > value) break;
		}
		
		Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		
		for(TransactionInput input: inputs){
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
	
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
}
