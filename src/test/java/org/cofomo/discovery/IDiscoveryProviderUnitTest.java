package org.cofomo.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.cofomo.discovery.controller.DiscoveryFacade;
import org.cofomo.discovery.controller.DiscoveryProviderController;
import org.cofomo.discovery.domain.MobilityProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@WebMvcTest(DiscoveryProviderController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class IDiscoveryProviderUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private DiscoveryFacade facade;

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation).uris().withScheme("https")
						.withHost("api.cofomo.org").withPort(443))
				.alwaysDo(
						document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
				.build();
	}

	@Test
	public void shouldReturnAllMobilityProvider() throws Exception {

		// create list of mobility providers
		List<MobilityProvider> mpList = new ArrayList<MobilityProvider>();
		mpList.add(createMobilityProvider1());
		mpList.add(createMobilityProvider2());

		// define mock return value
		when(facade.getAll()).thenReturn(mpList);

		// action
		this.mockMvc.perform(get("/v1")).andDo(print()).andExpect(status().isOk())
				.andDo(document("get-all", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}
	
	@Test
	public void shouldReturnAllActiveMobilityProvider() throws Exception {

		// create list of mobility providers
		List<MobilityProvider> mpList = new ArrayList<MobilityProvider>();
		mpList.add(createMobilityProvider1());
		mpList.add(createMobilityProvider2());

		// define mock return value
		when(facade.getAllActive()).thenReturn(mpList);

		// action
		this.mockMvc.perform(get("/v1/active")).andDo(print()).andExpect(status().isOk())
				.andDo(document("get-all-active", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	public void shouldReturnOneMobilityProvider() throws Exception {

		// create mobility provider
		MobilityProvider mp = createMobilityProvider1();

		// define mock return value
		when(facade.get("1")).thenReturn(mp);
		// action
		this.mockMvc.perform(get("/v1/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andDo(document("get-by-id", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						responseFields(fieldWithPath("id").description("Autogenerated UUID of the provider"),
								fieldWithPath("name").description("Name of the provider"),
								fieldWithPath("url").description("URI-endpoint of provider"),
								fieldWithPath("operationAreas").description("Operation area of provider"),
								fieldWithPath("serviceOffers").description("Services offered by provider"),
								fieldWithPath("alive").description("Service still alive?"),
								fieldWithPath("lastHeartBeat").description("Time of last heart beat"))))
				.andDo(print());
	}

	@Test
	public void shouldAddMobilityProvider() throws Exception {

		// create mobility provider
		MobilityProvider mp = createMobilityProvider1();

		// define mock return value
		when(facade.create(mp)).thenReturn(mp);

		// action
		MvcResult result = this.mockMvc
				.perform(post("/v1").content(objectMapper.writeValueAsString(mp))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isCreated())
				.andDo(document("add-provider", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						requestFields(fieldWithPath("id").description("Autogenerated UUID of the provider"),
								fieldWithPath("name").description("Name of the provider"),
								fieldWithPath("url").description("URI-endpoint of provider"),
								fieldWithPath("operationAreas").description("Operation area of provider"),
								fieldWithPath("serviceOffers").description("Services offered by provider"),
								fieldWithPath("alive").description("Service still alive?"),
								fieldWithPath("lastHeartBeat").description("Time of last heart beat"))))
				.andReturn();

		// compare response object to request object
		String mpRequest = objectMapper.writeValueAsString(mp);
		String mpResponse = result.getResponse().getContentAsString();
		assertThat(mpRequest).isEqualToIgnoringWhitespace(mpResponse);
	}

	@Test
	public void shouldUpdateOneMobilityProvider() throws Exception {

		// create mobility provider
		MobilityProvider mp = createMobilityProvider1();

		// action
		this.mockMvc
				.perform(put("/v1/1").content(objectMapper.writeValueAsString(mp))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andDo(document("update", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						requestFields(fieldWithPath("id").description("Autogenerated UUID of the provider"),
								fieldWithPath("name").description("Name of the provider"),
								fieldWithPath("url").description("URI-endpoint of provider"),
								fieldWithPath("operationAreas").description("Operation area of provider"),
								fieldWithPath("serviceOffers").description("Services offered by provider"),
								fieldWithPath("alive").description("Service still alive?"),
								fieldWithPath("lastHeartBeat").description("Time of last heart beat"))))
				.andDo(print());
	}

	@Test
	public void shouldDeleteMobilityProvider() throws Exception {

		// action
		this.mockMvc.perform(delete("/v1/1")).andExpect(status().isOk()).andDo(document("delete"))
				.andDo(print());
	}

	// helper functions
	private static MobilityProvider createMobilityProvider1() {
		List<String> serviceOffers = new ArrayList<String>();
		List<Integer> areas = new ArrayList<Integer>();
		serviceOffers.add("carsharing");
		areas.add(72072);
		return new MobilityProvider("Carsharing Inc.", "https://test.carsharing.org", areas, serviceOffers);
	}

	private static MobilityProvider createMobilityProvider2() {
		List<String> serviceOffers = new ArrayList<String>();
		List<Integer> areas = new ArrayList<Integer>();
		serviceOffers.add("bikesharing");
		serviceOffers.add("escootersharing");
		areas.add(72073);
		return new MobilityProvider("BikeSharing Inc.", "https://test.carsharing.org", areas, serviceOffers);
	}
}
