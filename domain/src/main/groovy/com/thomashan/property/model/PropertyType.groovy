package com.thomashan.property.model

enum PropertyType {
    APARTMENT(null),
    HOUSE(null),
    LAND(null),
    RETIREMENT(null),
    TOWNHOUSE(null),

    DUPLEX(HOUSE),
    FREE_STANDING(HOUSE),
    NEW_HOME_DESIGNS(HOUSE),
    NEW_HOUSE_LAND(HOUSE),
    SEMI_DETACHED(HOUSE),
    TERRACE(HOUSE),
    VILLA(HOUSE),

    BLOCK_OF_UNITS(APARTMENT),
    PENT_HOUSE(APARTMENT),
    STUDIO(APARTMENT),
    APARTMENT_UNIT_FLAT(APARTMENT),
    NEW_APARTMENTS(APARTMENT),

    DEVELOPMENT_SITE(LAND),
    NEW_LAND(LAND),
    VACANT_LAND(LAND)

    final Optional<PropertyType> parent

    PropertyType(PropertyType parent) {
        this.parent = Optional.ofNullable(parent)
    }
}
