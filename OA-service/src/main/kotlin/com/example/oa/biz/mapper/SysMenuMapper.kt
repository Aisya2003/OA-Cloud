package com.example.oa.biz.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.example.oa.model.system.SysMenu
import com.example.oa.model.system.SysRole
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

interface SysMenuMapper : BaseMapper<SysMenu> {
    @Select(
        "\n" +
                "        select distinct m.id,\n" +
                "                        m.parent_id,\n" +
                "                        m.name,\n" +
                "                        m.type,\n" +
                "                        m.path,\n" +
                "                        m.component,\n" +
                "                        m.perms,\n" +
                "                        m.icon,\n" +
                "                        m.sort_value,\n" +
                "                        m.status,\n" +
                "                        m.create_time,\n" +
                "                        m.update_time,\n" +
                "                        m.is_deleted\n" +
                "        from sys_menu m\n" +
                "                 inner join sys_role_menu rm on rm.menu_id = m.id\n" +
                "                 inner join sys_user_role ur on ur.role_id = rm.role_id\n" +
                "        where ur.user_id = #{userId}\n" +
                "          and m.status = 1\n" +
                "          and rm.is_deleted = 0\n" +
                "          and ur.is_deleted = 0\n" +
                "          and m.is_deleted = 0"
    )
    fun getPermitMenuByUserId(@Param("userId") userId: Long): List<SysMenu>
}