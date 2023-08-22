package com.thomashan.property

import com.thomashan.property.model.SearchCriteria
import com.thomashan.property.model.buy.BuyDetails

trait BuyRepository {
    abstract List<BuyDetails> findAll(SearchCriteria searchCriteria)
}
