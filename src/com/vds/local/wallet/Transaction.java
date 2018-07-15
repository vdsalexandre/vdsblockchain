package com.vds.local.wallet;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import com.vds.local.main.VdsBlockChain;
import com.vds.local.utils.Digest;
import com.vds.local.utils.Utils;

public class Transaction {

	public String transactionId; // hash of transaction
	public PublicKey sender; // senders address / public key
	public PublicKey recipient; // recipients address / public key
	public float value;
	public byte[] signature; // prevent anybody else from spending funds in our wallet
	
	public ArrayList<TransactionInput> inputs = new ArrayList<>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<>();
	
	private static int sequence = 0; // count how many transactions have been generated
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	private String calculateHash() {
		sequence++;
		
		return Utils.getDigest(Digest.SHA256, 
				Utils.getStringFromKey(sender) +
				Utils.getStringFromKey(recipient) +
				Float.toString(value) +
				sequence);
	}
	
	public void generateSignature(PrivateKey privateKey) {
		String data = Utils.getStringFromKey(sender) + Utils.getStringFromKey(recipient) + Float.toString(value);
		signature = Utils.applyECDSASig(privateKey, data);
	}
	
	public boolean verifySignature() {
		String data = Utils.getStringFromKey(sender) + Utils.getStringFromKey(recipient) + Float.toString(value);
		return Utils.verifyECDSASig(sender, data, signature);
	}
	
	public boolean processTransaction() {
		if (verifySignature() == false)
			return false;
		
		for (TransactionInput i : inputs) {
			i.UTXO = VdsBlockChain.UTXOs.get(i.transactionOutputId);
		}
		
		if (getInputsValue() < VdsBlockChain.minimumTransaction)
			return false;
		
		float leftOver = getInputsValue() - value; // get value of inputs then the left over change
		transactionId = calculateHash();
		outputs.add(new TransactionOutput(recipient, value, transactionId));
		outputs.add(new TransactionOutput(sender, leftOver, transactionId));
		
		for (TransactionOutput o : outputs) {
			VdsBlockChain.UTXOs.put(o.id, o);
		}
		
		for (TransactionInput i : inputs) {
			if (i.UTXO == null) continue;
			
			VdsBlockChain.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput i : inputs) {
			if (i.UTXO == null) continue; // if transaction can't be found skip it
			
			total += i.UTXO.value;
		}
		
		return total;
	}
	
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput o : outputs) {
			total += o.value;
		}
		
		return total;
	}
}
