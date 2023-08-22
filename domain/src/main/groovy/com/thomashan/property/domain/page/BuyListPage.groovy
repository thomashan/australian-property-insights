package com.thomashan.property.domain.page

import com.thomashan.browser.JsoupPage
import com.thomashan.property.model.SearchCriteria
import com.thomashan.property.model.buy.BuyDetails
import geb.Page
import geb.navigator.Navigator

import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

import static com.thomashan.property.model.PropertyType.RETIREMENT

class BuyListPage extends Page implements JsoupPage {
    static url = "https://www.domain.com.au/sale"

    String convertToPath(SearchCriteria searchCriteria) {
        return "/?${params(searchCriteria)}"
    }

    private static String params(SearchCriteria searchCriteria) {
        return [
            suburbs(searchCriteria),
            propertyTypes(searchCriteria),
            price(searchCriteria),
            bedrooms(searchCriteria),
            bathrooms(searchCriteria),
            parking(searchCriteria),
            surroundingSuburbs(searchCriteria),
            excludeUnderOffer(searchCriteria),
            establishType(searchCriteria),
            landSize(searchCriteria),
            features(searchCriteria),
            "sort=suburb-asc",
        ].stream()
            .filter { !it.empty }
            .collect(Collectors.joining("&"));
    }

    // in the format parkville-vic-3052
    private static String suburbs(SearchCriteria searchCriteria) {
        return "suburb=${searchCriteria.suburbs.stream().collect(Collectors.joining(","))}"
    }

    static String propertyTypes(SearchCriteria searchCriteria) {
        if (searchCriteria.propertyTypes.empty) {
            return ""
        }

        String propertyTypeString = searchCriteria.propertyTypes.stream()
            .flatMap(EnumSet::stream)
            .filter(Predicate.not(RETIREMENT::equals))
            .map(Enum::toString)
            .map(String::toLowerCase)
            .map(propertyType -> propertyType.replace("_", "-"))
            .collect(Collectors.joining(","))
        String propertyTypeQuery = propertyTypeString.empty ? "" : "ptype=" + propertyTypeString
        String retirementQuery = searchCriteria.propertyTypes.get().contains(RETIREMENT) ? "retirement=1" : ""
        return Stream.of(propertyTypeQuery, retirementQuery)
            .filter(Predicate.not(String::isEmpty))
            .collect(Collectors.joining("&"))
    }

    private static String bedrooms(SearchCriteria searchCriteria) {
        return searchCriteria.bedrooms.present ? "bedrooms=${searchCriteria.bedrooms.map { it.toString("0", "any") }.get()}" : ""
    }

    private static String bathrooms(SearchCriteria searchCriteria) {
        return searchCriteria.bathrooms.present ? "bathrooms=${searchCriteria.bathrooms.map { it.toString("0", "any") }.get()}" : ""
    }

    private static String parking(SearchCriteria searchCriteria) {
        return searchCriteria.parking.present ? "carspaces=${searchCriteria.parking.map { it.toString("0", "any") }.get()}" : ""
    }

    private static String price(SearchCriteria searchCriteria) {
        return searchCriteria.price.present ? "price=${searchCriteria.price.map { it.toString("0", "any") }.get()}" : ""
    }

    private static String surroundingSuburbs(SearchCriteria searchCriteria) {
        return searchCriteria.includeSurrounding.orElse(false) ? "" : "ssubs=0"
    }

    private static String excludeUnderOffer(SearchCriteria searchCriteria) {
        return searchCriteria.excludeUnderOffer.orElse(false) ? "excludeunderoffer=1" : ""
    }

    private static String establishType(SearchCriteria searchCriteria) {
        return searchCriteria.establishType.present ? "establishedtype=${searchCriteria.establishType.get().toString().toLowerCase()}" : ""
    }

    private static String landSize(SearchCriteria searchCriteria) {
        return searchCriteria.landSize.present ? "landsize=${searchCriteria.landSize.map { it.toString("0", "any") }.get()}" : ""
    }

    private static String features(SearchCriteria searchCriteria) {
        return searchCriteria.features.present ? "features=" +
            searchCriteria.features.stream()
                .flatMap(Collection::stream)
                .map(Object::toString)
                .map { it.replace("_", "") }
                .map(String::toLowerCase)
                .collect(Collectors.joining(",")) : ""
    }

    static content = {
        list {
            $("ul[data-testid='results']").$("li[data-testid^='listing'],li[data-testid^='topspot']").moduleList(BuyListSummaryModule)
        }
        pageNumber { $("span[data-testid='paginator-page-button']").text() as Integer }
        pageEnd {
            Navigator navigator = $("a[data-testid='paginator-page-button']")
            return !navigator.empty ? navigator.last().text() as Integer : 1
        }
    }

    List<BuyDetails> getBuyDetails() {
        return list.collect {
            new BuyDetails(price: it.price,
                address: it.addressLine1,
                suburb: it.suburb,
                state: it.state,
                postcode: it.postcode,
                bedrooms: it.bedrooms,
                bathrooms: it.bathrooms,
                parking: it.parking
            )
        }
    }
}
