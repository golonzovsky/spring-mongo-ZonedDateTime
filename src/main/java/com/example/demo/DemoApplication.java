package com.example.demo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

@RestController
@RequiredArgsConstructor
class Controller {

	private final QueryHistoryRepo repo;

	@GetMapping("/{id}")
	public QueryHistoryEntry getStuff(@PathVariable String id) {
		return repo.findById(id).orElse(null);
	}

	@PostMapping("/")
	public QueryHistoryEntry save(@RequestBody QueryHistoryEntryRepresentation query) {
		String userId = UUID.randomUUID().toString();
		return repo.save(new QueryHistoryEntry(userId, query.getTimestamp(), query.getQ()));
	}

}

@Repository
interface QueryHistoryRepo extends PagingAndSortingRepository<QueryHistoryEntry, String> {

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class QueryHistoryEntry {
	@Id
	private String id;
	private ZonedDateTime timestamp;
	private String query;
}

@Value
class QueryHistoryEntryRepresentation {

	@JsonSerialize(using = ZonedDateTimeJsonSerializer.class)
	@JsonDeserialize(using = ZonedDateTimeJsonDeserializer.class)
	private ZonedDateTime timestamp;

	private String q;

}


@Configuration
class MongodbConfig {

	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoDbFactory mongoDbFactory, MongoMappingContext mongoMappingContext) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		converter.setCustomConversions(customConversions());
		return converter;
	}

	private MongoCustomConversions customConversions() {
		return new MongoCustomConversions(Arrays.asList(
			new ZonedDateTimeJsonDeserializer(),
			new ZonedDateTimeJsonSerializer()
		));
	}
}


class ZonedDateTimeJsonSerializer extends JsonSerializer<ZonedDateTime> implements Converter<ZonedDateTime, String> {

	@Override
	public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeString(convert(zonedDateTime));
	}

	@Override
	public String convert(ZonedDateTime value) {
		return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value);
	}
}

class ZonedDateTimeJsonDeserializer extends JsonDeserializer<ZonedDateTime> implements Converter<String, ZonedDateTime> {

	private static final ZoneId DEFAULT_TIMEZONE_CH = ZoneId.of("Europe/Zurich");

	@Override
	public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		return convert(jsonParser.getText());
	}

	@Override
	public ZonedDateTime convert(String value) {
		TemporalAccessor dt = DateTimeFormatter.ISO_DATE_TIME.parseBest(value, ZonedDateTime::from, LocalDateTime::from);
		return dt instanceof ZonedDateTime ? (ZonedDateTime) dt : ZonedDateTime.of((LocalDateTime) dt, DEFAULT_TIMEZONE_CH);
	}
}
