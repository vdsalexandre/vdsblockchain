package com.vds.local.blockchain;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;
import com.vds.local.main.VdsBlockChain;
import com.vds.local.wallet.Transaction;
import com.vds.local.wallet.TransactionInput;
import com.vds.local.wallet.TransactionOutput;

public final class BlockChain {
	private static ArrayList<Block> blockChain = new ArrayList<>();
	private static int difficulty = 5;
	
	public static void addToChain(Block block) {
		block.mineBlock(difficulty);
		blockChain.add(block);
	}
	
	public static String chainToJSON() {
		String chainToJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
		return chainToJSON;
	}
	
	public static Block getBlock(int position) {
		return blockChain.get(position);
	}
	
	public static Block getLastBlock() {
		return blockChain.get(blockChain.size() - 1);
	}
	
	public static int getSize() {
		return blockChain.size();
	}
	
	public static boolean isValidChain() {
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
		tempUTXOs.put(VdsBlockChain.genesisTransaction.outputs.get(0).id, VdsBlockChain.genesisTransaction.outputs.get(0));
		
		
		for (int i = 1; i < blockChain.size(); i++) {
			Block currentBlock = blockChain.get(i);
			Block previousBlock = blockChain.get(i - 1);
			
			// compare le hash courant avec le hash courant calculé
			if (!currentBlock.getHash().equals(currentBlock.calculateHash()))
				return false;
			
			// compare le hash précédent avec le hash précédent enregistré
			if (!previousBlock.getHash().equals(currentBlock.getPreviousHash()))
				return false;
			
			// vérifie si le hash est résolu
			if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget))
				return false;
			
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if (!currentTransaction.verifySignature())
					return false;
				
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue())
					return false;
				
				for (TransactionInput input : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if (tempOutput == null) return false;
					
					if (input.UTXO.value != tempOutput.value) return false;
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for (TransactionOutput output : currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient)
					return false;
				
				if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender)
					return false;
			}
			
			System.out.println("Blockchain is valid");
		}
		
		return true;
	}

	public static int getDifficulty() {
		return difficulty;
	}

	public static void setDifficulty(int difficulty) {
		BlockChain.difficulty = difficulty;
	}
}
