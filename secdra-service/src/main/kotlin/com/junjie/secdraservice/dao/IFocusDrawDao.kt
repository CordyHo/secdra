package com.junjie.secdraservice.dao

import com.junjie.secdraservice.model.FocusDraw
import org.springframework.data.jpa.repository.JpaRepository

interface IFocusDrawDao : JpaRepository<FocusDraw, String> {
    fun deleteByUserIdAndDrawId(userId: String, drawId: String)

    fun findFirstByUserIdAndDrawId(userId: String, drawId: String): FocusDraw
}