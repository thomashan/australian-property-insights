package com.thomashan.property.model

import com.thomashan.property.util.Range

class SearchCriteria {
    final Set<String> suburbs
    Optional<EnumSet<PropertyType>> propertyTypes = Optional.empty()
    Optional<Range<BigDecimal>> price = Optional.empty()
    Optional<Range<Integer>> bedrooms = Optional.empty()
    Optional<Range<Integer>> bathrooms = Optional.empty()
    Optional<Range<Integer>> parking = Optional.empty()
    Optional<Boolean> includeSurrounding = Optional.empty()
    Optional<Boolean> excludeUnderOffer = Optional.empty()
    Optional<EstablishType> establishType = Optional.empty()
    Optional<Range<Integer>> landSize = Optional.empty()
    Optional<Set<Feature>> features = Optional.empty()

    private SearchCriteria() {
        this.suburbs = Set.of()
    }

    private SearchCriteria(Set<String> suburbs,
                           Optional<EnumSet<PropertyType>> propertyTypes,
                           Optional<Range<BigDecimal>> price,
                           Optional<Range<Integer>> bedrooms,
                           Optional<Range<Integer>> bathrooms,
                           Optional<Range<Integer>> parking,
                           Optional<Boolean> includeSurrounding,
                           Optional<Boolean> excludeUnderOffer,
                           Optional<EstablishType> establishType,
                           Optional<Range<Integer>> landSize,
                           Optional<Set<Feature>> features) {
        this.suburbs = suburbs
        this.propertyTypes = propertyTypes
        this.price = price
        this.bedrooms = bedrooms
        this.bathrooms = bathrooms
        this.parking = parking
        this.includeSurrounding = includeSurrounding
        this.excludeUnderOffer = excludeUnderOffer
        this.establishType = establishType
        this.landSize = landSize
        this.features = features
    }

    static class Builder {
        final Set<String> suburbs
        Optional<EnumSet<PropertyType>> propertyTypes = Optional.empty()
        Optional<Range<BigDecimal>> price = Optional.empty()
        Optional<Range<Integer>> bedrooms = Optional.empty()
        Optional<Range<Integer>> bathrooms = Optional.empty()
        Optional<Range<Integer>> parking = Optional.empty()
        Optional<Boolean> includeSurrounding = Optional.empty()
        Optional<Boolean> excludeUnderOffer = Optional.empty()
        Optional<EstablishType> establishType = Optional.empty()
        Optional<Range<Integer>> landSize = Optional.empty()
        Optional<Set<Feature>> features = Optional.empty()

        Builder(Set<String> suburbs) {
            this.suburbs = Objects.requireNonNull(suburbs)
        }

        Builder propertyTypes(EnumSet<PropertyType> propertyTypes) {
            this.propertyTypes = Optional.of(propertyTypes)
            return this
        }

        Builder price(Range<BigDecimal> price) {
            this.price = Optional.of(price)
            return this
        }

        Builder bedrooms(Range<Integer> bedrooms) {
            this.bedrooms = Optional.of(bedrooms)
            return this
        }

        Builder bathrooms(Range<Integer> bathrooms) {
            this.bathrooms = Optional.of(bathrooms)
            return this
        }

        Builder parking(Range<Integer> parking) {
            this.parking = Optional.of(parking)
            return this
        }

        Builder includeSurrounding(boolean includeSurrounding) {
            this.includeSurrounding = Optional.of(includeSurrounding)
            return this
        }

        Builder excludeUnderOffer(boolean excludeUnderOffer) {
            this.excludeUnderOffer = Optional.of(excludeUnderOffer)
            return this
        }

        Builder establishType(EstablishType establishType) {
            this.establishType = Optional.of(establishType)
            return this
        }

        Builder landSize(Range<Integer> landSize) {
            this.landSize = Optional.of(landSize)
            return this
        }

        Builder features(Set<Feature> features) {
            this.features = Optional.of(features)
            return this
        }

        SearchCriteria build() {
            return new SearchCriteria(
                suburbs,
                propertyTypes,
                price,
                bedrooms,
                bathrooms,
                parking,
                includeSurrounding,
                excludeUnderOffer,
                establishType,
                landSize,
                features
            )
        }
    }
}
