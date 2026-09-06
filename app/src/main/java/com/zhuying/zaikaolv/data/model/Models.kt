package com.zhuying.zaikaolv.data.model

/** 问题分量:小问题快答 / 大问题详析 */
enum class QuestionMode(val key: String, val title: String, val subtitle: String) {
    SMALL("small", "小问题", "日常琐事 · 6 道题快速判断"),
    BIG("big", "大问题", "重要决定 · 16 道题详细分析"),
    ;

    companion object {
        fun fromKey(key: String): QuestionMode = entries.first { it.key == key }
    }
}

/**
 * 一道判断题。
 * 大问题(大)仍用 dir/weight 归一化计分;小问题(小)用显式的"是/不是"贡献分(yesScore/noScore)。
 * 选"是"记 yesScore,选"不是"记 noScore。
 */
data class Question(
    val id: String,
    val text: String,
    val dimension: String,
    val dir: Int = 1,
    val weight: Int = 1,
    val yesScore: Int? = null,       // 分数制:选「是」的贡献分
    val noScore: Int? = null,        // 分数制:选「不是」的贡献分
    val highRisk: Boolean = false,    // 高风险项(答「是」会压制结论到谨慎)
    val conditionGate: Boolean = false, // 条件项(配合其他条件项同选「不是」→ 直接不太推荐)
)

/** 统一答案档位(2 级),索引即得分 0..1(1=是/认同,0=不是/不认同) */
val SCALE_OPTIONS = listOf("不是", "是")

/** 三档结论 */
enum class VerdictTier(val label: String) {
    RECOMMEND("推荐去做"),
    CAUTION("建议谨慎考虑"),
    NOT_RECOMMEND("不太推荐"),
}

/**
 * 一次判断的完整结果。score = 0..100 的「倾向做」分;
 * doLeanPct = 对最终结论而言的推荐度(给 UI 进度条用)。
 */
data class DecisionResult(
    val mode: QuestionMode,
    val answerIndexes: List<Int>,
    val score: Int, // 0..100 倾向做
    val tier: VerdictTier,
    val reasonsDo: List<String>, // 支持「做」的维度
    val reasonsDont: List<String>, // 支持「不做」的维度
    val highRisk: Boolean = false,   // 是否触发了高风险项(答「是」)
    val gateNotMet: Boolean = false, // 是否触发了「条件不足」(条件项都答「不是」)
) {
    /** 界面展示用:推荐度百分比(朝向 tier 的那一侧) */
    val doLeanPct: Int
        get() = when (tier) {
            VerdictTier.RECOMMEND -> score
            VerdictTier.NOT_RECOMMEND -> 100 - score
            VerdictTier.CAUTION -> score // 中间态就展示倾向做的原始百分比
        }
}
