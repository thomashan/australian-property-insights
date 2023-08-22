package com.thomashan.property.domain.page

import geb.Module
import geb.navigator.Navigator

import java.util.regex.Matcher

class BuyListSummaryModule extends Module {
    private static final EMPTY = ""
    private static final COMMA = ","
    private static final BEDROOMS = "Beds"
    private static final BATHROOMS = "Bath"
    private static final PARKING = "Parking"

    static content = {
        price {
            Matcher matcher = $("p[data-testid='listing-card-price']").text() =~ /(\d+\.?\d+)/
            return matcher.count > 0 ? Optional.of(matcher[0][0] as BigDecimal) : Optional.empty()
        }
        addressLine1 {
            String address1 = $("span[data-testid='address-line1']").text()
            return address1 ? address1?.strip() - COMMA : EMPTY
        }
        suburb { $("span[itemprop='addressLocality']").text() }
        state { $("span[itemprop='addressRegion']").text() }
        postcode { $("span[itemprop='postalCode']").text() }
        bedrooms {
            Navigator bedroomElements = $("span[data-testid='property-features-text']", text: contains(BEDROOMS))
            return getValue(bedroomElements)
        }
        bathrooms {
            Navigator bathroomElements = $("span[data-testid='property-features-text']", text: contains(BATHROOMS))
            return getValue(bathroomElements)
        }
        parking {
            Navigator parkingElements = $("span[data-testid='property-features-text']", text: contains(PARKING))
            return getValue(parkingElements)
        }

        addressLine2 { $("span.address-line2").text() }
    }

    static Integer getValue(String text) {
        Matcher matcher = text =~ /.*(\d).*/
        if (matcher.count == 0) {
            return 0
        }

        Integer size = matcher[0].size()

        return matcher[0][size - 1] as Integer
    }

    static Integer getValue(Navigator element) {
        if (element.isEmpty()) {
            return 0
        }

        return getValue(element.parent().text())
    }
}
