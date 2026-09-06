package com.zhuying.zaikaolv.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decision_records")
data class DecisionRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val modeKey: String,      // "small" | "big"
    val answersCsv: String,   // 每题档位索引 0..3,逗号连接
    val score: Int,           // 0..100 倾向做
    val tierName: String,     // VerdictTier.name
    val createdAt: Long,
)
