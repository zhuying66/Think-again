package com.zhuying.zaikaolv.ui.screens.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.room.DecisionDao
import com.zhuying.zaikaolv.data.room.DecisionRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecordViewModel(container: AppContainer) : ViewModel() {

    private val dao: DecisionDao = container.database.decisionDao()

    val records: Flow<List<DecisionRecordEntity>> = dao.observeAll()

    /** 修改某条记录的名称;传入空白则清除自定义名称(回退显示小问题/大问题) */
    fun rename(record: DecisionRecordEntity, title: String) {
        val normalized = title.trim().ifBlank { null }
        viewModelScope.launch { dao.updateTitle(record.id, normalized) }
    }

    /** 删除单条记录 */
    fun delete(record: DecisionRecordEntity) {
        viewModelScope.launch { dao.deleteById(record.id) }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { RecordViewModel(container) }
        }
    }
}
