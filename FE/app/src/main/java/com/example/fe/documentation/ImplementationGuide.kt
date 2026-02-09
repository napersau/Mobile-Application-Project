/**
 * DOCUMENT MANAGEMENT SYSTEM WITH ROLE-BASED ACCESS CONTROL
 * =========================================================
 * 
 * Hệ thống quản lý tài liệu với phân quyền User/Admin cho Android
 * 
 * FEATURES IMPLEMENTED:
 * ====================
 * 
 * 1. USER PERMISSIONS:
 *    - Đọc tài liệu (xem chi tiết, nội dung)
 *    - Xem danh sách tài liệu theo category
 *    - Chia sẻ tài liệu
 *    
 * 2. ADMIN PERMISSIONS:  
 *    - Tất cả quyền của User
 *    - Tạo tài liệu mới
 *    - Chỉnh sửa tài liệu
 *    - Xóa tài liệu
 *    - Quản lý trạng thái Publish/Draft
 *
 * FILES CREATED:
 * ==============
 * 
 * Models:
 * - ApiResponse.kt (đã có sẵn) - Generic API response wrapper
 * - DocCategory.kt - Enum cho categories tài liệu
 * - DocumentResponse.kt - Response model từ backend
 * - DocumentRequest.kt - Request model với validation
 * - User.kt - User model và UserSession singleton
 * 
 * Network & Repository:
 * - DocumentApiService.kt - Retrofit interface cho API calls
 * - NetworkManager.kt - Retrofit configuration với Gson converter
 * - DocumentRepository.kt - Business logic với permission checks
 * 
 * ViewModels:
 * - DocumentViewModel.kt - UI state management với permission reactive updates
 * 
 * UI Activities:
 * - DocumentListActivity.kt - Danh sách tài liệu với admin controls
 * - DocumentDetailActivity.kt - Chi tiết tài liệu (đọc) với optional admin actions
 * - DocumentEditorActivity.kt - Tạo/sửa tài liệu (Admin only)
 * 
 * Adapters:
 * - DocumentAdapter.kt - RecyclerView adapter với conditional admin buttons
 * 
 * Utils:
 * - PermissionUtils.kt - Helper functions và extensions cho permission checks
 * 
 * USAGE GUIDE:
 * ============
 * 
 * 1. SETUP NETWORK:
 *    - Update BASE_URL trong NetworkManager.kt
 *    - Add dependencies trong build.gradle:
 *      implementation 'com.squareup.retrofit2:retrofit:2.9.0'
 *      implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
 *      implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
 * 
 * 2. LOGIN FLOW:
 *    // Khi user login thành công
 *    val user = User(
 *        id = response.userId,
 *        username = response.username,
 *        email = response.email,
 *        role = if (response.isAdmin) UserRole.ADMIN else UserRole.USER,
 *        token = response.token
 *    )
 *    UserSession.login(user)
 * 
 * 3. PERMISSION CHECKS:
 *    // Trong code
 *    if (UserSession.canCreate()) {
 *        // Show create button
 *    }
 *    
 *    // Với utility extensions
 *    createButton.showIfCanCreate()
 *    editMenuItem.enableIfCanEdit()
 * 
 * 4. NAVIGATION:
 *    // Đọc tài liệu (User & Admin)
 *    startActivity(Intent(this, DocumentListActivity::class.java))
 *    
 *    // Tạo tài liệu (Admin only)
 *    if (UserSession.canCreate()) {
 *        startActivity(Intent(this, DocumentEditorActivity::class.java))
 *    }
 * 
 * LAYOUT FILES NEEDED:
 * ===================
 * 
 * activities:
 * - activity_document_list.xml
 * - activity_document_detail.xml  
 * - activity_document_editor.xml
 * 
 * items:
 * - item_document.xml
 * 
 * menus:
 * - menu_document_list.xml
 * - menu_document_detail.xml
 * - menu_document_editor.xml
 * 
 * SECURITY NOTES:
 * ===============
 * 
 * 1. Client-side permission checks chỉ để UX, không phải security
 * 2. Backend API phải validate permissions cho mọi request
 * 3. Token authentication được handle qua Authorization header
 * 4. UserSession lưu trạng thái trong memory, cần implement persistent storage nếu cần
 * 
 * EXAMPLE IMPLEMENTATION:
 * =======================
 * 
 * // Trong Activity
 * class MainActivity : AppCompatActivity() {
 *     private val viewModel: DocumentViewModel by viewModels {
 *         DocumentViewModelFactory(DocumentRepository(NetworkManager.documentApiService))
 *     }
 * 
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         
 *         // Simulate login
 *         val adminUser = User(1, "admin", "admin@app.com", UserRole.ADMIN, "token123")
 *         UserSession.login(adminUser)
 *         
 *         // Start document list
 *         startActivity(Intent(this, DocumentListActivity::class.java))
 *     }
 * }
 * 
 * API ENDPOINTS MAPPING:
 * =====================
 * 
 * GET /api/v1/documents/category?category={category} -> getDocumentsByCategory() [USER & ADMIN]
 * GET /api/v1/documents/{id} -> getDocumentById() [USER & ADMIN]
 * POST /api/v1/documents -> createDocument() [ADMIN ONLY]
 * PUT /api/v1/documents/{id} -> updateDocument() [ADMIN ONLY]  
 * DELETE /api/v1/documents/{id} -> deleteDocument() [ADMIN ONLY]
 */

package com.example.fe.documentation