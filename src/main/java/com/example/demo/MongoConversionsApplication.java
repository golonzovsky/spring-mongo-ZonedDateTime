package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;

@SpringBootApplication
public class MongoConversionsApplication {
	public static void main(String[] args) {
		SpringApplication.run(MongoConversionsApplication.class, args);
	}
}

@Repository
interface QueryHistoryRepo extends PagingAndSortingRepository<QueryHistoryEntry, String> { }

@Data
@NoArgsConstructor
@AllArgsConstructor
class QueryHistoryEntry {
	@Id
	private String id;
	private ZonedDateTime timestamp;
	private String query;
}

@Configuration
class MongodbConfig {

	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoDbFactory mongoDbFactory, MongoMappingContext mongoMappingContext) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
		converter.setCustomConversions(customConversions());
		return converter;
	}

	private CustomConversions customConversions() {
		return new CustomConversions(Arrays.asList(
			new ZonedDateTimeJsonDeserializer(),
			new ZonedDateTimeJsonSerializer()
		));
	}
}


class ZonedDateTimeJsonSerializer implements Converter<ZonedDateTime, String> {
	@Override
	public String convert(ZonedDateTime value) {
		return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value);
	}
}

class ZonedDateTimeJsonDeserializer implements Converter<String, ZonedDateTime> {
	@Override
	public ZonedDateTime convert(String value) {
		TemporalAccessor dt = DateTimeFormatter.ISO_DATE_TIME.parseBest(value, ZonedDateTime::from, LocalDateTime::from);
		return dt instanceof ZonedDateTime ? (ZonedDateTime) dt : ZonedDateTime.of((LocalDateTime) dt, ZoneId.of("Europe/Zurich"));
	}
}
