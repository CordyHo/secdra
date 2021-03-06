package com.junjie.secdraservice.service

import com.junjie.secdraservice.document.DrawDocument
import org.elasticsearch.search.aggregations.Aggregation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface DrawDocumentService {
    fun get(id: String): DrawDocument

    fun paging(pageable: Pageable, tagList: List<String>?, precise: Boolean, name: String?, startDate: Date?, endDate: Date?, userId: String?, self: Boolean): Page<DrawDocument>

    fun countByTag(tag: String): Long

    fun getFirstByTag(tag: String): DrawDocument

    fun listTagTop30(): Aggregation?

    fun save(draw: DrawDocument): DrawDocument
}