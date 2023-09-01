package com.thomashan.property.model.buy

import com.thomashan.property.model.DataSource
import com.thomashan.property.model.SaleType
import com.thomashan.property.model.map.LatLongCoordinates
import com.thomashan.property.util.Range
import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.ImmutableBase

@ImmutableBase(copyWith = true)
@Immutable(knownImmutableClasses = [Optional])
@EqualsAndHashCode(excludes = ["coordinates"])
class BuyDetails {
    Optional<Range<BigDecimal>> priceRange = Optional.empty()
    Optional<BigDecimal> price = Optional.empty()
    String address
    String suburb
    String state
    String postcode
    Optional<Integer> bedrooms = Optional.empty()
    Optional<Integer> bathrooms = Optional.empty()
    Optional<Integer> parking = Optional.empty()
    Optional<Integer> landSize = Optional.empty()
    SaleType saleType
    DataSource dataSource
    Optional<LatLongCoordinates> coordinates = Optional.empty()
}
