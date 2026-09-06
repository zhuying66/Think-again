package com.zhuying.zaikaolv.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.model.DecisionResult
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.data.questions.QuestionBank
import com.zhuying.zaikaolv.data.room.DecisionRecordEntity
import com.zhuying.zaikaolv.domain.DecisionEngine
import kotlinx.coroutines.launch

class ResultViewModel(
    private val mode: QuestionMode,
    answersCsv: String,
    private val engine: DecisionEngine,
    private val dao: com.zhuying.zaikaolv.data.room.DecisionDao,
) : ViewModel() {

    private val questions = QuestionBank.questionsOf(mode)
    private val answers: List<Int> = answersCsv.split(",").filter { it.isNotBlank() }.map { it.toInt() }

    private val _result: DecisionResult = engine.evaluate(mode, questions, answers)
    val result: DecisionResult = _result

    init {
        // 结果产生即写入历史记录
        viewModelScope.launch {
            dao.insert(
                DecisionRecordEntity(
                    modeKey = mode.key,
                    answersCsv = answersCsv,
                    score = _result.score,
                    tierName = _result.tier.name,
                    createdAt = System.currentTimeMillis(),
                )
            )
        }
    }

    companion object {
        fun factory(
            mode: QuestionMode,
            answersCsv: String,
            container: AppContainer,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ResultViewModel(mode, answersCsv, container.decisionEngine, container.database.decisionDao())
            }
        }
    }
}
