package com.lakesidemutual.customerselfservice.interfaces.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import com.lakesidemutual.customerselfservice.domain.customer.CustomerId;

/**
 * This converter class allows us to use CustomerId as the type of
 * a @PathVariable parameter in a Spring @RestController class.
 */
@Profile("!migration")
@Component
public class StringToCustomerIdConverter implements Converter<String, CustomerId> {
	@Override
	public CustomerId convert(String source) {
		return new CustomerId(source);
	}
}
