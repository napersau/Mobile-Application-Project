# Tính năng Bài Thi (Exam Feature) - Tài liệu Hướng dẫn

## Tổng quan
Tính năng bài thi đã được xây dựng hoàn chỉnh cho ứng dụng Android, tích hợp với backend API. Tính năng này cho phép người dùng:
- Xem danh sách các bài thi
- Lọc bài thi theo loại (TOEIC Full Test, TOEIC Mini Test, IELTS Academic, IELTS General, Mock Test)
- Xem chi tiết bài thi
- Làm bài thi với đồng hồ đếm ngược
- Xem kết quả và điểm số

## Cấu trúc dự án

### 1. Models (Data Classes)
**File:** `app/src/main/java/com/example/fe/model/Exam.kt`

#### ExamResponse
```kotlin
data class ExamResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val duration: Int?, // phút
    val type: ExamType,
    val totalQuestions: Int?,
    val questionGroups: List<QuestionGroupResponse>?
)
```

#### ExamType (Enum)
- `TOEIC_FULL_TEST` - Đề thi TOEIC đầy đủ (200 câu)
- `TOEIC_MINI_TEST` - Đề rút gọn (50-100 câu)
- `IELTS_ACADEMIC` - IELTS Học thuật
- `IELTS_GENERAL` - IELTS Tổng quát
- `MOCK_TEST` - Thi thử chung

#### QuestionGroupResponse
```kotlin
data class QuestionGroupResponse(
    val id: Long,
    val type: PartType,
    val content: String?, // HTML content cho Part 6, 7
    val audioUrl: String?, // File nghe Part 1,2,3,4
    val imageUrl: String?, // File ảnh Part 1
    val questions: List<QuestionResponse>?
)
```

#### PartType (Enum)
- `PART_1` - Photographs
- `PART_2` - Question-Response
- `PART_3` - Conversations
- `PART_4` - Short Talks
- `PART_5` - Incomplete Sentences
- `PART_6` - Text Completion
- `PART_7` - Reading Comprehension

#### QuestionResponse
```kotlin
data class QuestionResponse(
    val id: Long,
    val questionNumber: Int,
    val text: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String?,
    val correctAnswer: String, // "A", "B", "C", hoặc "D"
    val explanation: String?
)
```

### 2. Network Layer

#### ExamApi
**File:** `app/src/main/java/com/example/fe/network/ExamApi.kt`

Các endpoint API:
- `GET /api/v1/exams` - Lấy tất cả bài thi
- `GET /api/v1/exams/type?examType={type}` - Lọc bài thi theo loại
- `GET /api/v1/exams/{id}` - Lấy chi tiết bài thi
- `POST /api/v1/exams` - Tạo bài thi mới
- `PUT /api/v1/exams/{id}` - Cập nhật bài thi
- `DELETE /api/v1/exams/{id}` - Xóa bài thi

#### RetrofitClient
**File:** `app/src/main/java/com/example/fe/network/RetrofitClient.kt`
- Đã thêm `examApi` property

### 3. Repository Layer

#### ExamRepository
**File:** `app/src/main/java/com/example/fe/repository/ExamRepository.kt`

Xử lý các API calls và trả về `Result<T>` để quản lý thành công/thất bại.

### 4. ViewModel Layer

#### ExamViewModel
**File:** `app/src/main/java/com/example/fe/viewmodel/ExamViewModel.kt`

LiveData properties:
- `examsLiveData` - Danh sách bài thi
- `examDetailLiveData` - Chi tiết bài thi
- `isLoading` - Trạng thái loading
- `errorMessage` - Thông báo lỗi

Methods:
- `getAllExams()` - Lấy tất cả bài thi
- `getExamsByType(ExamType)` - Lọc theo loại
- `getExamById(Long)` - Lấy chi tiết
- `createExam(ExamRequest)` - Tạo mới
- `updateExam(Long, ExamRequest)` - Cập nhật
- `deleteExam(Long)` - Xóa

### 5. UI Components

#### ExamsFragment
**File:** `app/src/main/java/com/example/fe/ui/fragment/ExamsFragment.kt`
**Layout:** `res/layout/fragment_exams.xml`

Chức năng:
- Hiển thị danh sách bài thi trong RecyclerView
- TabLayout để lọc theo loại bài thi (Tất cả, TOEIC Full, TOEIC Mini, IELTS Academic, IELTS General, Mock Test)
- Click vào bài thi để xem chi tiết

#### ExamDetailActivity
**File:** `app/src/main/java/com/example/fe/ui/activity/ExamDetailActivity.kt`
**Layout:** `res/layout/activity_exam_detail.xml`

Chức năng:
- Hiển thị thông tin chi tiết bài thi
- Thông tin về thời gian, số câu hỏi
- Cấu trúc đề thi (các phần)
- Nút "Bắt đầu làm bài"

#### ExamTakingActivity
**File:** `app/src/main/java/com/example/fe/ui/activity/ExamTakingActivity.kt`
**Layout:** `res/layout/activity_exam_taking.xml`

Chức năng:
- Đồng hồ đếm ngược (nếu có thời gian giới hạn)
- Hiển thị tiến độ (đã làm bao nhiêu câu)
- RecyclerView hiển thị tất cả câu hỏi
- Radio buttons để chọn đáp án
- Nút "Nộp bài" với xác nhận
- Cảnh báo khi thoát (để không mất bài làm)
- Tự động nộp bài khi hết giờ

#### ExamResultActivity
**File:** `app/src/main/java/com/example/fe/ui/activity/ExamResultActivity.kt`
**Layout:** `res/layout/activity_exam_result.xml`

Chức năng:
- Hiển thị điểm số (phần trăm)
- Số câu đúng/tổng số câu
- Thời gian làm bài
- Thông điệp kết quả (Xuất sắc, Tốt, Khá, etc.)
- Nút "Xem chi tiết đáp án" (TODO: cần implement)
- Nút "Quay lại danh sách"

### 6. Adapters

#### ExamAdapter
**File:** `app/src/main/java/com/example/fe/ui/adapter/ExamAdapter.kt`
**Layout:** `res/layout/item_exam.xml`

Hiển thị mỗi bài thi trong danh sách với:
- Tiêu đề
- Loại bài thi (với icon)
- Thời gian
- Số câu hỏi
- Mô tả

#### QuestionAdapter
**File:** `app/src/main/java/com/example/fe/ui/adapter/QuestionAdapter.kt`
**Layout:** `res/layout/item_question.xml`

Hiển thị mỗi câu hỏi với:
- Số thứ tự câu hỏi
- Nội dung câu hỏi
- Radio buttons cho các đáp án (A, B, C, D)
- Lưu lại đáp án đã chọn

### 7. Layouts

#### Fragment/Activity Layouts
- `fragment_exams.xml` - Danh sách bài thi với TabLayout và RecyclerView
- `activity_exam_detail.xml` - Chi tiết bài thi
- `activity_exam_taking.xml` - Màn hình làm bài
- `activity_exam_result.xml` - Màn hình kết quả

#### Item Layouts
- `item_exam.xml` - Item cho danh sách bài thi
- `item_question.xml` - Item cho câu hỏi trong bài thi

#### Drawable Resources
- `bg_chip.xml` - Background cho badge/chip (màu xanh nhạt, bo góc)

### 8. AndroidManifest.xml

Đã thêm 3 activities:
```xml
<activity android:name=".ui.activity.ExamDetailActivity" />
<activity android:name=".ui.activity.ExamTakingActivity" 
          android:screenOrientation="portrait"/>
<activity android:name=".ui.activity.ExamResultActivity" />
```

## Workflow (Luồng sử dụng)

1. **Xem danh sách bài thi:**
   - Người dùng mở tab "Bài Thi" trong BottomNavigation
   - ExamsFragment hiển thị danh sách bài thi
   - Có thể lọc theo loại bằng TabLayout

2. **Xem chi tiết:**
   - Click vào một bài thi
   - ExamDetailActivity hiển thị thông tin chi tiết
   - Xem cấu trúc đề (các phần, số câu)

3. **Làm bài thi:**
   - Click "Bắt đầu làm bài"
   - ExamTakingActivity mở ra
   - Đồng hồ bắt đầu đếm ngược (nếu có)
   - Cuộn qua các câu hỏi và chọn đáp án
   - Click "Nộp bài"

4. **Xem kết quả:**
   - ExamResultActivity hiển thị kết quả
   - Điểm số, số câu đúng, thời gian
   - Có thể quay lại danh sách hoặc xem chi tiết đáp án

## Tính năng đặc biệt

### Đồng hồ đếm ngược
- Tự động khởi động khi bắt đầu bài thi
- Cảnh báo (màu đỏ) khi còn dưới 5 phút
- Tự động nộp bài khi hết giờ
- Hiển thị format HH:MM:SS hoặc MM:SS

### Quản lý trạng thái
- Lưu đáp án đã chọn trong memory
- Hiển thị tiến độ (đã làm X/Y câu)
- Cảnh báo nếu chưa làm hết khi nộp bài

### Xử lý lỗi
- Loading state với ProgressBar
- Error messages với Toast
- Empty state khi không có bài thi

### UI/UX
- Material Design với CardView, elevation
- Responsive với RecyclerView
- Smooth scrolling
- Confirmation dialogs cho các hành động quan trọng

## API Backend Integration

Base URL: `http://10.0.2.2:8080/` (Android Emulator)

### Request Headers
- `Authorization: Bearer {token}` (tự động thêm bởi AuthInterceptor)
- `Content-Type: application/json`

### Response Format
```json
{
  "code": 1000,
  "result": { ... },
  "message": "Success message"
}
```

## Các bước tiếp theo (TODO)

1. **Exam Review Screen:**
   - Màn hình xem lại chi tiết đáp án
   - Hiển thị đúng/sai cho từng câu
   - Hiển thị explanation (giải thích)

2. **Audio/Image Support:**
   - Tích hợp ExoPlayer cho audio (Part 1-4)
   - Hiển thị images trong QuestionAdapter
   - HTML rendering cho content (Part 6-7)

3. **Offline Support:**
   - Cache bài thi đã tải bằng Room Database
   - Cho phép làm bài offline

4. **History & Statistics:**
   - Lưu lịch sử làm bài
   - Thống kê điểm số theo thời gian
   - Biểu đồ tiến bộ

5. **Advanced Features:**
   - Bookmark câu hỏi khó
   - Review mode (chỉ xem, không làm bài)
   - Share results
   - Leaderboard

## Testing

### Build Status
✅ Build successful với Gradle
⚠️ Một số deprecated warnings (onBackPressed) - có thể fix sau

### Checklist
- [x] Models và Enums
- [x] Network API interface
- [x] Repository layer
- [x] ViewModel
- [x] ExamsFragment với filtering
- [x] ExamDetailActivity
- [x] ExamTakingActivity với timer
- [x] ExamResultActivity
- [x] Adapters (Exam, Question)
- [x] Layouts (responsive, Material Design)
- [x] AndroidManifest registration
- [x] Build successful

## Dependencies Required

Đảm bảo các dependencies sau đã có trong `build.gradle`:
- Retrofit2 & Gson converter
- Kotlin Coroutines
- AndroidX Lifecycle (ViewModel, LiveData)
- RecyclerView
- CardView
- Material Components
- (Optional) ExoPlayer cho audio
- (Optional) Glide/Coil cho images

## Kết luận

Tính năng bài thi đã được implement hoàn chỉnh với đầy đủ chức năng cơ bản:
- ✅ CRUD operations via API
- ✅ List & Filter exams
- ✅ View exam details
- ✅ Take exam with timer
- ✅ Submit & view results
- ✅ Proper error handling
- ✅ Material Design UI

Ứng dụng sẵn sàng để test với backend API!

