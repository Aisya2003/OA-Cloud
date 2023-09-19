package com.example.oa.biz.config

import org.activiti.engine.HistoryService
import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.RepositoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.impl.history.HistoryLevel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class ProcessEngineConfig(private val dataSource: DataSource) {
    @Bean
    fun processEngine(): ProcessEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
        .setDataSource(dataSource)
        .setHistoryLevel(HistoryLevel.FULL)
        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
//        .setDatabaseSchema("true")
        .buildProcessEngine()

    @Bean
    fun repositoryService(): RepositoryService = processEngine().repositoryService

    @Bean
    fun runtimeService(): RuntimeService = processEngine().runtimeService

    @Bean
    fun taskService(): TaskService = processEngine().taskService

    @Bean
    fun historyService(): HistoryService = processEngine().historyService

}