package com.linagora.pnv.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.linagora.pnv.GroupMembershipResolver;

/*
TODO fill the gaps
 */

public class JPAGroupMembershipResolver implements GroupMembershipResolver {

	private final EntityManager entityManager;

	public JPAGroupMembershipResolver(EntityManagerFactory entityManagerFactory) {
		this.entityManager = entityManagerFactory.createEntityManager();
	}

	@Override
	public void addMembership(String group, String user) {
		this.entityManager.getTransaction().begin();
		this.entityManager.merge(new JPAMembership(group, user));
		this.entityManager.getTransaction().commit();
	}

	@Override
	public void removeMembership(String group, String user) {
		this.entityManager.getTransaction().begin();
		this.entityManager.remove(new JPAMembership(group, user));
		this.entityManager.getTransaction().commit();
	}

	@Override
	public boolean isMember(String user, String group) {
		return this.entityManager.find(JPAMembership.class, new JPAMembershipId(group, user)) != null;
	}
}
