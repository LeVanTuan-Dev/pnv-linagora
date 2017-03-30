package com.linagora.pnv.jpa;

import java.io.Serializable;
import java.util.Optional;

import com.google.common.base.Objects;

public class JPAMembershipId implements Serializable {

	private String groupName;
	private String userName;
	
	public JPAMembershipId(String groupName, String userName){
		super();
		this.groupName = groupName;
		this.userName =JPAMembership.sanitizeUserName(userName);
	}
	
	public JPAMembershipId(){
		
	}

	public String getGroupName() {
		return groupName;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof JPAMembershipId){
			JPAMembershipId that = (JPAMembershipId) other;
			return Objects.equal(this.userName, that.userName) && Objects.equal(this.groupName, that.groupName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(groupName, userName);
	}
	
}
