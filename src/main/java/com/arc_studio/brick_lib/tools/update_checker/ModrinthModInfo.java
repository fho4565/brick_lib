package com.arc_studio.brick_lib.tools.update_checker;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public record ModrinthModInfo(

	@SerializedName("featured")
	boolean featured,

	@SerializedName("version_type")
	String versionType,

	@SerializedName("changelog")
	String changelog,

	@SerializedName("version_number")
	String versionNumber,

	@SerializedName("dependencies")
	List<String> dependencies,

	@SerializedName("loaders")
	List<String> loaders,

	@SerializedName("project_id")
	String projectId,

	@SerializedName("date_published")
	String datePublished,

	@SerializedName("downloads")
	int downloads,

	@SerializedName("name")
	String name,

	@SerializedName("files")
	List<ModrinthModsItem> files,

	@SerializedName("id")
	String id,

	@SerializedName("game_versions")
	List<String> gameVersions,

	@SerializedName("author_id")
	String authorId,

	@SerializedName("status")
	String status
) {
}