package com.smart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	
	@NotBlank(message = "First Name is required..")
	@Size(min = 2, max = 20, message = "First name should be upto 20 characters")
	private String name;
	
	@NotBlank(message = "Last name is also required..")
	@Size(min = 2, max = 20, message = "First name should be upto 20 characters")
	private String secondName;
	
	@NotBlank(message = "work is required..")
	private String work;
	
	@Email(message = "email is not valid")
	@NotBlank(message = "email is required...")
	private String email;
	
	private String image;
	
	@NotBlank(message = "Phone number is required...")
	@Size(min = 10, max = 10, message = "Phone number must be 10 digits..")
	private String phone;
	
	@Column(length = 1000)
	@NotBlank(message = "Discription is required...")
	@Size(min = 5, max = 500, message = "Description must be form 5 to 500 character")
	private String description;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	
	
	public Contact() {
		
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

//	@Override
//	public String toString() {
//		return "Contact [cid=" + cid + ", name=" + name + ", secondName=" + secondName + ", work=" + work + ", email="
//				+ email + ", image=" + image + ", phone=" + phone + ", description=" + description + ", user=" + user
//				+ "]";
//	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.cid==((Contact)obj).getCid();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
