package com.vds.local.wallet;

public class TransactionInput {
	public String transactionOutputId; // reference to TransactionOutputs
	public TransactionOutput UTXO; // contains the unspent transaction output;
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
