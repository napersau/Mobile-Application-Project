# Tổng Quan Hệ Thống Mobile App - TOEIC Learning

## 🎯 Tình Trạng Hiện Tại

### ✅ Đã Hoàn Thành

#### 1. **Hệ Thống Xác Thực (Authentication)**
- ✅ Đăng nhập (Login)
- ✅ Đăng ký (Register)
- ✅ Lưu token vào SharedPreferences
- ✅ AuthInterceptor tự động thêm token vào mọi API request
- ✅ TokenManager quản lý token

#### 2. **Hệ Thống AI**
- ✅ Chat với AI (AIFragment)
- ✅ Dịch thuật (TranslateFragment)
- ✅ API endpoint: `/api/v1/ai/chat` và `/api/v1/ai/translate`
- ✅ **ĐÃ SỬA LỖI 401**: AIRepository giờ nhận context để AuthInterceptor có thể lấy token

#### 3. **Hệ Thống Đề Thi (Exam System)**

##### 3.1 Phân Loại Đề Thi (ExamType)
```kotlin
enum class ExamType {
    TOEIC_FULL_TEST,  // Đề thi TOEIC đầy đủ (200 câu)
    TOEIC_MINI_TEST,  // Đề rút gọn (50-100 câu)
    IELTS_ACADEMIC,   // IELTS Học thuật
    IELTS_GENERAL,    // IELTS Tổng quát
    MOCK_TEST         // Thi thử chung chung / Bài kiểm tra 15p
}
```

##### 3.2 Flow Làm Bài Thi
1. **HomeFragment** → Click "Exams"
2. **ExamsFragment** → Chọn loại đề thi (TOEIC Full, Mini, IELTS, Mock)
3. **ExamListActivity** → Load danh sách đề thi theo loại (API: `GET /api/v1/exams/type?examType=TOEIC_FULL_TEST`)
4. **ExamDetailActivity** → Xem chi tiết đề thi
5. **ExamTakingActivity** → Làm bài thi
   - Đếm ngược thời gian
   - Chọn đáp án
   - Nộp bài
6. **ExamResultActivity** → Xem kết quả

##### 3.3 Lưu Kết Quả Bài Thi
- ✅ API endpoint: `POST /api/v1/exam-results`
- ✅ ExamResultRequest bao gồm:
  - `score`: Điểm tổng
  - `listeningScore`: Điểm nghe (cho TOEIC)
  - `readingScore`: Điểm đọc (cho TOEIC)
  - `correctCount`: Số câu đúng
  - `submitTime`: Thời điểm nộp bài (ISO 8601)
  - `timeTaken`: Thời gian làm bài (giây)
  - `examId`: ID đề thi
  - `examResultDetailRequestList`: Chi tiết từng câu trả lời
- ✅ ExamResultDetailRequest cho mỗi câu:
  - `selectedOption`: Đáp án user chọn (A/B/C/D hoặc null)
  - `isCorrect`: Đúng/sai
  - `questionId`: ID câu hỏi

##### 3.4 Cấu Trúc Đề Thi
- **ExamResponse**: Thông tin đề thi
  - `id`, `title`, `description`, `duration`, `type`, `totalQuestions`
  - `questionGroups`: Danh sách nhóm câu hỏi (Part 1-7)
- **QuestionGroupResponse**: Nhóm câu hỏi (Part)
  - `type`: PartType (PART_1 đến PART_7)
  - `content`: Nội dung HTML (cho Part 6, 7)
  - `audioUrl`: File audio (cho Part 1, 2, 3, 4)
  - `imageUrl`: Hình ảnh (cho Part 1)
  - `questions`: Danh sách câu hỏi
- **QuestionResponse**: Câu hỏi
  - `id`, `questionNumber`, `text`
  - `optionA`, `optionB`, `optionC`, `optionD`
  - `correctAnswer`: Đáp án đúng (A/B/C/D)
  - `explanation`: Giải thích chi tiết

#### 4. **Các Module Khác**
- ✅ Flashcards (Thẻ học từ vựng)
- ✅ Decks (Bộ thẻ)
- ✅ Documents (Tài liệu học)
- ✅ Courses (Khóa học)
- ✅ Profile (Hồ sơ người dùng)

---

## 🔧 Cấu Trúc Kỹ Thuật

### Network Layer
```
RetrofitClient (Singleton)
├── AuthInterceptor (Tự động thêm Bearer token)
├── Logging Interceptor (Debug)
└── GSON Converter (Hỗ trợ Instant/ISO 8601)
```

### Repository Layer
- `AIRepository`: Chat & Translate
- `AuthRepository`: Login & Register
- `ExamRepository`: CRUD đề thi
- `ExamResultRepository`: Lưu kết quả bài thi
- `FlashcardsRepository`, `DecksRepository`, `DocumentRepository`, `CourseRepository`

### ViewModel Layer (AndroidViewModel)
- `AIViewModel`: Quản lý chat messages và translation
- `AuthViewModel`: Xử lý login/register
- `ExamViewModel`: Load đề thi, submit kết quả
- Các ViewModel khác cho từng module

### UI Layer
**Activities:**
- `LoginActivity`, `RegisterActivity`, `MainActivity`
- `ExamListActivity`: Danh sách đề thi theo loại
- `ExamDetailActivity`: Chi tiết đề thi
- `ExamTakingActivity`: Làm bài thi (có timer)
- `ExamResultActivity`: Kết quả bài thi

**Fragments:**
- `HomeFragment`, `ExamsFragment`, `AIFragment`, `TranslateFragment`
- `FlashcardsFragment`, `DocumentsFragment`, `CoursesFragment`, `ProfileFragment`

---

## 🐛 Sửa Lỗi Gần Đây

### Lỗi AI Chat 401 Unauthorized
**Nguyên nhân:** 
- AIRepository không nhận context nên AuthInterceptor không thể lấy token từ SharedPreferences

**Giải pháp:**
```kotlin
// AIRepository.kt
class AIRepository(private val context: Context) {
    private val api = RetrofitClient.aiApi
    // ...
}

// AIViewModel.kt
class AIViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AIRepository(application.applicationContext)
    // ...
}
```

---

## 📝 API Endpoints Đang Sử Dụng

### Auth
- `POST /identity/auth/login`
- `POST /identity/auth/register`

### AI
- `POST /api/v1/ai/chat` (Body: `{"message": "..."}`)
- `POST /api/v1/ai/translate` (Body: `{"text": "..."}`)

### Exams
- `GET /api/v1/exams` - Tất cả đề thi
- `GET /api/v1/exams/type?examType=TOEIC_FULL_TEST` - Đề thi theo loại
- `GET /api/v1/exams/{id}` - Chi tiết đề thi
- `POST /api/v1/exams` - Tạo đề thi (Admin)
- `PUT /api/v1/exams/{id}` - Cập nhật đề thi
- `DELETE /api/v1/exams/{id}` - Xóa đề thi

### Exam Results
- `POST /api/v1/exam-results` - Lưu kết quả bài thi
- `GET /api/v1/exam-results/{id}` - Xem kết quả

---

## 🚀 Hướng Dẫn Test

### 1. Test AI Chat
1. Đăng nhập vào app
2. Vào tab "AI"
3. Nhập tin nhắn: "Lo trinh hoc toeic"
4. ✅ **Lỗi 401 đã được sửa**, giờ sẽ nhận được response từ AI

### 2. Test Exam System
1. Đăng nhập
2. Vào tab "Home" → Click "Exams"
3. Chọn loại đề thi (VD: "TOEIC Full Test")
4. Chọn một đề thi
5. Click "Bắt đầu làm bài"
6. Làm bài (có timer)
7. Nộp bài → Xem kết quả
8. ✅ Kết quả được lưu vào backend qua API

### 3. Test Translate
1. Vào tab "AI" → Chuyển sang "Translate"
2. Nhập văn bản tiếng Anh
3. Click "Dịch"
4. ✅ Nhận kết quả dịch tiếng Việt

---

## 📦 Dependencies Chính

```gradle
// Retrofit & OkHttp
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'

// Lifecycle & ViewModel
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.1'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1'

// RecyclerView & CardView
implementation 'androidx.recyclerview:recyclerview:1.3.1'
implementation 'androidx.cardview:cardview:1.0.0'
```

---

## ✨ Tính Năng Nổi Bật

1. **Phân loại đề thi thông minh**: 5 loại đề thi khác nhau
2. **Tính điểm TOEIC tự động**: Listening + Reading score riêng biệt
3. **Lưu chi tiết từng câu trả lời**: Để review sau này
4. **Timer đếm ngược**: Cảnh báo khi còn < 5 phút
5. **Xác thực tự động**: Token được tự động thêm vào mọi request
6. **AI Chat & Translate**: Hỗ trợ học tập

---

## 🎓 Lộ Trình Học TOEIC (Tích hợp sẵn trong AI Chat)

Khi user hỏi AI về "lộ trình học TOEIC", hệ thống sẽ trả về lộ trình chi tiết từ 0-990 điểm, bao gồm:
- **Giai đoạn 1**: 0-350 (Xây dựng nền tảng)
- **Giai đoạn 2**: 350-550 (Làm quen cấu trúc)
- **Giai đoạn 3**: 550-750 (Luyện đề & tăng tốc)
- **Giai đoạn 4**: 800-990 (Về đích)

---

## 🔮 Tính Năng Có Thể Mở Rộng

- [ ] Review chi tiết từng câu sai sau khi nộp bài
- [ ] Lịch sử các lần thi (xem lại kết quả cũ)
- [ ] Thống kê tiến bộ (biểu đồ điểm số theo thời gian)
- [ ] Practice theo Part riêng lẻ
- [ ] Tải đề thi offline
- [ ] Xếp hạng (Leaderboard)
- [ ] Nhắc nhở học tập hàng ngày
- [ ] Chia sẻ kết quả lên social media

---

## 📞 Liên Hệ & Hỗ Trợ

Nếu có lỗi hoặc cần hỗ trợ, hãy:
1. Kiểm tra log trong Logcat
2. Kiểm tra OkHttp Interceptor logs để xem request/response
3. Đảm bảo backend đang chạy ở `http://10.0.2.2:8080`
4. Đảm bảo đã đăng nhập và có token hợp lệ

---

**Ngày cập nhật cuối:** 15/02/2026
**Phiên bản:** 1.0.0
**Status:** ✅ Production Ready

