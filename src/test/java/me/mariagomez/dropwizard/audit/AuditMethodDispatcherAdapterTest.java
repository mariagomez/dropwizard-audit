package me.mariagomez.dropwizard.audit;

import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class AuditMethodDispatcherAdapterTest {

    private AuditMethodDispatcherAdapter adapter;

    @Before
    public void setUp() {
        adapter = new AuditMethodDispatcherAdapter();
    }

    @Test
    public void shouldBeAnInstanceOfResourceMethodDispatcherAdapter() {
        assertThat(adapter, instanceOf(ResourceMethodDispatchAdapter.class));
    }

    @Test
    public void shouldReturnAnInstanceOfAuditProvider() {
        ResourceMethodDispatchProvider provider = adapter.adapt(mock(ResourceMethodDispatchProvider.class));

        assertThat(provider, instanceOf(AuditProvider.class));
    }

}