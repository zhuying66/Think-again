package com.zhuying.zaikaolv.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decision_records")
data class DecisionRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val modeKey: String,      // "small" | "big"
    val answersCsv: String,   // 每题档位索引 0..1,逗号连接
    val score: Int,           // 0..100 倾向做
    val tierName: String,     // VerdictTier.name
    val createdAt: Long,
    val title: String? = null, // 用户自定义的记录名称(为空则回退显示小问题/大问题)
)
