package com.thomashan.property.util

import java.util.stream.Collectors
import java.util.stream.Stream

class Range<T extends Comparable<T>> {
    private static final String EMPTY = ""
    final Optional<T> min
    final Optional<T> max

    Range(Optional<T> min) {
        this.min = min
        this.max = Optional.empty()
    }

    Range(Optional<T> min, Optional<T> max) {
        if (max.present && min.map { it.compareTo(max.get()) > 0 }.orElse(false)) {
            throw new RuntimeException("Min must be lte to max")
        }
        this.min = min
        this.max = max
    }

    String toString(String noMinString, String noMaxString) {
        if (min.empty && max.empty) {
            return EMPTY
        }
        if (min.empty && max.present) {
            return noMinString + "-" + max.get()
        }
        if (min.present && max.empty) {
            return min.get() + "-" + noMaxString
        }
        return Stream.concat(min.stream(), max.stream())
            .map(Object::toString)
            .collect(Collectors.joining("-"))
    }
}
