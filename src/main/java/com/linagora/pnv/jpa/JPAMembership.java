package com.linagora.pnv.jpa;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;


/*
 TODO implement Persistance logic
 */
@Entity(name ="Membership")
@Table(name ="Membership")
@IdClass(JPAMembershipId.class)
public class JPAMembership implements Serializable {
	

	private static final String NULL_VALUE = "___NULL___";
	
	@Id
	@Column(name = "groupName", nullable = true)
    private String groupName;
	
	@Id
	@Column(name = "userName", nullable = true)
    private String userName;

    public JPAMembership(String groupName, String userName) {
        this.groupName = groupName;
        this.userName = sanitizeUserName(userName);
    }
    
    public static String sanitizeUserName(String userName){
    	Preconditions.checkArgument(!Objects.equal(userName, NULL_VALUE), NULL_VALUE + " can not be used as a username");
    	
    	return Optional.ofNullable(userName)
        		.orElse(NULL_VALUE);
    }
    
    public JPAMembership() {
    	
    }
    
    /*
    TODO implements equals and hashcode
     */
	@Override
	public boolean equals(Object object) {
		if(object instanceof JPAMembership){
			JPAMembership that = (JPAMembership) object;
			return Objects.equal(this.userName, that.userName) && Objects.equal(this.groupName, that.groupName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(groupName, userName);
	}
}
