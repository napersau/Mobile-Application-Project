# 🎓 TOEIC Learning Mobile App - Android

Ứng dụng học TOEIC trên Android với các tính năng:
- 🤖 Chat với AI để học tập
- 📝 Làm đề thi TOEIC/IELTS với phân loại chi tiết
- 🃏 Học từ vựng với Flashcards
- 📚 Tài liệu học tập
- 🌍 Dịch thuật tiếng Anh - Việt

---

## 📋 Yêu Cầu Hệ Thống

- **Android Studio**: Arctic Fox hoặc mới hơn
- **JDK**: 11 hoặc 17
- **Android SDK**: API 24+ (Android 7.0+)
- **Kotlin**: 1.8+
- **Backend**: Spring Boot server phải chạy ở `http://localhost:8080`

---

## 🚀 Cài Đặt & Chạy

### 1. Clone Project

```bash
git clone <repository-url>
cd FE
```

### 2. Mở Trong Android Studio

1. Mở Android Studio
2. File → Open → Chọn folder `FE`
3. Đợi Gradle sync hoàn tất

### 3. Cấu Hình Backend URL

**Nếu dùng Android Emulator:**
- Giữ nguyên `BASE_URL = "http://10.0.2.2:8080/"` trong `RetrofitClient.kt`

**Nếu dùng thiết bị thật:**
- Lấy IP máy tính: `ipconfig` (Windows) hoặc `ifconfig` (Mac/Linux)
- Thay đổi trong `RetrofitClient.kt`:
  ```kotlin
  private const val BASE_URL = "http://192.168.1.XXX:8080/"
  ```
- Đảm bảo thiết bị và máy cùng WiFi

### 4. Đảm Bảo Backend Đang Chạy

```bash
# Chạy backend Spring Boot
cd ../BE
./mvnw spring-boot:run
# hoặc
./gradlew bootRun
```

Backend phải chạy ở port 8080!

### 5. Build & Run

**Cách 1: Từ Android Studio**
- Click nút ▶️ Run
- Chọn emulator hoặc device

**Cách 2: Từ Terminal**
```bash
# Build debug APK
./gradlew assembleDebug

# Install to device
./gradlew installDebug

# Or run directly
./gradlew installDebug && adb shell am start -n com.example.fe/.MainActivity
```

---

## 📁 Cấu Trúc Project

```
app/src/main/java/com/example/fe/
├── model/                  # Data models
│   ├── Exam.kt            # Exam models & enums
│   ├── User.kt            # User models
│   ├── ChatMessage.kt     # AI chat model
│   └── ...
├── network/               # Network layer
│   ├── RetrofitClient.kt  # Retrofit configuration
│   ├── AuthInterceptor.kt # Token injection
│   ├── *Api.kt           # API interfaces
│   └── ...
├── repository/            # Data repositories
│   ├── ExamRepository.kt
│   ├── AIRepository.kt
│   └── ...
├── viewmodel/            # ViewModels
│   ├── ExamViewModel.kt
│   ├── AIViewModel.kt
│   └── ...
├── ui/                   # UI layer
│   ├── activity/        # Activities
│   │   ├── LoginActivity.kt
│   │   ├── MainActivity.kt
│   │   ├── ExamListActivity.kt
│   │   ├── ExamDetailActivity.kt
│   │   ├── ExamTakingActivity.kt
│   │   └── ExamResultActivity.kt
│   ├── fragment/        # Fragments
│   │   ├── HomeFragment.kt
│   │   ├── ExamsFragment.kt
│   │   ├── AIFragment.kt
│   │   ├── TranslateFragment.kt
│   │   └── ...
│   └── adapter/         # RecyclerView adapters
│       ├── ExamAdapter.kt
│       ├── QuestionAdapter.kt
│       └── ...
└── utils/               # Utilities
    └── TokenManager.kt  # Token management
```

---

## 🔑 Tính Năng Chính

### 1. Authentication
- Đăng ký tài khoản mới
- Đăng nhập với username/password
- Token tự động được lưu và inject vào mọi request

### 2. Exam System

#### Phân Loại Đề Thi
- **TOEIC Full Test**: 200 câu, 120 phút
- **TOEIC Mini Test**: 50-100 câu
- **IELTS Academic**: Đề thi học thuật
- **IELTS General**: Đề thi tổng quát
- **Mock Test**: Bài kiểm tra ngắn

#### Flow Làm Bài
1. Chọn loại đề thi
2. Xem danh sách đề thi
3. Đọc thông tin chi tiết
4. Bắt đầu làm bài (có timer)
5. Nộp bài
6. Xem kết quả (điểm số, listening/reading score)

#### Kết Quả
- Điểm tổng (%)
- Điểm Listening (cho TOEIC)
- Điểm Reading (cho TOEIC)
- Số câu đúng/tổng số câu
- Thời gian làm bài
- Chi tiết từng câu trả lời (lưu vào backend)

### 3. AI Features

#### Chat AI
- Hỏi về lộ trình học TOEIC
- Giải thích ngữ pháp
- Tư vấn học tập

#### Translate
- Dịch tiếng Anh sang tiếng Việt
- Quick examples
- Copy kết quả dịch

### 4. Flashcards
- Tạo bộ thẻ học (Decks)
- Thêm từ vựng
- Luyện tập với flashcards

### 5. Documents & Courses
- Xem tài liệu học tập
- Khóa học trực tuyến

---

## 🔧 Cấu Hình

### Thêm Dependencies (nếu cần)

```gradle
// app/build.gradle.kts

dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    
    // UI
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.9.0")
}
```

### Permissions

**AndroidManifest.xml:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## 🧪 Testing

### Manual Testing

**1. Test Login:**
```
Username: testuser
Password: password123
```

**2. Test AI Chat:**
- Input: "Lo trinh hoc toeic"
- Expected: Nhận được lộ trình học chi tiết

**3. Test Exam:**
- Chọn "TOEIC Full Test"
- Làm một vài câu
- Nộp bài
- Kiểm tra kết quả có lưu vào backend không

### Logcat Debugging

**Xem logs:**
```
adb logcat | grep -E "AuthInterceptor|OkHttpClient|ExamViewModel"
```

**Hoặc trong Android Studio:**
- Logcat → Filter: `package:com.example.fe`

---

## 🐛 Xử Lý Lỗi

Xem file `TROUBLESHOOTING_GUIDE.md` để biết chi tiết về:
- Lỗi 401 Unauthorized
- Lỗi kết nối
- Lỗi JSON parsing
- Và nhiều lỗi khác...

### Lỗi Thường Gặp

**Lỗi 401 - Unauthorized:**
```
✅ ĐÃ SỬA: AIRepository giờ nhận context để lấy token
```

**Lỗi không kết nối được:**
- Kiểm tra backend có chạy không
- Kiểm tra địa chỉ IP có đúng không
- Nếu dùng emulator: dùng `10.0.2.2`
- Nếu dùng thiết bị thật: dùng IP thực của máy

---

## 📚 Tài Liệu Tham Khảo

- [SYSTEM_OVERVIEW.md](./SYSTEM_OVERVIEW.md) - Tổng quan hệ thống
- [TROUBLESHOOTING_GUIDE.md](./TROUBLESHOOTING_GUIDE.md) - Hướng dẫn xử lý lỗi
- [EXAM_FEATURE_DOCUMENTATION.md](./EXAM_FEATURE_DOCUMENTATION.md) - Chi tiết tính năng Exam
- [AI_FEATURES_DOCUMENTATION.md](./AI_FEATURES_DOCUMENTATION.md) - Chi tiết tính năng AI

---

## 🎨 Screenshots

### Home Screen
- Dashboard với quick access
- Exam categories
- Recent activities

### Exam Flow
- Exam list by type
- Exam details
- Taking exam with timer
- Result screen with scores

### AI Features
- Chat interface
- Translation tool

---

## 📝 API Documentation

### Base URL
```
http://10.0.2.2:8080
```

### Endpoints

**Auth:**
- `POST /identity/auth/login` - Đăng nhập
- `POST /identity/auth/register` - Đăng ký

**AI:**
- `POST /api/v1/ai/chat` - Chat với AI
- `POST /api/v1/ai/translate` - Dịch thuật

**Exams:**
- `GET /api/v1/exams/type?examType={type}` - Lấy đề thi theo loại
- `GET /api/v1/exams/{id}` - Chi tiết đề thi
- `POST /api/v1/exam-results` - Lưu kết quả

Xem thêm trong `SYSTEM_OVERVIEW.md`

---

## 🤝 Contributing

1. Fork project
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add some AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Open Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Authors

- **Mobile Team** - Android Development

---

## 🙏 Acknowledgments

- Backend API by Spring Boot team
- AI integration
- TOEIC learning methodology

---

## 📞 Support

Nếu gặp vấn đề:
1. Kiểm tra `TROUBLESHOOTING_GUIDE.md`
2. Xem Logcat logs
3. Test API với Postman
4. Liên hệ team support

---

**Happy Coding! 🚀**

**Version:** 1.0.0  
**Last Updated:** 15/02/2026  
**Status:** ✅ Production Ready

