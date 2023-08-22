package com.thomashan.property.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RangeTest {
    @Test
    void testMinGreaterThanMax() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException, {
            new Range(Optional.of(2), Optional.of(1))
        })
        assert exception.message == "Min must be lte to max"
    }

    @Test
    void testConstruction() {
        Range<Integer> range = new Range(Optional.of(1), Optional.of(2))
        assert range.min != null
        assert range.max != null
        assert range.min.get() == 1
        assert range.max.get() == 2
    }

    @Test
    void testConstructionNoMaxValue() {
        Range<Integer> range = new Range(Optional.of(1), Optional.empty())
        assert range.min != null
        assert range.max != null
        assert range.min.get() == 1
        assert range.max == Optional.empty()
    }

    @Test
    void testToStringMinMaxValuePresent() {
        Range<Integer> range = new Range(Optional.of(1), Optional.of(2))
        String string = range.toString("0", "any")
        assert "1-2" == string
    }

    @Test
    void testToStringOnlyMinPresent() {
        Range<Integer> range = new Range(Optional.of(1), Optional.empty())
        String string = range.toString("0", "any")
        assert "1-any" == string
    }

    @Test
    void testToStringOnlyMaxPresent() {
        Range<Integer> range = new Range(Optional.empty(), Optional.of(1))
        String string = range.toString("0", "any")
        assert "0-1" == string
    }

    @Test
    void testToStringNoValues() {
        Range<Integer> range = new Range(Optional.empty(), Optional.empty())
        String string = range.toString("0", "any")
        assert "" == string
    }
}
