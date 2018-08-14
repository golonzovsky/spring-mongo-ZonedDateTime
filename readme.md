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