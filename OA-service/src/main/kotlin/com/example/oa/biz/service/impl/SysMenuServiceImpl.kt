package com.example.oa.biz.service.impl

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.SysMenuMapper
import com.example.oa.biz.service.SysMenuService
import com.example.oa.biz.service.SysRoleMenuService
import com.example.oa.exception.BizException
import com.example.oa.model.constant.MenuType
import com.example.oa.model.constant.StatusConstant
import com.example.oa.model.constant.SysUserConstant
import com.example.oa.model.system.*
import com.example.oa.model.vo.system.AssignMenuVo
import com.example.oa.model.vo.system.MetaVo
import com.example.oa.model.vo.system.RouterVo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SysMenuServiceImpl(
    private val sysRoleMenuService: SysRoleMenuService
) : SysMenuService, ServiceImpl<SysMenuMapper, SysMenu>() {
    override fun findNodes(): List<SysMenu> {
        val allMenus = this.list()
        return buildMenuTreeNodes(allMenus)
    }

    private fun buildMenuTreeNodes(allMenus: List<SysMenu>?): List<SysMenu> {
        //根菜单
        val parenMenus: List<SysMenu> =
            allMenus?.filter { it.parentId == 0L } ?: throw BizException(null, "根菜单不存在！")
        //设置根菜单的子菜单
        parenMenus.forEach {
            it.children = this@SysMenuServiceImpl.setChildrenNodes(it.id, allMenus)
        }
        return parenMenus
    }

    override fun removePreventCascadeById(id: Long): Boolean {
        val hasSubMenu = this.count(LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id)) > 0
        return if (hasSubMenu) false
        else this.removeById(id)
    }

    @Transactional
    override fun assignRoleMenu(assignMenuVo: AssignMenuVo?): Boolean {
        //根据角色id删除角色菜单关系表
        assignMenuVo?.let {
            it.roleId?.let { roleId ->
                sysRoleMenuService.remove(
                    LambdaQueryWrapper<SysRoleMenu>().eq(
                        SysRoleMenu::getRoleId,
                        roleId
                    )
                )
            } ?: return false
            //保存角色对应关系
            it.menuIdList?.let { menuIds ->
                menuIds.filterNotNull().forEach { menuId ->
                    val sysRoleMenu = SysRoleMenu().apply {
                        this.roleId = it.roleId
                        this.menuId = menuId
                    }
                    sysRoleMenuService.save(sysRoleMenu)
                }
            } ?: return false
        } ?: return false
        return true
    }

    override fun findMenuListByRoleId(roleId: Long): List<SysMenu> {
        //查询出所有的菜单，status 为启用状态
        val allEnabledMenuList =
            this.list(LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, StatusConstant.ENABLED))
        //查询角色关系表中 角色对应的菜单id集合
        val roleMenuIdList =
            sysRoleMenuService.list(LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId))
                ?.map {
                    it.menuId
                }
        //将角色拥有的菜单属性isSelected设置为true
        allEnabledMenuList.forEach {
            it.isSelect = roleMenuIdList?.contains(it.id) ?: false
        }
        //转换为树形结构
        return this.buildMenuTreeNodes(allEnabledMenuList)
    }

    override fun getRouterVoByUserId(userId: Long): List<RouterVo> {
        //判断是否为管理员，具有所有权限
        val userMenuList =
            if (userId == SysUserConstant.adminUserId) {
                //查询所有菜单
                this.list(
                    LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getStatus, StatusConstant.ENABLED)
                        .orderByAsc(SysMenu::getSortValue)
                )
            } else {
                this.baseMapper.getPermitMenuByUserId(userId)
            }
        //构建树形结构
        return this.buildRouterVoTreeNodes(this.buildMenuTreeNodes(userMenuList))
    }

    /**
     * 构建路由结构
     */
    private fun buildRouterVoTreeNodes(sysMenuList: List<SysMenu>): MutableList<RouterVo> {
        val resultRouterVoList: MutableList<RouterVo> = mutableListOf()
        sysMenuList.forEach {
            val routerVo = RouterVo()
            routerVo.apply {
                this.isHidden = false
                this.alwaysShow = false
                this.path = this@SysMenuServiceImpl.getRouterPath(it)
                this.component = it.component
                this.meta = MetaVo(it.name, it.icon)
                //构建隐藏子路由
                if (it.type == MenuType.hiddenRouter) {
                    it.children.filterNotNull()
                        .filter { sysMenu -> sysMenu.component != null && sysMenu.component.isNotBlank() }
                        .forEach { hiddenMenu ->
                            val hiddenRouterVo = RouterVo().apply {
                                this.isHidden = true
                                this.alwaysShow = false
                                this.path = this@SysMenuServiceImpl.getRouterPath(hiddenMenu)
                                this.component = hiddenMenu.component
                                this.meta = MetaVo(hiddenMenu.name, hiddenMenu.icon)
                            }
                            //加入到结果中
                            resultRouterVoList.add(hiddenRouterVo)
                        }
                }
                //构建普通子路由
                else {
                    if (it.children == null) {
                        //没有子路由
                        return@apply
                    }
                    if (it.children.isNotEmpty()) {
                        //设置子路由
                        routerVo.children = this@SysMenuServiceImpl.buildRouterVoTreeNodes(it.children)
                    }
                }
            }
            //加入到结果中
            resultRouterVoList.add(routerVo)
        }
        return resultRouterVoList
    }

    /**
     * 获取路由路径
     */
    private fun getRouterPath(sysMenu: SysMenu): String? {
        return if (sysMenu.parentId != 0L) sysMenu.path
        else "/${sysMenu.path}"
    }

    override fun getPermitActByUserId(userId: Long): List<String> {
        //判断是否为管理员
        val sysMenuList =
            if (userId == SysUserConstant.adminUserId)
                this.list(
                    LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getStatus, StatusConstant.ENABLED)
                )
            else
                this.baseMapper.getPermitMenuByUserId(userId)
        return sysMenuList.filterNotNull()
            .filter { it.type == MenuType.btn }
            .map { it.perms }
    }

    /**
     * 设置子节点
     */
    private fun setChildrenNodes(id: Long, allMenus: List<SysMenu>?): List<SysMenu>? {
        val subNodes = allMenus?.filter {
            it.parentId == id
        } ?: throw BizException(null, "菜单不存在")
        //递归出口
        if (subNodes.isEmpty()) return null

        subNodes.forEach {
            it.children = setChildrenNodes(it.id, allMenus)
        }
        return subNodes
    }
}