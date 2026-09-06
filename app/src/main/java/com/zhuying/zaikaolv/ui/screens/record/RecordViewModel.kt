package com.zhuying.zaikaolv.ui.screens.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.room.DecisionRecordEntity
import kotlinx.coroutines.flow.Flow

class RecordViewModel(container: AppContainer) : ViewModel() {
    val records: Flow<List<DecisionRecordEntity>> = container.database.decisionDao().observeAll()

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { RecordViewModel(container) }
        }
    }
}
