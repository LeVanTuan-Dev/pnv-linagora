package com.linagora.pnv.jpa;

import java.io.Serializable;

public class JPAMembershipId implements Serializable {

	private String groupName;
	private String userName;
	
	public JPAMembershipId(String groupName, String userName) {
		super();
		this.groupName = groupName;
		this.userName = userName;
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
		if (this == other) return true;
        if ( !(other instanceof JPAMembershipId) ) return false;
        return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
