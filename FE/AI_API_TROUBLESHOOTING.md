# AI API Troubleshooting Guide

## Vấn đề thường gặp khi sử dụng AI Features

### 1. SocketTimeoutException (Timeout Error)

**Triệu chứng:**
```
java.net.SocketTimeoutException: timeout
okhttp.OkHttpClient: <-- HTTP FAILED: java.net.SocketTimeoutException: timeout
```

**Nguyên nhân:**
- AI API của backend gọi ChatGPT API, có thể mất 30-60 giây hoặc lâu hơn
- Timeout mặc định của OkHttp quá ngắn (30 giây)

**Giải pháp đã áp dụng:**
- Tăng `readTimeout` lên 120 giây trong `RetrofitClient.kt`
- Tăng `connectTimeout` lên 60 giây
- Tăng `writeTimeout` lên 60 giây

**Code trong RetrofitClient.kt:**
```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor(context.applicationContext))
    .addInterceptor(loggingInterceptor)
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(120, TimeUnit.SECONDS)  // Increased for AI API
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()
```

**UX Improvements:**
- Hiển thị Toast thông báo "AI đang suy nghĩ... Có thể mất 30-60 giây"
- Hiển thị ProgressBar khi loading
- Disable nút gửi khi đang xử lý

---

### 2. 401 Unauthorized Error - **FIXED**

**Triệu chứng:**
```
<-- 401 http://10.0.2.2:8080/api/v1/ai/chat
WWW-Authenticate: Bearer
AuthInterceptor: ❌ NO TOKEN AVAILABLE for: http://...
```

**Nguyên nhân:**
- ✅ Token không được gửi trong header (user chưa login)
- ✅ Token đã hết hạn
- ✅ Token không hợp lệ
- ✅ User chưa đăng nhập

**Giải pháp đã áp dụng (2026-02-15):**

1. **Improved AuthInterceptor với logging tốt hơn:**
   ```kotlin
   class AuthInterceptor(private val context: Context) : Interceptor {
       override fun intercept(chain: Interceptor.Chain): Response {
           val originalRequest = chain.request()
           val token = TokenManager.getToken(context)

           val newRequest = if (!token.isNullOrEmpty()) {
               Log.d("AuthInterceptor", "Adding token to request: ${originalRequest.url}")
               Log.d("AuthInterceptor", "Token: ${token.take(20)}...")
               Log.d("AuthInterceptor", "Token length: ${token.length}")
               originalRequest.newBuilder()
                   .header("Authorization", "Bearer $token")
                   .build()
           } else {
               Log.e("AuthInterceptor", "❌ NO TOKEN AVAILABLE for: ${originalRequest.url}")
               Log.e("AuthInterceptor", "User needs to login first!")
               originalRequest
           }

           return try {
               val response = chain.proceed(newRequest)
               if (response.code == 401) {
                   Log.e("AuthInterceptor", "❌ 401 Unauthorized response from: ${originalRequest.url}")
                   Log.e("AuthInterceptor", "Token was ${if (token.isNullOrEmpty()) "NOT" else ""} included in request")
               }
               response
           } catch (e: java.net.SocketTimeoutException) {
               Log.e("AuthInterceptor", "⏱️ Timeout for: ${originalRequest.url} - ${e.message}")
               throw e
           } catch (e: Exception) {
               Log.e("AuthInterceptor", "❌ Error in interceptor for: ${originalRequest.url}", e)
               throw e
           }
       }
   }
   ```

2. **Pre-check token trong AIRepository:**
   ```kotlin
   suspend fun chat(message: String): Result<String> {
       return try {
           // Check if user has token before making request
           val token = TokenManager.getToken(context)
           if (token.isNullOrEmpty()) {
               return Result.failure(Exception("Vui lòng đăng nhập để sử dụng AI Chat"))
           }
           
           // ... rest of the code
       } catch (e: java.net.SocketTimeoutException) {
           Result.failure(Exception("Yêu cầu mất quá nhiều thời gian (timeout). Vui lòng thử câu hỏi ngắn gọn hơn hoặc thử lại sau."))
       }
   }
   ```

3. **Auto-redirect to Login trong AIFragment:**
   ```kotlin
   private fun sendMessage() {
       // Check if user is logged in before sending
       if (!TokenManager.hasToken(requireContext())) {
           Toast.makeText(
               requireContext(), 
               "Vui lòng đăng nhập để sử dụng AI Chat", 
               Toast.LENGTH_LONG
           ).show()
           
           val intent = Intent(requireContext(), LoginActivity::class.java)
           intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
           startActivity(intent)
           requireActivity().finish()
           return
       }
       // ... send message
   }
   
   // Also handle 401 errors from API
   viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
       if (message.isNotEmpty()) {
           Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
           
           // If error is related to authentication, navigate to login
           if (message.contains("đăng nhập", ignoreCase = true)) {
               TokenManager.clearTokens(requireContext())
               // Navigate to login
           }
       }
   }
   ```

4. **Better error messages:**
   ```kotlin
   val errorMsg = when (response.code()) {
       401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại để sử dụng AI Chat."
       408 -> "Request timeout - AI đang xử lý quá lâu"
       else -> "Failed to chat: ${response.message()}"
   }
   ```

**Debug steps để kiểm tra:**
1. Check log để xem token có được add vào request không:
   ```
   D/AuthInterceptor: Adding token to request: http://10.0.2.2:8080/api/v1/ai/chat
   D/AuthInterceptor: Token: eyJhbGciOiJIUzI1NiI...
   D/AuthInterceptor: Token length: 147
   ```

2. Nếu thấy log này thì token không có:
   ```
   E/AuthInterceptor: ❌ NO TOKEN AVAILABLE for: http://...
   E/AuthInterceptor: User needs to login first!
   ```

3. Verify token trong SharedPreferences:
   ```kotlin
   val token = TokenManager.getToken(context)
   Log.d("DEBUG", "Current token: $token")
   ```

4. Test với Postman xem token có hoạt động không

---

### 3. Backend Security Configuration

**Lưu ý:**
Đảm bảo backend Spring Security cho phép AI endpoints:

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/ai/**").authenticated()  // Require auth
                // ...
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

---

### 4. Testing Tips

**Test với curl:**
```bash
# Get token first
curl -X POST http://10.0.2.2:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test", "password":"password"}'

# Test AI chat with token
curl -X POST http://10.0.2.2:8080/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{"message":"Hello"}'
```

**Test với Logcat:**
```bash
adb logcat | grep -E "AuthInterceptor|AIRepository|okhttp"
```

**Expected logs:**
```
D/AuthInterceptor: Adding token to request: http://10.0.2.2:8080/api/v1/ai/chat
D/AuthInterceptor: Token: eyJhbGciOiJIUzI1NiI...
I/okhttp.OkHttpClient: --> POST http://10.0.2.2:8080/api/v1/ai/chat
I/okhttp.OkHttpClient: Authorization: Bearer eyJhbGciOiJIUzI1NiI...
I/okhttp.OkHttpClient: <-- 200 http://10.0.2.2:8080/api/v1/ai/chat (45000ms)
```

---

### 5. Performance Optimization

**Frontend:**
- Implement request debouncing
- Cache AI responses (optional)
- Show typing indicator
- Limit message length

**Backend:**
- Consider implementing streaming responses
- Add request queue/rate limiting
- Implement caching for common queries
- Use async processing

---

### 6. User Experience Best Practices

**Loading states:**
```kotlin
viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
    if (isLoading) {
        Toast.makeText(
            requireContext(), 
            "AI đang suy nghĩ... Có thể mất 30-60 giây", 
            Toast.LENGTH_LONG
        ).show()
    }
}
```

**Error messages:**
```kotlin
val errorMsg = when {
    error.message?.contains("timeout", ignoreCase = true) == true -> 
        "Yêu cầu mất quá nhiều thời gian. Vui lòng thử lại hoặc đặt câu hỏi ngắn gọn hơn."
    error.message?.contains("401") == true -> 
        "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
    else -> "Xin lỗi, tôi gặp lỗi: ${error.message}"
}
```

---

### 7. Common Issues Checklist

- [ ] Backend server đang chạy?
- [ ] API key ChatGPT có hợp lệ không?
- [ ] User đã đăng nhập chưa?
- [ ] Token có trong SharedPreferences không?
- [ ] AuthInterceptor có thêm token vào header không?
- [ ] Timeout values có đủ lớn không? (120s for read)
- [ ] Network connection OK?
- [ ] Backend security config cho phép endpoint?

---

## Tổng kết

**Các thay đổi đã thực hiện:**
1. ✅ Tăng timeout trong RetrofitClient (120s read timeout)
2. ✅ Cải thiện error handling trong AIRepository
3. ✅ Cải thiện error messages trong AIViewModel
4. ✅ Thêm loading indicators trong UI
5. ✅ Thêm logging trong AuthInterceptor
6. ✅ Xử lý SocketTimeoutException riêng

**Kết quả mong đợi:**
- AI requests không còn timeout nếu < 120 giây
- User được thông báo rõ ràng khi request đang xử lý
- Error messages thân thiện và hướng dẫn user
- Token issues được phát hiện và báo lỗi rõ ràng

