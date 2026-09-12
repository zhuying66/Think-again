package com.zhuying.zaikaolv.ui.screens.classify

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.data.questions.ScaleQuestion
import com.zhuying.zaikaolv.data.questions.ScaleQuestions
import com.zhuying.zaikaolv.domain.ScaleClassifier

/**
 * 「问题规模」分类问答的状态。5 题、每题 1~5 分;
 * 选中答案后需手动点「下一题」前进(与小/大问题问卷一致),
 * 全部答完后进入分类结果页,给出提示语并可进入对应问卷。
 */
class ClassifyViewModel : ViewModel() {

    val questions: List<ScaleQuestion> = ScaleQuestions.all
    val total: Int = questions.size

    /** 每题选中的分值 1..5,null = 未选 */
    val answers = mutableStateListOf<Int?>().apply { repeat(total) { add(null) } }

    /** 当前题目索引 */
    var currentIndex by mutableStateOf(0)
        private set

    /** 是否已进入分类结果页 */
    var showResult by mutableStateOf(false)
        private set

    val currentAnswer: Int?
        get() = answers.getOrNull(currentIndex)

    /** 当前题是否已作答(未作答则「下一题」不可点) */
    val canGoNext: Boolean
        get() = currentAnswer != null

    val answeredCount: Int
        get() = answers.count { it != null }

    val progress: Float
        get() = if (total == 0) 0f else answeredCount.toFloat() / total

    /** 分类判定出的问题规模(仅在全部答完后有意义)。 */
    val classifiedMode: QuestionMode
        get() = ScaleClassifier.classifyByAnswers(answers.filterNotNull())

    /** 结果页提示语 */
    val resultHint: String
        get() = ScaleClassifier.hintFor(classifiedMode)

    fun select(questionIndex: Int, score: Int) {
        if (questionIndex in answers.indices) answers[questionIndex] = score
    }

    /**
     * 推进到下一题(答完最后一题则进入结果页)。
     * 传入 fromIndex 以避免快速连点时重复推进。
     */
    fun advance(fromIndex: Int) {
        if (showResult || currentIndex != fromIndex) return
        if (currentIndex < total - 1) {
            currentIndex += 1
        } else {
            showResult = true
        }
    }

    /** 返回上一题;已在第一题则返回 false(由调用方决定退出)。 */
    fun goBack(): Boolean {
        if (showResult) {
            // 从结果页退回最后一题重新作答
            showResult = false
            currentIndex = total - 1
            return true
        }
        if (currentIndex > 0) {
            currentIndex -= 1
            return true
        }
        return false
    }
}
