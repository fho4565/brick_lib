package com.arc_studio.brick_lib.tools.update_checker;

import com.google.gson.annotations.SerializedName;

public record ModrinthModsItem(

	@SerializedName("filename")
	String filename,

	@SerializedName("size")
	int size,

	@SerializedName("hashes")
    ModrinthModHashes hashes,

	@SerializedName("url")
	String url,

	@SerializedName("primary")
	boolean primary
) {
}