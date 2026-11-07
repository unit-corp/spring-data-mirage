package vn.com.unit.springframework.data.mirage.repository.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import vn.com.unit.springframework.data.mirage.repository.TestConfiguration;

/**
 * Test for saving entities whose id is generated on insert.
 */
@SpringBootTest
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class UuidItemRepositoryTest {

    @Autowired
    UuidItemRepository repos;

    @Test
    public void saveNewEntityWithoutIdGeneratesUuid() {
        UuidItem item = repos.save(new UuidItem("a"));
        assertThat(item.getId(), is(notNullValue()));
        assertThat(item.getId().version(), is(7));
        assertThat(repos.count(), is(1L));
        assertThat(repos.findOne(item.getId()).getName(), is("a"));
    }

    @Test
    public void saveExistingEntityUpdates() {
        UuidItem item = repos.save(new UuidItem("a"));
        item.setName("b");
        repos.save(item);
        assertThat(repos.count(), is(1L));
        assertThat(repos.findOne(item.getId()).getName(), is("b"));
    }
}
