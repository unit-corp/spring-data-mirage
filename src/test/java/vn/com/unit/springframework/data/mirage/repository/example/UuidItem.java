package vn.com.unit.springframework.data.mirage.repository.example;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

/**
 * Entity whose primary key is a UUID v7 generated on insert.
 */
@Table(name = "uuid_items")
public class UuidItem {

	@Id
	@Column(name = "id")
	@PrimaryKey(generationType = GenerationType.UUID_V7)
	private UUID id;

	@Column(name = "name")
	private String name;

	public UuidItem(String name) {
		this.name = name;
	}

	UuidItem() {
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
