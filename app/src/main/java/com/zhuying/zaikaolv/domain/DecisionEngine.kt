package com.zhuying.zaikaolv.domain

import com.zhuying.zaikaolv.data.model.DecisionResult
import com.zhuying.zaikaolv.data.model.Question
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.data.model.VerdictTier
import kotlin.math.roundToInt

/**
 * 判断引擎抽象。v1 用 [LocalRuleEngine] 本地加权规则;
 * 后续接 AI 时实现同一接口替换即可,UI / 记录层无需改动。
 */
interface DecisionEngine {
    fun evaluate(
        mode: QuestionMode,
        questions: List<Question>,
        answerIndexes: List<Int>,
    ): DecisionResult
}

/**
 * 本地规则算法。大小问题各自独立实现(见 [evaluateSmall] / [evaluateBig]),均使用显式「是/不是」贡献分。
 * - 小问题:总分 -10..12;≥7 推荐,0..6 谨慎,≤-1 不太推荐;s4(highRisk)答「是」压制到谨慎。
 * - 大问题:总分 -30..35;≥22 推荐,5..21 谨慎,≤4 不太推荐;
 *   b10(highRisk)答「是」压制到谨慎;b11 与 b13(conditionGate)同为「不是」→ 直接不太推荐。
 * score(0..100)由各自的 total 线性映射,供 UI 匹配度展示。
 */
class LocalRuleEngine : DecisionEngine {

    override fun evaluate(
        mode: QuestionMode,
        questions: List<Question>,
        answerIndexes: List<Int>,
    ): DecisionResult {
        require(questions.size == answerIndexes.size) {
            "answers(${answerIndexes.size}) mismatch questions(${questions.size})"
        }
        // 每题「是=1 / 不是=0」的实际得分(既用于贡献计算,也用于 reasons)
        val chosen = answerIndexes.map { it.coerceIn(0, 1) }

        return when (mode) {
            QuestionMode.SMALL -> evaluateSmall(questions, chosen)
            QuestionMode.BIG -> evaluateBig(questions, chosen)
        }
    }

    // —— 小问题:整数贡献分制 ——
    private fun evaluateSmall(questions: List<Question>, chosen: List<Int>): DecisionResult {
        // 每题其选中答案对应的贡献分
        val contributions = questions.indices.map { i ->
            val q = questions[i]
            val s = chosen[i]
            val points = (if (s == 1) q.yesScore else q.noScore) ?: 0
            Triple(q, points.toFloat(), s)
        }
        val total = contributions.sumOf { it.second.toInt() }

        var tier = when {
            total >= SMALL_RECOMMEND_MIN -> VerdictTier.RECOMMEND
            total >= SMALL_CAUTION_MIN -> VerdictTier.CAUTION
            else -> VerdictTier.NOT_RECOMMEND
        }

        // 高风险项(s4)答「是」→ 最高只能给「建议谨慎考虑」
        val highRisk = questions.indices.any { i ->
            questions[i].highRisk && chosen[i] == 1
        }
        if (highRisk && tier == VerdictTier.RECOMMEND) {
            tier = VerdictTier.CAUTION
        }

        val reasonsDo = contributions
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(3)
            .map { it.first.dimension }
        val reasonsDont = contributions
            .filter { it.second < 0 }
            .sortedBy { it.second }
            .take(3)
            .map { it.first.dimension }

        // 总分 -10..12 → 0..100 线性映射,供匹配度展示
        val score = (((total - SMALL_MIN) / (SMALL_MAX - SMALL_MIN).toFloat()) * 100f)
            .roundToInt().coerceIn(0, 100)

        return DecisionResult(
            mode = QuestionMode.SMALL,
            answerIndexes = chosen,
            score = score,
            tier = tier,
            reasonsDo = reasonsDo,
            reasonsDont = reasonsDont,
            highRisk = highRisk,
        )
    }

    // —— 大问题:整数贡献分制(16 题,总分 -30..35)——
    private fun evaluateBig(questions: List<Question>, chosen: List<Int>): DecisionResult {
        val contributions = questions.indices.map { i ->
            val q = questions[i]
            val s = chosen[i]
            val points = (if (s == 1) q.yesScore else q.noScore) ?: 0
            Triple(q, points.toFloat(), s)
        }
        val total = contributions.sumOf { it.second.toInt() }

        var tier = when {
            total >= BIG_RECOMMEND_MIN -> VerdictTier.RECOMMEND
            total >= BIG_CAUTION_MIN -> VerdictTier.CAUTION
            else -> VerdictTier.NOT_RECOMMEND
        }

        // 高风险项(b10)答「是」→ 最高只能给「建议谨慎考虑」
        val highRisk = questions.indices.any { i ->
            questions[i].highRisk && chosen[i] == 1
        }
        // 条件不足:所有 conditionGate 题(b11、b13)都答「不是」→ 直接不太推荐
        val gateNotMet = questions.indices.any { questions[it].conditionGate }.let { hasGate ->
            hasGate && questions.indices
                .filter { questions[it].conditionGate }
                .all { chosen[it] == 0 }
        }

        when {
            gateNotMet -> tier = VerdictTier.NOT_RECOMMEND
            highRisk && tier == VerdictTier.RECOMMEND -> tier = VerdictTier.CAUTION
        }

        val reasonsDo = contributions
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(3)
            .map { it.first.dimension }
        val reasonsDont = contributions
            .filter { it.second < 0 }
            .sortedBy { it.second }
            .take(3)
            .map { it.first.dimension }

        // 总分 -30..35 → 0..100 线性映射,供匹配度展示
        val score = (((total - BIG_MIN) / (BIG_MAX - BIG_MIN).toFloat()) * 100f)
            .roundToInt().coerceIn(0, 100)

        return DecisionResult(
            mode = QuestionMode.BIG,
            answerIndexes = chosen,
            score = score,
            tier = tier,
            reasonsDo = reasonsDo,
            reasonsDont = reasonsDont,
            highRisk = highRisk,
            gateNotMet = gateNotMet,
        )
    }

    companion object {
        // 大问题分档阈值:≥22 推荐,5..21 谨慎,≤4 不太推荐
        const val BIG_RECOMMEND_MIN = 22
        const val BIG_CAUTION_MIN = 5
        const val BIG_MIN = -30
        const val BIG_MAX = 35

        // 小问题分档阈值:≥7 推荐,0..6 谨慎,≤-1 不太推荐
        const val SMALL_RECOMMEND_MIN = 7
        const val SMALL_CAUTION_MIN = 0
        const val SMALL_MIN = -10
        const val SMALL_MAX =12
    }
}
