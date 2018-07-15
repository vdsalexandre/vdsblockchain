package com.vds.local.wallet;

import java.security.PublicKey;

import com.vds.local.utils.Digest;
import com.vds.local.utils.Utils;

public class TransactionOutput {
	public String id;
	public PublicKey recipient; // owner of these coins
	public float value;
	public String parentTransactionId; // id of the transaction this output was created in
	
	public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = Utils.getDigest(Digest.SHA256, Utils.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
	}
	
	public boolean isMine(PublicKey publicKey) {
		return publicKey == recipient;
	}
}
