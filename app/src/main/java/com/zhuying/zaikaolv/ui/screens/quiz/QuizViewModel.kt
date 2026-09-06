package com.zhuying.zaikaolv.ui.screens.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zhuying.zaikaolv.data.model.Question
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.data.questions.QuestionBank

class QuizViewModel(mode: QuestionMode) : ViewModel() {

    val questions: List<Question> = QuestionBank.questionsOf(mode)
    val total: Int = questions.size

    /** 每题选中的档位索引 0..4,null=未选 */
    val answers = mutableStateListOf<Int?>().apply { repeat(total) { add(null) } }

    /** 当前展示的题目索引(显式,由「下一题」推进) */
    var currentIndex by mutableStateOf(0)

    val canGoNext: Boolean
        get() = currentIndex < total && answers[currentIndex] != null

    val answeredCount: Int
        get() = answers.count { it != null }

    val progress: Float
        get() = if (total == 0) 0f else answeredCount.toFloat() / total

    val allAnswered: Boolean
        get() = answers.all { it != null }

    fun select(questionIndex: Int, scoreIdx: Int) {
        answers[questionIndex] = scoreIdx
    }

    fun selectNext() {
        if (!canGoNext) return
        if (currentIndex < total - 1) {
            currentIndex += 1
        }
    }

    fun goBack(): Boolean {
        if (currentIndex > 0) {
            currentIndex -= 1
            return true
        }
        return false
    }

    fun restart() {
        for (i in 0 until total) answers[i] = null
        currentIndex = 0
    }

    companion object {
        fun factory(mode: QuestionMode): ViewModelProvider.Factory = viewModelFactory {
            initializer { QuizViewModel(mode) }
        }
    }
}
