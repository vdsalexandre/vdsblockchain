package com.vds.local.utils;

public enum Digest {
	MD5 ("MD5"),
	SHA256 ("SHA-256"),
	SHA512 ("SHA-512");
	
	private String digest;
	
	Digest(String digest) {
		this.digest = digest;
	}
	
	public String toString() {
		return this.digest;
	}
}