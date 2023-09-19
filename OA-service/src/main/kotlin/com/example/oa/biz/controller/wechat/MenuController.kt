package com.example.oa.biz.controller.wechat


import com.example.oa.biz.service.wechat.MenuService
import com.example.oa.model.vo.wechat.MenuVo
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.example.oa.model.dto.Result
import com.example.oa.model.wechat.Menu
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("/admin/wechat/menu")
class MenuController(
    private val menuService: MenuService
) {
    /**
     * 获取菜单列表
     */
    @GetMapping("/findMenuInfo")
    @PreAuthorize("hasAuthority('btn.menu.list')")
    fun getMenuInfo(): Result<List<MenuVo>> {
        return Result.ok(menuService.getMenuTreeNodes())
    }

    /**
     * 同步菜单至微信
     */
    @GetMapping("/syncMenu")
    @PreAuthorize("hasAuthority('btn.menu.syncMenu')")
    fun synchronizeToWeChat(): Result<Unit> {
        return if (menuService.synchronize()) Result.ok()
        else Result.fail()
    }

    /**
     * 删除微信的菜单
     */
    @DeleteMapping("/removeMenu")
    fun removeWechatMenu(): Result<Unit> {
        return if (menuService.removeWxMenu()) Result.ok()
        else Result.fail()
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('btn.menu.list')")
    fun get(@PathVariable("id") id: Long): Result<Menu> {
        return Result.ok(menuService.getById(id))
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('btn.menu.add')")
    fun save(@RequestBody menu: Menu): Result<Unit> {
        return if (menuService.save(menu)) Result.ok()
        else Result.fail()
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('btn.menu.update')")
    fun update(@RequestBody menu: Menu): Result<Unit> {
        return if (menuService.updateById(menu)) Result.ok()
        else Result.fail()
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('btn.menu.remove')")
    fun remove(@PathVariable("id") id: Long): Result<Unit> {
        return if (menuService.removeById(id)) Result.ok()
        else Result.fail()
    }
}