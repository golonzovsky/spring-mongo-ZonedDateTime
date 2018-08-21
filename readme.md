# spring data mongo ZonedDateTime

This is to reproduce issue with spring-data-mongo custom converters, which kicks in for normal string types.

## instructions 
Run the test. Framework tries to convert string value 'cafe' to ZonedDateTime.

```
org.springframework.core.convert.ConversionFailedException: Failed to convert from type [java.lang.String] to type [java.time.ZonedDateTime] for value 'cafe'; nested exception is java.time.format.DateTimeParseException: Text 'cafe' could not be parsed at index 0

	at org.springframework.core.convert.support.ConversionUtils.invokeConverter(ConversionUtils.java:46)
	at org.springframework.core.convert.support.GenericConversionService.convert(GenericConversionService.java:191)
	at org.springframework.core.convert.support.GenericConversionService.convert(GenericConversionService.java:174)
```

## UPD: fix

See Jira ticket: https://jira.spring.io/browse/DATAMONGO-2063

Missing piece was @WritingConverter on ZonedDateTimeJsonSerializer and @ReadingConverter on ZonedDateTimeJsonDeserializer.

Quoting some parts here as well:

> both ZonedDateTime as well as String are considered simple types in the first place. That means you need to annotate the converters with @ReadingConverter and @WritingConverter in the first place to make sure we know when to use which.

See related documentation as well: https://docs.spring.io/spring-data/mongodb/docs/2.0.8.RELEASE/reference/html/#mongo.converter-disambiguation

## Notes on ZonedDateTime and Mongo:

> In general, persisting date/time types with time zone information into a single field is not recommended as it completely breaks sortabilty of those fields. We rather recommend to transform all zoned types into UTC and also persist the offset as time-zones are usually a presentation layer issue.