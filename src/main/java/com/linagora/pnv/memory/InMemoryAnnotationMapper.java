/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package com.linagora.pnv.memory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Table;
import com.linagora.pnv.AnnotationMapper;
import com.linagora.pnv.MailboxAnnotation;
import com.linagora.pnv.MailboxAnnotationKey;
import com.linagora.pnv.MailboxException;
import com.linagora.pnv.MailboxId;

public class InMemoryAnnotationMapper implements AnnotationMapper {
	private final Table<InMemoryId, String, String> mailboxesAnnotations;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public InMemoryAnnotationMapper() {
		mailboxesAnnotations = HashBasedTable.create();
	}

	@Override
	public void endRequest() {

	}

	@Override
	public <T> T execute(Transaction<T> transaction) throws MailboxException {
		return transaction.run();
	}
	
	private List<MailboxAnnotation> retrieveAllAnnotations(InMemoryId mailboxId) {
		lock.readLock().lock();
		try {
			return mailboxesAnnotations.row(mailboxId).entrySet().stream().map(
					input -> MailboxAnnotation.newInstance(new MailboxAnnotationKey(input.getKey()), input.getValue()))
					.collect(Collectors.toList());
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<MailboxAnnotation> getAllAnnotations(MailboxId mailboxId) {
		return ImmutableList.copyOf(retrieveAllAnnotations((InMemoryId) mailboxId));
	}
	
	@Override
	public List<MailboxAnnotation> getAnnotationsByKeys(MailboxId mailboxId, final Set<MailboxAnnotationKey> keys) {
		return ImmutableList.copyOf(retrieveAllAnnotations((InMemoryId) mailboxId))
				.stream()
				.filter(input -> keys.contains(input.getKey()))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<MailboxAnnotation> getAnnotationsByKeysWithAllDepth(MailboxId mailboxId,
			final Set<MailboxAnnotationKey> keys) {
		return retrieveAllAnnotations((InMemoryId) mailboxId)
			.stream()
			.filter(anotation -> getPredicateFilterByAll(keys).apply(anotation))
			.collect(Collectors.toList());
	}

	@Override
	public List<MailboxAnnotation> getAnnotationsByKeysWithOneDepth(MailboxId mailboxId,
			final Set<MailboxAnnotationKey> keys) {
		return getAnnotationsByKeysWithAllDepth(mailboxId, keys)
				.stream()
				.filter(anotation -> getPredicateFilterByOne(keys).apply(anotation))
				.collect(Collectors.toList());
	}
	private Predicate<MailboxAnnotation> getPredicateFilterByAll(final Set<MailboxAnnotationKey> keys) {
		return input -> keys.stream().anyMatch(key -> filterAnnotationsByPrefix(input).apply(key));
	}

	private Predicate<MailboxAnnotation> getPredicateFilterByOne(final Set<MailboxAnnotationKey> keys) {
		return input -> keys.stream()
				.anyMatch(key -> filterAnnotationsByParentKey(input.getKey()).apply(key));
	}

	private Predicate<MailboxAnnotationKey> filterAnnotationsByParentKey(final MailboxAnnotationKey input) {
		
		return key -> input.countComponents() <= (key.countComponents() + 1);
	}

	private Predicate<MailboxAnnotationKey> filterAnnotationsByPrefix(final MailboxAnnotation input) {
		return key -> key.equals(input.getKey()) 
				|| StringUtils.startsWith(input.getKey().asString(), key.asString() + "/");
	}

	@Override
	public void insertAnnotation(MailboxId mailboxId, MailboxAnnotation mailboxAnnotation) {
		Preconditions.checkArgument(!mailboxAnnotation.isNil());
		lock.writeLock().lock();
		try {
			mailboxesAnnotations.put((InMemoryId) mailboxId, mailboxAnnotation.getKey().asString(),
					mailboxAnnotation.getValue().get());
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void deleteAnnotation(MailboxId mailboxId, MailboxAnnotationKey key) {
		lock.writeLock().lock();
		try {
			mailboxesAnnotations.remove(mailboxId, key.asString());
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean exist(MailboxId mailboxId, MailboxAnnotation mailboxAnnotation) {
		return mailboxesAnnotations.contains((InMemoryId) mailboxId, mailboxAnnotation.getKey().asString());
	}

	@Override
	public int countAnnotations(MailboxId mailboxId) {
		return mailboxesAnnotations.row((InMemoryId) mailboxId).size();
	}

}
