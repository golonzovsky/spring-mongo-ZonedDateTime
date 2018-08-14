package com.example.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.UUID;

@DataMongoTest
@Import(MongodbConfig.class)
@RunWith(SpringRunner.class)
public class UserQueryHistoryRepositoryImplTest {
	
	@Autowired
	private QueryHistoryRepo userQueryHistoryRepository;

	@Test
	public void testCRUD() {
		ZonedDateTime startDate = ZonedDateTime.now().withFixedOffsetZone();

		String userId = UUID.randomUUID().toString();
		String queryText = "cafe";
		QueryHistoryEntry query = new QueryHistoryEntry(userId, startDate, queryText);
		userQueryHistoryRepository.save(query);

		Assert.assertEquals(1, userQueryHistoryRepository.count());

		// retrieve the same query
		QueryHistoryEntry history = userQueryHistoryRepository.findById(userId).orElse(null);
		Assert.assertNotNull(history);

		Assert.assertEquals(userId, history.getQuery());

	}


}
