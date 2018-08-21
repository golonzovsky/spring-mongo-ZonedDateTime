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
public class MongoCustomConversionsTest {

	@Autowired
	private QueryHistoryRepo userQueryHistoryRepository;

	@Test
	public void testCRUD() {
		ZonedDateTime date = ZonedDateTime.now().withFixedOffsetZone();

		String userId = UUID.randomUUID().toString();
		String queryText = "cafe";
		QueryHistoryEntry query = new QueryHistoryEntry(userId, date, queryText);
		userQueryHistoryRepository.save(query);

		Assert.assertEquals(1, userQueryHistoryRepository.count());

		QueryHistoryEntry history = userQueryHistoryRepository.findById(userId).orElse(null);
		Assert.assertNotNull(history);
		Assert.assertEquals(userId, history.getId());
		Assert.assertEquals(date, history.getTimestamp());
		Assert.assertEquals(queryText, history.getQuery());
	}

}
