package de.bsailer.routing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TargetDijkstraAborterTest {

	@Mock
	private Edge current;

	@Mock
	private Edge target;

	@Test
	public void abortsOnTarget() {
		final var sut = new TargetDijkstraAborter(target);
		assertTrue(sut.abort(target));
	}

	@Test
	public void continuesElse() {
		final var sut = new TargetDijkstraAborter(target);
		assertFalse(sut.abort(current));
	}
}
