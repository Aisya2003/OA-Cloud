package com.example.oa.biz

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper
import com.example.oa.biz.service.SysMenuService
import com.example.oa.biz.service.SysRoleService
import com.example.oa.model.system.SysMenu
import com.example.oa.model.system.SysRole
import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.ProcessEngines
import org.activiti.engine.RepositoryService
import org.activiti.spring.boot.ActivitiProperties
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import javax.sql.DataSource

@SpringBootTest
class ServiceApplicationTest {
    @Autowired
    lateinit var sysRoleService: SysRoleService

    @Autowired
    lateinit var sysMenuService: SysMenuService

    @Test
    fun testService() {
        println(sysRoleService.list())

        println(sysRoleService.getOne(QueryWrapper<SysRole>().eq("id", "9")))
    }

    @Test
    fun testTreeNodes() {
        val sysMenu = SysMenu()
        sysMenu.createTime = LocalDateTime.now()
        sysMenu.updateTime = LocalDateTime.now()
        sysMenuService.update(sysMenu, LambdaUpdateWrapper<SysMenu>().ne(SysMenu::getId, "1029"))
    }

    @Test
    fun testPermit() {
        sysMenuService.getRouterVoByUserId(1L).forEach {
            println("Menu--------->${it.toString()}")
        }
    }

    @Autowired
    lateinit var mybatisPlusProperties: MybatisPlusProperties

    @Test
    fun testMapper() {
        sysMenuService.getRouterVoByUserId(13L).forEach {
            println("router->${it}")
        }

    }

    @Autowired
    lateinit var processEngine: ProcessEngine

    @Test
    fun testActiviti() {
        //部署
        val deploy = processEngine.repositoryService.createDeployment()
            .addClasspathResource("process/leave.bpmn20.xml")
            .addClasspathResource("process/leave.png")
            .name("请假流程")
            .deploy()
        deploy.let {
            println(it.id)
            println(it.name)
        }
    }

}