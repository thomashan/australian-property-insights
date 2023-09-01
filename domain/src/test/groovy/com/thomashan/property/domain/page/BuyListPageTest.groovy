package com.thomashan.property.domain.page

import com.thomashan.browser.JsoupBrowser
import com.thomashan.property.model.PropertyType
import com.thomashan.property.model.SearchCriteria
import com.thomashan.property.model.buy.BuyDetails
import geb.Browser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

import static com.thomashan.property.model.PropertyType.HOUSE
import static com.thomashan.property.model.PropertyType.RETIREMENT
import static java.nio.charset.StandardCharsets.UTF_8

class BuyListPageTest {
    private static final Set<String> EXPECTED_PROPERTY_TYPES = [
        "house", "duplex", "free-standing", "new-house-land", "semi-detached", "terrace", "villa", "new-home-designs",
        "apartment", "apartment-unit-flat", "block-of-units", "pent-house", "studio", "new-apartments",
        "townhouse",
        "land", "development-site", "new-land", "vacant-land"
    ]

    @ParameterizedTest
    @EnumSource(PropertyType)
    void testPropertyTypes(PropertyType propertyType) {
        SearchCriteria searchCriteria = new SearchCriteria.Builder([] as Set)
            .propertyTypes(EnumSet.copyOf([propertyType]))
            .build()

        String queryPropertyType = BuyListPage.propertyTypes(searchCriteria)
        String propertyTypeString = propertyType.toString().toLowerCase().replace("_", "-")
        if (propertyType != RETIREMENT) {
            assert EXPECTED_PROPERTY_TYPES.contains(propertyTypeString)
            assert "ptype=" + propertyTypeString == queryPropertyType
        } else {
            assert "retirement=1" == queryPropertyType
        }

        println(queryPropertyType)
    }

    @Test
    void testPropertyTypesPropertyTypeWithRetirement() {
        SearchCriteria searchCriteria = new SearchCriteria.Builder([] as Set)
            .propertyTypes(EnumSet.copyOf([HOUSE, RETIREMENT]))
            .build()
        assert "ptype=house&retirement=1" == BuyListPage.propertyTypes(searchCriteria)
    }

    @Disabled("disable for now as it's driving a real browser")
    @Test
    void testBrowser() {
        SearchCriteria searchCriteria = new SearchCriteria.Builder(["parkville-vic-3052"] as Set<String>)
            .build()
        Browser.drive {
            to BuyListPage, searchCriteria
            BuyListPage buyListPage = (BuyListPage) page
            List<BuyDetails> buyDetails = buyListPage.buyDetails
            assert buyDetails != null
        }
    }

    @Disabled
    @Test
    void testHttpClient() {
        SearchCriteria searchCriteria = new SearchCriteria.Builder(["parkville-vic-3052"] as Set<String>)
            .build()
        HttpClient httpClient = HttpClient.newHttpClient()
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(BuyListPage.url + BuyListPage.convertToPath(searchCriteria)))
            .build()
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(UTF_8))

        assert httpResponse != null
    }

    @Disabled
    @Test
    void testJsoupBrowser() {
        SearchCriteria searchCriteria = new SearchCriteria.Builder(["parkville-vic-3052"] as Set<String>)
            .build()
        JsoupBrowser.drive {
            to BuyListPage, searchCriteria
            println(page)
        }
//        Document document = Jsoup.connect(BuyListPage.url + BuyListPage.convertToPath(searchCriteria)).get()
//        assert document != null
    }

    @Test
    void testJsoup() {
        SearchCriteria searchCriteria = new SearchCriteria.Builder(["parkville-vic-3052"] as Set<String>)
            .build()
        Document document = Jsoup.connect(BuyListPage.url + BuyListPage.convertToPath(searchCriteria)).get()
        assert document != null
    }
}
