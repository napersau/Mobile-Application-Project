# Hướng Dẫn Xử Lý Lỗi (Troubleshooting Guide)

## 🔍 Các Lỗi Thường Gặp

### 1. Lỗi 401 Unauthorized

#### Triệu chứng:
```
<-- 401 http://10.0.2.2:8080/api/v1/ai/chat
WWW-Authenticate: Bearer
```

#### Nguyên nhân:
- Token không được gửi trong header request
- Token đã hết hạn
- Token không hợp lệ
- User chưa đăng nhập

#### Giải pháp:

**Bước 1: Kiểm tra token có được lưu không**
```kotlin
// Trong LoginActivity hoặc bất kỳ Activity nào
val token = TokenManager.getToken(this)
Log.d("DEBUG", "Token: $token")
```

**Bước 2: Kiểm tra AuthInterceptor có hoạt động không**
- Mở file `AuthInterceptor.kt`
- Kiểm tra log:
```
D/AuthInterceptor: Adding token to request
```
- Nếu thấy log "No token available", nghĩa là token chưa được lưu

**Bước 3: Kiểm tra Repository có nhận context không**
```kotlin
// Repository PHẢI nhận context
class AIRepository(private val context: Context) {
    private val api = RetrofitClient.aiApi
}

// ViewModel PHẢI truyền context
class AIViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AIRepository(application.applicationContext)
}
```

**Bước 4: Force logout và login lại**
```kotlin
TokenManager.clearTokens(this)
// Chuyển về LoginActivity
```

---

### 2. Lỗi "Failed to connect" / "Unable to resolve host"

#### Triệu chứng:
```
java.net.UnknownHostException: Unable to resolve host "10.0.2.2"
```

#### Nguyên nhân:
- Backend không chạy
- Sai địa chỉ IP
- Android Emulator không thể kết nối

#### Giải pháp:

**Nếu dùng Emulator:**
- Dùng `10.0.2.2` cho localhost
- Kiểm tra backend đang chạy ở port 8080

**Nếu dùng thiết bị thật:**
- Thay `10.0.2.2` bằng IP thực của máy tính (VD: `192.168.1.100`)
- Đảm bảo thiết bị và máy tính cùng mạng WiFi
- Tắt firewall trên máy tính

**Cách lấy IP máy tính:**
```bash
# Windows
ipconfig
# Tìm "IPv4 Address"

# Mac/Linux
ifconfig
# Tìm "inet"
```

**Cập nhật BASE_URL:**
```kotlin
// RetrofitClient.kt
private const val BASE_URL = "http://192.168.1.100:8080/"
```

---

### 3. Lỗi "channel is unrecoverably broken"

#### Triệu chứng:
```
E/InputDispatcher: channel 'd59bb88 com.example.fe/...' ~ Channel is unrecoverably broken!
```

#### Nguyên nhân:
- App bị crash hoặc ANR (Application Not Responding)
- Thao tác blocking UI thread quá lâu

#### Giải pháp:
- Đảm bảo mọi network call đều chạy trong `viewModelScope.launch {}`
- Không blocking UI thread
- Check Logcat để tìm exception gốc gây crash

---

### 4. Lỗi JSON Parsing

#### Triệu chứng:
```
com.google.gson.JsonSyntaxException: Expected BEGIN_OBJECT but was STRING
```

#### Nguyên nhân:
- Backend trả về format JSON không khớp với model
- Field null nhưng model không cho phép null

#### Giải pháp:

**Bước 1: Kiểm tra response thực tế**
- Xem OkHttp Logging Interceptor logs
- So sánh với model Kotlin

**Bước 2: Đảm bảo model khớp với backend**
```kotlin
// Nếu field có thể null, dùng `?`
data class ExamResponse(
    val id: Long,
    val title: String,
    val description: String?,  // ← Nullable
    val duration: Int?        // ← Nullable
)
```

**Bước 3: Test với Postman/Thunder Client trước**
- Gửi request bằng tool khác
- Xem response có đúng format không

---

### 5. Lỗi Instant/Date Parsing

#### Triệu chứng:
```
Failed to parse date: 2026-02-15T04:23:21Z
```

#### Nguyên nhân:
- GSON không biết cách parse `Instant`

#### Giải pháp:
**Đã được xử lý trong RetrofitClient:**
```kotlin
private fun createGson() = GsonBuilder()
    .registerTypeAdapter(Instant::class.java, JsonDeserializer { json, _, _ ->
        Instant.parse(json.asString)
    })
    .registerTypeAdapter(Instant::class.java, JsonSerializer<Instant> { src, _, _ ->
        JsonPrimitive(src.toString())
    })
    .create()
```

---

### 6. Lỗi "lateinit property has not been initialized"

#### Triệu chứng:
```
kotlin.UninitializedPropertyAccessException: lateinit property viewModel has not been initialized
```

#### Nguyên nhân:
- Truy cập biến `lateinit` trước khi khởi tạo

#### Giải pháp:
```kotlin
// Trong Fragment/Activity
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    // PHẢI khởi tạo trước khi dùng
    viewModel = ViewModelProvider(this)[AIViewModel::class.java]
    
    // Bây giờ mới dùng được
    viewModel.sendMessage("Hello")
}
```

---

## 🛠️ Debug Tools

### 1. Logcat Filtering

**Xem tất cả logs của app:**
```
package:com.example.fe
```

**Xem logs API requests:**
```
tag:OkHttpClient
```

**Xem logs Authentication:**
```
tag:AuthInterceptor
```

**Xem logs specific Activity:**
```
tag:LoginActivity
```

### 2. Network Inspection

**Xem request/response chi tiết:**
```kotlin
// Đã được bật trong RetrofitClient
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

**Log output ví dụ:**
```
--> POST http://10.0.2.2:8080/api/v1/ai/chat
Content-Type: application/json; charset=UTF-8
Content-Length: 32
{"message":"Lo trinh hoc toeic"}
--> END POST

<-- 200 http://10.0.2.2:8080/api/v1/ai/chat (1234ms)
Content-Type: application/json
{"code":1000,"result":"Đây là lộ trình học TOEIC...","message":"Success"}
<-- END HTTP
```

### 3. Breakpoint Debugging

**Đặt breakpoint tại:**
1. Repository (trước khi gọi API)
2. ViewModel (xử lý result)
3. Activity/Fragment (hiển thị UI)

---

## 📋 Checklist Khi Gặp Lỗi

- [ ] Backend có đang chạy không?
- [ ] Địa chỉ IP/port có đúng không?
- [ ] Đã đăng nhập và có token chưa?
- [ ] Request body có đúng format không?
- [ ] Model Kotlin có khớp với response JSON không?
- [ ] Đã check Logcat logs chưa?
- [ ] Đã test với Postman/Thunder Client chưa?
- [ ] Internet permission đã được thêm vào Manifest chưa?

---

## 🧪 Test API Với Postman

### 1. Login
```http
POST http://10.0.2.2:8080/identity/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123"
}
```

**Response:**
```json
{
    "code": 1000,
    "result": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "authenticated": true
    },
    "message": "Login successful"
}
```

**Lưu token để dùng cho các request khác!**

---

### 2. Chat AI (Cần token)
```http
POST http://10.0.2.2:8080/api/v1/ai/chat
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
    "message": "Lo trinh hoc toeic"
}
```

---

### 3. Get Exams By Type
```http
GET http://10.0.2.2:8080/api/v1/exams/type?examType=TOEIC_FULL_TEST
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### 4. Submit Exam Result
```http
POST http://10.0.2.2:8080/api/v1/exam-results
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
    "score": 85,
    "listeningScore": 425,
    "readingScore": 420,
    "correctCount": 180,
    "submitTime": "2026-02-15T04:23:21Z",
    "timeTaken": 7200,
    "examId": 1,
    "examResultDetailRequestList": [
        {
            "selectedOption": "A",
            "isCorrect": true,
            "questionId": 101
        },
        {
            "selectedOption": "C",
            "isCorrect": false,
            "questionId": 102
        }
    ]
}
```

---

## 💡 Tips

1. **Luôn check Logcat trước khi hỏi**: 90% lỗi có thể tìm thấy trong logs
2. **Test API với Postman trước**: Đảm bảo backend hoạt động đúng
3. **Dùng try-catch**: Wrap mọi network call trong try-catch
4. **Timeout hợp lý**: Đừng set timeout quá ngắn (30s là OK)
5. **Handle loading state**: Luôn hiển thị loading indicator khi gọi API

---

## 🆘 Khi Mọi Thứ Đều Fail

1. **Clean & Rebuild:**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

2. **Invalidate Cache:**
   - Android Studio → File → Invalidate Caches / Restart

3. **Check Gradle Dependencies:**
   - Sync Gradle
   - Update dependencies nếu cần

4. **Xóa app khỏi emulator/device và cài lại**

5. **Restart Android Studio và Emulator**

6. **Check lại backend logs** - có thể lỗi từ server

---

**Cập nhật lần cuối:** 15/02/2026

