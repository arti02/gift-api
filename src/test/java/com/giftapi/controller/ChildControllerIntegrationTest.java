package com.giftapi.controller;

import com.giftapi.common.GiftApiTestHelper;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.dto.command.gift.AddGiftCommand;
import com.giftapi.model.dto.command.gift.UpdateGiftCommand;
import com.giftapi.model.entity.Boy;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Gift;
import com.giftapi.model.entity.Girl;
import com.giftapi.repository.ChildRepository;
import com.giftapi.repository.GiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ChildControllerIntegrationTest extends GiftApiTestHelper {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ChildRepository childRepository;

	@Autowired
	private GiftRepository giftRepository;

	@BeforeEach
	void cleanup() {
		giftRepository.deleteAll();
		childRepository.deleteAll();
	}

	@Test
	void getChildren_returnsPageWithChildren() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Girl girl = createGirl("Alice", "Smith");
		childRepository.save(boy);
		childRepository.save(girl);

		// when / then
		mockMvc.perform(get("/api/v1/children")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.content[0].id").isNotEmpty())
				.andExpect(jsonPath("$.content[0].firstName").isNotEmpty())
				.andExpect(jsonPath("$.content[0].lastName").isNotEmpty())
				.andExpect(jsonPath("$.totalElements").value(2));
	}

	@Test
	void getChildren_withFilters_returnsFilteredResults() throws Exception {
		// given
		Boy boy1 = createBoy("John", "Doe");
		Boy boy2 = createBoy("Mike", "Smith");
		childRepository.save(boy1);
		childRepository.save(boy2);

		// when / then - filter by lastName
		mockMvc.perform(get("/api/v1/children")
						.param("page", "0")
						.param("size", "10")
						.param("lastName", "Doe"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].lastName").value("Doe"));
	}

	@Test
	void getById_returnsChild() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		// when / then
		mockMvc.perform(get("/api/v1/children/" + saved.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(saved.getId().intValue()))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"));
	}

	@Test
	void getById_returnsNotFound_whenChildDoesNotExist() throws Exception {
		// when / then
		mockMvc.perform(get("/api/v1/children/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void create_createsChildAndReturnsCreated() throws Exception {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put("type", "BOY");
		properties.put("firstName", "John");
		properties.put("lastName", "Doe");
		properties.put("birthDate", "2015-05-10");
		properties.put("favoriteSport", "Football");

		CreateChildCommand cmd = new CreateChildCommand(properties);
		String body = toJson(cmd);

		// when / then
		mockMvc.perform(post("/api/v1/children")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"));

		// and persisted
		List<Child> all = childRepository.findAll();
		assertThat(all).hasSize(1);
		Child persisted = all.getFirst();
		assertThat(persisted.getFirstName()).isEqualTo("John");
		assertThat(persisted.getLastName()).isEqualTo("Doe");
		assertThat(persisted).isInstanceOf(Boy.class);
	}

	@Test
	void create_createsGirlWithDressColor() throws Exception {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put("type", "GIRL");
		properties.put("firstName", "Alice");
		properties.put("lastName", "Smith");
		properties.put("birthDate", "2016-03-15");
		properties.put("dressColor", "Pink");

		CreateChildCommand cmd = new CreateChildCommand(properties);
		String body = toJson(cmd);

		// when / then
		mockMvc.perform(post("/api/v1/children")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.firstName").value("Alice"));

		// and persisted with specific properties
		List<Child> all = childRepository.findAll();
		assertThat(all).hasSize(1);
		assertThat(all.getFirst()).isInstanceOf(Girl.class);
		Girl girl = (Girl) all.getFirst();
		assertThat(girl.getDressColor()).isEqualTo("Pink");
	}

	@Test
	void update_updatesChildAndReturnsUpdated() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		Map<String, Object> properties = new HashMap<>();
		properties.put("type", "BOY");
		properties.put("firstName", "Johnny");
		properties.put("lastName", "Doe");
		properties.put("birthDate", "2015-05-10");
		properties.put("favoriteSport", "Basketball");

		UpdateChildCommand cmd = new UpdateChildCommand(properties);
		String body = toJson(cmd);

		// when / then
		mockMvc.perform(put("/api/v1/children/" + saved.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(saved.getId().intValue()))
				.andExpect(jsonPath("$.firstName").value("Johnny"));

		// and persisted
		Child updated = childRepository.findById(saved.getId()).orElseThrow();
		assertThat(updated.getFirstName()).isEqualTo("Johnny");
		assertThat(((Boy) updated).getFavoriteSport()).isEqualTo("Basketball");
	}

	@Test
	void delete_deletesChild() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		// when / then
		mockMvc.perform(delete("/api/v1/children/" + saved.getId()))
				.andExpect(status().isNoContent());
		assertThat(childRepository.findById(saved.getId())).isEmpty();
	}

	@Test
	void delete_returnsNotFound_whenChildDoesNotExist() throws Exception {
		// when / then
		mockMvc.perform(delete("/api/v1/children/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getGiftsForChild_returnsListOfGifts() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		Gift gift1 = createGift("Toy Car", BigDecimal.valueOf(29.99), saved);
		Gift gift2 = createGift("Lego Set", BigDecimal.valueOf(49.99), saved);
		giftRepository.save(gift1);
		giftRepository.save(gift2);

		// when / then
		mockMvc.perform(get("/api/v1/children/" + saved.getId() + "/gifts"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").isNotEmpty())
				.andExpect(jsonPath("$[0].price").isNotEmpty());
	}

	@Test
	void getGiftByIdForChild_returnsGift() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		Gift gift = createGift("Toy Car", BigDecimal.valueOf(29.99), saved);
		Gift savedGift = giftRepository.save(gift);

		// when / then
		mockMvc.perform(get("/api/v1/children/" + saved.getId() + "/gifts/" + savedGift.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedGift.getId().intValue()))
				.andExpect(jsonPath("$.name").value("Toy Car"))
				.andExpect(jsonPath("$.price").value(29.99));
	}

	@Test
	void addGiftToChild_createsGiftAndReturnsCreated() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		AddGiftCommand cmd = new AddGiftCommand("Toy Car", BigDecimal.valueOf(29.99));
		String body = toJson(cmd);

		// when / then
		mockMvc.perform(post("/api/v1/children/" + saved.getId() + "/gifts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Toy Car"))
				.andExpect(jsonPath("$.price").value(29.99))
				.andExpect(jsonPath("$.childId").value(saved.getId().intValue()));

		// and persisted
		List<Gift> gifts = giftRepository.findAll();
		assertThat(gifts).hasSize(1);
		assertThat(gifts.getFirst().getName()).isEqualTo("Toy Car");
		assertThat(gifts.getFirst().getChild().getId()).isEqualTo(saved.getId());
	}

	@Test
	void addGiftToChild_returnsBadRequest_whenChildHasMaxGifts() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		giftRepository.save(createGift("Gift 1", BigDecimal.valueOf(10), saved));
		giftRepository.save(createGift("Gift 2", BigDecimal.valueOf(20), saved));
		giftRepository.save(createGift("Gift 3", BigDecimal.valueOf(30), saved));

		AddGiftCommand cmd = new AddGiftCommand("Gift 4", BigDecimal.valueOf(40));
		String body = toJson(cmd);

		// when / then
		mockMvc.perform(post("/api/v1/children/" + saved.getId() + "/gifts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest());

		// and not persisted
		assertThat(giftRepository.count()).isEqualTo(3);
	}

	@Test
	void updateGiftForChild_updatesGiftAndReturnsUpdated() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		Gift gift = createGift("Toy Car", BigDecimal.valueOf(29.99), saved);
		Gift savedGift = giftRepository.save(gift);

		UpdateGiftCommand cmd = new UpdateGiftCommand("Updated Toy", BigDecimal.valueOf(39.99));
		String body = toJson(cmd);

		// when / then
		mockMvc.perform(put("/api/v1/children/" + saved.getId() + "/gifts/" + savedGift.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedGift.getId().intValue()))
				.andExpect(jsonPath("$.name").value("Updated Toy"))
				.andExpect(jsonPath("$.price").value(39.99));

		// and persisted
		Gift updated = giftRepository.findById(savedGift.getId()).orElseThrow();
		assertThat(updated.getName()).isEqualTo("Updated Toy");
		assertThat(updated.getPrice()).isEqualTo(BigDecimal.valueOf(39.99));
	}

	@Test
	void deleteGiftFromChild_deletesGift() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		Gift gift = createGift("Toy Car", BigDecimal.valueOf(29.99), saved);
		Gift savedGift = giftRepository.save(gift);

		// when / then
		mockMvc.perform(delete("/api/v1/children/" + saved.getId() + "/gifts/" + savedGift.getId()))
				.andExpect(status().isNoContent());

		// and deleted from database
		assertThat(giftRepository.findById(savedGift.getId())).isEmpty();
	}

	@Test
	void getChildren_withGiftCount_returnsChildrenWithGiftCounts() throws Exception {
		// given
		Boy boy = createBoy("John", "Doe");
		Boy saved = childRepository.save(boy);

		giftRepository.save(createGift("Gift 1", BigDecimal.valueOf(10), saved));
		giftRepository.save(createGift("Gift 2", BigDecimal.valueOf(20), saved));

		// when / then
		mockMvc.perform(get("/api/v1/children")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].giftCount").value(2));
	}
}
