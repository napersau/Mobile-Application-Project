# AI Chat Fix - 2026-02-15

## Vấn đề
Người dùng gặp lỗi khi sử dụng AI Chat:
1. **SocketTimeoutException**: Request timeout khi gọi API
2. **401 Unauthorized**: Token không được gửi hoặc không hợp lệ

## Log Errors
```
2026-02-15 20:56:55.927 - okhttp.OkHttpClient: <-- HTTP FAILED: java.net.SocketTimeoutException: timeout
2026-02-15 20:56:55.934 - AuthInterceptor: Error in interceptor (Fix with AI)
2026-02-15 20:56:55.989 - okhttp.OkHttpClient: <-- 401 http://10.0.2.2:8080/api/v1/ai/chat (53ms)
```

## Root Cause Analysis

### Issue 1: SocketTimeoutException
- AI API calls to ChatGPT can take 30-60+ seconds
- Initial timeout was insufficient
- This is expected behavior for AI APIs

### Issue 2: 401 Unauthorized
**Main Problem**: User was not logged in or token was missing/expired
- AuthInterceptor logs showed token was NOT being added to requests
- No "Authorization: Bearer ..." header in the HTTP logs
- User needs to login first before using AI features

## Solutions Implemented

### 1. Enhanced AuthInterceptor Logging
**File**: `app/src/main/java/com/example/fe/network/AuthInterceptor.kt`

**Changes**:
- ✅ Added detailed logging for token presence/absence
- ✅ Added 401 response detection and logging
- ✅ Added timeout-specific error handling
- ✅ Clear error messages with emoji for easy identification

**Key improvements**:
```kotlin
// Now logs clear messages:
// ✅ "Adding token to request: ..."
// ❌ "NO TOKEN AVAILABLE for: ..."
// ❌ "401 Unauthorized response from: ..."
// ⏱️ "Timeout for: ..."
```

### 2. Pre-flight Token Check in AIRepository
**File**: `app/src/main/java/com/example/fe/repository/AIRepository.kt`

**Changes**:
- ✅ Check if token exists BEFORE making API call
- ✅ Return user-friendly error message immediately
- ✅ Better timeout error messages in Vietnamese
- ✅ Better 401 error messages

**Benefits**:
- Saves network call if user not logged in
- Provides immediate feedback
- Better UX with Vietnamese error messages

```kotlin
suspend fun chat(message: String): Result<String> {
    // Check if user has token before making request
    val token = TokenManager.getToken(context)
    if (token.isNullOrEmpty()) {
        return Result.failure(Exception("Vui lòng đăng nhập để sử dụng AI Chat"))
    }
    // ...
}
```

### 3. Auto-Redirect to Login in AIFragment
**File**: `app/src/main/java/com/example/fe/ui/fragment/AIFragment.kt`

**Changes**:
- ✅ Check token before sending message
- ✅ Auto-redirect to login if no token
- ✅ Clear token and redirect on authentication errors
- ✅ Added Intent import for LoginActivity

**User Flow**:
1. User tries to send message without login
2. App shows: "Vui lòng đăng nhập để sử dụng AI Chat"
3. App automatically redirects to LoginActivity
4. User logs in and can use AI Chat

```kotlin
private fun sendMessage() {
    // Check if user is logged in before sending
    if (!TokenManager.hasToken(requireContext())) {
        Toast.makeText(
            requireContext(), 
            "Vui lòng đăng nhập để sử dụng AI Chat", 
            Toast.LENGTH_LONG
        ).show()
        
        // Auto-redirect to login
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
        return
    }
    // ... send message
}
```

### 4. Better Error Messages
All error messages now in Vietnamese and user-friendly:

| Error | Old Message | New Message |
|-------|------------|-------------|
| No Token | "Unauthorized - Token không hợp lệ hoặc đã hết hạn" | "Vui lòng đăng nhập để sử dụng AI Chat" |
| 401 Error | "Unauthorized - Token không hợp lệ hoặc đã hết hạn" | "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại để sử dụng AI Chat." |
| Timeout | "Request timeout - AI đang xử lý quá lâu" | "Yêu cầu mất quá nhiều thời gian (timeout). Vui lòng thử câu hỏi ngắn gọn hơn hoặc thử lại sau." |

## Testing Guide

### How to Test the Fix

1. **Test without login (401 scenario)**:
   ```
   1. Clear app data (Settings > Apps > FE App > Storage > Clear Data)
   2. Open app
   3. Navigate to AI Chat without logging in
   4. Try to send a message
   5. Expected: Immediate redirect to login with message
   ```

2. **Test with login (success scenario)**:
   ```
   1. Login with valid credentials
   2. Navigate to AI Chat
   3. Send a message
   4. Expected: See loading indicator, then AI response
   ```

3. **Test timeout scenario**:
   ```
   1. Login
   2. Send a very complex question (long message)
   3. Wait for timeout (120 seconds)
   4. Expected: User-friendly timeout message
   ```

4. **Test expired token (401 after login)**:
   ```
   1. Login and wait for token to expire (if backend has short expiry)
   2. Try to send message
   3. Expected: Error message + redirect to login
   ```

### Expected Logs

**When user is NOT logged in:**
```
E/AuthInterceptor: ❌ NO TOKEN AVAILABLE for: http://10.0.2.2:8080/api/v1/ai/chat
E/AuthInterceptor: User needs to login first!
```

**When user IS logged in:**
```
D/AuthInterceptor: Adding token to request: http://10.0.2.2:8080/api/v1/ai/chat
D/AuthInterceptor: Token: eyJhbGciOiJIUzI1NiI...
D/AuthInterceptor: Token length: 147
I/okhttp.OkHttpClient: --> POST http://10.0.2.2:8080/api/v1/ai/chat
I/okhttp.OkHttpClient: Authorization: Bearer eyJhbGciOiJIUzI1NiI...
I/okhttp.OkHttpClient: <-- 200 http://10.0.2.2:8080/api/v1/ai/chat (45000ms)
```

**On timeout:**
```
E/AuthInterceptor: ⏱️ Timeout for: http://10.0.2.2:8080/api/v1/ai/chat - timeout
```

**On 401 error:**
```
E/AuthInterceptor: ❌ 401 Unauthorized response from: http://10.0.2.2:8080/api/v1/ai/chat
E/AuthInterceptor: Token was included in request
```

## Files Modified

1. ✅ `app/src/main/java/com/example/fe/network/AuthInterceptor.kt`
   - Enhanced logging with emoji markers
   - Added 401 detection
   - Added timeout-specific handling

2. ✅ `app/src/main/java/com/example/fe/repository/AIRepository.kt`
   - Pre-flight token check
   - Better error messages (Vietnamese)
   - Both `chat()` and `translate()` functions updated

3. ✅ `app/src/main/java/com/example/fe/ui/fragment/AIFragment.kt`
   - Token check before sending message
   - Auto-redirect to login
   - Handle authentication errors from API

4. ✅ `AI_API_TROUBLESHOOTING.md`
   - Updated with new fixes
   - Added debugging steps
   - Added expected logs

## Next Steps for User

1. **Rebuild and Run the App**:
   ```bash
   ./gradlew clean
   ./gradlew build
   # Or use Android Studio Build > Clean Project > Rebuild Project
   ```

2. **Test the scenarios above**

3. **If still getting 401 errors after login**:
   - Check backend logs to see if JWT token is valid
   - Verify token expiration time in backend
   - Check if backend SecurityConfig allows `/api/v1/ai/**` endpoints

4. **If still getting timeout errors**:
   - Try shorter questions
   - Check backend ChatGPT API key is valid
   - Consider implementing streaming responses (advanced)

## Prevention

To prevent this issue in the future:

1. **Always check authentication state** before making API calls
2. **Provide immediate feedback** to users about authentication status
3. **Use descriptive log messages** with clear markers (✅, ❌, ⏱️)
4. **Handle errors gracefully** with auto-redirect to login
5. **Use user-friendly error messages** in the user's language

## Success Criteria

✅ User can see clear error message when not logged in  
✅ User is automatically redirected to login  
✅ After login, user can successfully use AI Chat  
✅ Timeout errors show user-friendly messages  
✅ 401 errors trigger re-login flow  
✅ Logs are clear and easy to debug

## Notes

- The SocketTimeoutException is **expected behavior** for AI APIs
- The 120-second timeout is already configured correctly
- The main issue was **missing authentication token** (user not logged in)
- All fixes focus on **better UX** and **clear error handling**

