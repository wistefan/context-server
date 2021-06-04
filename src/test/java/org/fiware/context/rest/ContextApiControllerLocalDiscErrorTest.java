package org.fiware.context.rest;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.fiware.context.api.ContextServerApiTestClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

@MicronautTest(environments = "test-local")
public class ContextApiControllerLocalDiscErrorTest {


	@Inject
	private ContextServerApiTestClient testClient;


	 @DisplayName("Delete context fails on io.")
	@Test
	public void deleteFail() {
//		 Mockito.mockStatic
	 }


}
