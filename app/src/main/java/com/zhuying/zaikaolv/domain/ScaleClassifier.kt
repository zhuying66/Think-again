package com.zhuying.zaikaolv.domain

import com.zhuying.zaikaolv.data.model.QuestionMode

/**
 * 问题规模分类器(纯本地、确定性,不接入任何 AI / API / 网络)。
 *
 * 依据 5 道分类题的分值求和,把「拿不准的事」自动归入小问题或大问题:
 *
 *   totalScore = Q1 + Q2 + Q3 + Q4 + Q5      (理论范围 5 ~ 25)
 *   · 5  ~ 14 分 → 小问题
 *   · 15 ~ 25 分 → 大问题
 *
 * 边界固定:≤14 一律小问题,≥15 一律大问题,保证同一组答案永远得到同一判定。
 *
 * 本类与 [DecisionEngine](最终结果计算)完全独立,互不影响。
 */
object ScaleClassifier {

    /** 总分下限 */
    const val MIN_TOTAL = 5

    /** 总分上限 */
    const val MAX_TOTAL = 25

    /** 小问题上界:≤ 该值即小问题 */
    const val SMALL_MAX = 14

    /** 计算总分。answers 为每题的 1..5 分。 */
    fun totalScore(answers: List<Int>): Int = answers.sum()

    /** 按总分判定问题规模。 */
    fun classify(total: Int): QuestionMode =
        if (total <= SMALL_MAX) QuestionMode.SMALL else QuestionMode.BIG

    /** 便捷方法:直接从答案得到规模。 */
    fun classifyByAnswers(answers: List<Int>): QuestionMode = classify(totalScore(answers))

    /** 分类完成后的提示语(不向用户展示分数)。 */
    fun hintFor(mode: QuestionMode): String = when (mode) {
        QuestionMode.SMALL -> "这件事看起来属于比较轻量的决定"
        QuestionMode.BIG -> "这件事可能会对你产生比较明显的影响"
    }
}
