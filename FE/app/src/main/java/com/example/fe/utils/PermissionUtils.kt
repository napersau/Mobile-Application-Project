package com.example.fe.utils

import android.view.View
import com.example.fe.model.UserSession

object PermissionUtils {
    
    /**
     * Hiển thị view nếu user có quyền, ẩn nếu không có quyền
     */
    fun showIfCanCreate(view: View) {
        view.visibility = if (UserSession.canCreate()) View.VISIBLE else View.GONE
    }
    
    fun showIfCanEdit(view: View) {
        view.visibility = if (UserSession.canEdit()) View.VISIBLE else View.GONE
    }
    
    fun showIfCanDelete(view: View) {
        view.visibility = if (UserSession.canDelete()) View.VISIBLE else View.GONE
    }
    
    fun showIfIsAdmin(view: View) {
        view.visibility = if (UserSession.isAdmin()) View.VISIBLE else View.GONE
    }
    
    /**
     * Enable view nếu user có quyền, disable nếu không có quyền
     */
    fun enableIfCanCreate(view: View) {
        view.isEnabled = UserSession.canCreate()
    }
    
    fun enableIfCanEdit(view: View) {
        view.isEnabled = UserSession.canEdit()
    }
    
    fun enableIfCanDelete(view: View) {
        view.isEnabled = UserSession.canDelete()
    }
    
    /**
     * Kiểm tra permission và trả về thông báo lỗi nếu không có quyền
     */
    fun checkCreatePermission(): String? {
        return if (!UserSession.canCreate()) "Bạn không có quyền tạo tài liệu" else null
    }
    
    fun checkEditPermission(): String? {
        return if (!UserSession.canEdit()) "Bạn không có quyền chỉnh sửa tài liệu" else null
    }
    
    fun checkDeletePermission(): String? {
        return if (!UserSession.canDelete()) "Bạn không có quyền xóa tài liệu" else null
    }
}

/**
 * Extension functions để sử dụng dễ dàng hơn
 */
fun View.showIfCanCreate() = PermissionUtils.showIfCanCreate(this)
fun View.showIfCanEdit() = PermissionUtils.showIfCanEdit(this)
fun View.showIfCanDelete() = PermissionUtils.showIfCanDelete(this)
fun View.showIfIsAdmin() = PermissionUtils.showIfIsAdmin(this)
fun View.enableIfCanCreate() = PermissionUtils.enableIfCanCreate(this)
fun View.enableIfCanEdit() = PermissionUtils.enableIfCanEdit(this)
fun View.enableIfCanDelete() = PermissionUtils.enableIfCanDelete(this)