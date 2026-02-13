# Tích hợp Lưu Kết Quả Bài Thi vào Backend

## Tóm tắt

Đã tích hợp hoàn chỉnh tính năng lưu kết quả bài thi vào backend sau khi người dùng hoàn thành làm bài. Kết quả được gửi lên API và lưu vào database.

## API Backend được sử dụng

### 1. Create Exam Result
```
POST /api/v1/exam-results
```

**Request Body:**
```json
{
  "score": 85,
  "listeningScore": 450,
  "readingScore": 445,
  "correctCount": 170,
  "submitTime": "2024-02-13T10:30:00Z",
  "timeTaken": 6300,
  "examId": 1,
  "examResultDetailRequestList": [
    {
      "selectedOption": "A",
      "isCorrect": true,
      "questionId": 101
    },
    {
      "selectedOption": "B",
      "isCorrect": false,
      "questionId": 102
    }
    // ... all questions
  ]
}
```

**Response:**
```json
{
  "code": 1000,
  "result": {
    "id": 123,
    "score": 85,
    "listeningScore": 450,
    "readingScore": 445,
    "correctCount": 170,
    "submitTime": "2024-02-13T10:30:00Z",
    "timeTaken": 6300,
    "user": {...},
    "exam": {...},
    "details": [...]
  },
  "message": "Exam result created successfully"
}
```

### 2. Get Exam Result by ID
```
GET /api/v1/exam-results/{id}
```

## Files đã tạo/cập nhật

### 1. Models (Exam.kt)

#### ExamResultRequest
```kotlin
data class ExamResultRequest(
    val score: Int,                    // Điểm % (0-100)
    val listeningScore: Int?,          // Điểm Listening (0-495)
    val readingScore: Int?,            // Điểm Reading (0-495)
    val correctCount: Int,             // Số câu đúng
    val submitTime: String,            // ISO 8601 format
    val timeTaken: Int,                // Thời gian (giây)
    val examId: Long,
    val examResultDetailRequestList: List<ExamResultDetailRequest>
)
```

#### ExamResultDetailRequest
```kotlin
data class ExamResultDetailRequest(
    val selectedOption: String?,       // "A", "B", "C", "D" or null
    val isCorrect: Boolean,
    val questionId: Long
)
```

#### ExamResultResponse
```kotlin
data class ExamResultResponse(
    val id: Long,
    val score: Int,
    val listeningScore: Int?,
    val readingScore: Int?,
    val correctCount: Int,
    val submitTime: String,
    val timeTaken: Int,
    val user: UserResponse?,
    val exam: ExamResponse?,
    val details: List<ExamResultDetailResponse>?
)
```

### 2. Network Layer

#### ExamResultApi.kt (Mới)
```kotlin
interface ExamResultApi {
    @POST("api/v1/exam-results")
    suspend fun createExamResult(@Body request: ExamResultRequest): Response<ApiResponse<ExamResultResponse>>
    
    @GET("api/v1/exam-results/{id}")
    suspend fun getExamResultById(@Path("id") id: Long): Response<ApiResponse<ExamResultResponse>>
}
```

#### RetrofitClient.kt (Cập nhật)
```kotlin
val examResultApi: ExamResultApi
    get() = getRetrofit().create(ExamResultApi::class.java)
```

### 3. Repository Layer

#### ExamResultRepository.kt (Mới)
- `createExamResult(request)` - Submit kết quả lên backend
- `getExamResultById(id)` - Lấy kết quả đã lưu

### 4. ViewModel

#### ExamViewModel.kt (Cập nhật)
**Thêm:**
- `examResultRepository` - Repository instance
- `submitExamResultLiveData` - LiveData cho kết quả submit
- `submitExamResult(request)` - Method submit kết quả

### 5. UI - ExamTakingActivity (Cập nhật lớn)

#### submitExam() method mới:
1. **Tính toán kết quả:**
   - Đếm số câu đúng
   - Tách Listening (Part 1-4) và Reading (Part 5-7)
   - Tính điểm % và điểm TOEIC (0-495 cho mỗi phần)
   - Tính thời gian làm bài (seconds)

2. **Chuẩn bị data:**
   - Tạo `ExamResultRequest` với tất cả thông tin
   - Tạo list `ExamResultDetailRequest` cho từng câu hỏi:
     - `selectedOption`: Đáp án người dùng chọn
     - `isCorrect`: Đúng/sai
     - `questionId`: ID câu hỏi

3. **Submit lên backend:**
   - Gọi `viewModel.submitExamResult(request)`
   - Observe `submitExamResultLiveData`

4. **Navigate với kết quả:**
   - **Success:** Navigate với `EXAM_RESULT_ID` từ backend
   - **Failure:** Hiển thị Toast và navigate với data cục bộ

**Code example:**
```kotlin
// Calculate listening and reading scores
questions.forEach { question ->
    val userAnswer = userAnswers[question.id]
    if (userAnswer == question.correctAnswer) {
        correctAnswers++
        if (group.type in listOf(PART_1, PART_2, PART_3, PART_4)) {
            listeningCorrect++
        } else {
            readingCorrect++
        }
    }
}

// Convert to TOEIC score (0-495 each)
val listeningScore = (listeningCorrect * 495 / 100)
val readingScore = (readingCorrect * 495 / 100)

// Create request
val examResultRequest = ExamResultRequest(
    score = score,
    listeningScore = listeningScore,
    readingScore = readingScore,
    correctCount = correctAnswers,
    submitTime = Instant.now().toString(),
    timeTaken = timeTakenSeconds,
    examId = exam.id,
    examResultDetailRequestList = examResultDetails
)

// Submit
viewModel.submitExamResult(examResultRequest)
```

### 6. UI - ExamResultActivity (Cập nhật)

**Thêm hiển thị:**
- Listening Score (🎧)
- Reading Score (📖)
- Exam Result ID từ backend

**Logic:**
- Nếu có listening/reading scores → Hiển thị (TOEIC)
- Nếu không → Ẩn (IELTS, Mock Test)

### 7. Layout - activity_exam_result.xml (Cập nhật)

**Thêm UI elements:**
```xml
<!-- Listening Score -->
<TextView android:id="@+id/tvListeningLabel" android:visibility="gone" />
<TextView android:id="@+id/tvListeningScore" android:visibility="gone" />

<!-- Reading Score -->
<TextView android:id="@+id/tvReadingLabel" android:visibility="gone" />
<TextView android:id="@+id/tvReadingScore" android:visibility="gone" />
```

## Luồng hoạt động (Workflow)

```
1. Người dùng làm bài thi
   ↓
2. Click "Nộp bài"
   ↓
3. ExamTakingActivity.submitExam():
   - Tính toán kết quả (correct answers, scores)
   - Tạo ExamResultRequest
   - Gọi API POST /api/v1/exam-results
   ↓
4. Backend xử lý:
   - Lưu ExamResult vào database
   - Lưu từng ExamResultDetail (câu hỏi + đáp án)
   - Trả về ExamResultResponse với ID
   ↓
5. Navigate to ExamResultActivity:
   - Truyền EXAM_RESULT_ID từ backend
   - Hiển thị kết quả đầy đủ
   - Có thể dùng ID này để load lại từ backend sau
```

## Dữ liệu được lưu

### ExamResult (Main record)
- `id` - ID kết quả từ backend
- `score` - Điểm phần trăm (0-100)
- `listeningScore` - Điểm Listening TOEIC (0-495)
- `readingScore` - Điểm Reading TOEIC (0-495)
- `correctCount` - Số câu đúng
- `submitTime` - Thời điểm nộp bài (ISO 8601)
- `timeTaken` - Thời gian làm bài (giây)
- `examId` - ID đề thi
- `userId` - ID người dùng (từ auth token)

### ExamResultDetail (Chi tiết từng câu)
- `id` - ID chi tiết
- `selectedOption` - Đáp án đã chọn ("A", "B", "C", "D" or null)
- `isCorrect` - Đúng/sai
- `questionId` - ID câu hỏi
- `examResultId` - ID kết quả bài thi

## Tính toán điểm TOEIC

### Điểm phần trăm:
```kotlin
val score = (correctAnswers / totalQuestions) * 100
```

### Điểm Listening và Reading (0-495):
```kotlin
// Listening: Part 1-4 (100 câu)
val listeningScore = (listeningCorrect * 495 / 100)

// Reading: Part 5-7 (100 câu)  
val readingScore = (readingCorrect * 495 / 100)

// Tổng TOEIC = listeningScore + readingScore (0-990)
```

**Lưu ý:** Đây là công thức đơn giản hóa. TOEIC thực tế dùng equating table phức tạp hơn.

## Error Handling

### Khi API thất bại:
```kotlin
result.onFailure { error ->
    // Show toast with error
    Toast.makeText(this, "Lưu kết quả thất bại: ${error.message}", LENGTH_LONG).show()
    
    // Still navigate to result screen with local data
    // User can see their result even if save failed
    navigateToResult(localData)
}
```

### Lợi ích:
- ✅ Không mất kết quả nếu API lỗi
- ✅ User vẫn thấy điểm của mình
- ✅ Có thể retry save sau (future feature)

## Các tính năng mở rộng (Future)

### 1. History & Review
- Load lại kết quả từ backend qua `getExamResultById()`
- Hiển thị lịch sử các lần làm bài
- Xem lại chi tiết từng câu trả lời

### 2. Statistics
- Thống kê điểm theo thời gian
- So sánh với các lần làm trước
- Phân tích điểm yếu (part nào sai nhiều)

### 3. Offline Support
- Cache kết quả local nếu không có internet
- Sync lên backend khi có kết nối
- Queue system cho pending submissions

### 4. Retry Mechanism
- Tự động retry nếu API timeout
- Show "Đang lưu..." progress
- Notification khi save thành công/thất bại

## Testing Checklist

- [ ] Làm bài thi đến hết
- [ ] Click "Nộp bài"
- [ ] Kiểm tra API được gọi với data đúng
- [ ] Backend lưu ExamResult thành công
- [ ] Backend lưu tất cả ExamResultDetails
- [ ] Response trả về với ID
- [ ] Navigate đến result screen
- [ ] Hiển thị đúng: score, listening, reading, time
- [ ] Test khi API fail → Vẫn hiển thị result
- [ ] Test với TOEIC Full Test (có listening/reading)
- [ ] Test với IELTS (không có listening/reading scores)

## Build Status

✅ **BUILD SUCCESSFUL** in 1m 6s
- Warnings: Chỉ có deprecated warnings (không ảnh hưởng)

## Database Schema (Backend)

### exam_results table:
```sql
CREATE TABLE exam_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    score INT,
    listening_score INT,
    reading_score INT,
    correct_count INT,
    submit_time TIMESTAMP,
    time_taken INT,
    user_id BIGINT,
    exam_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (exam_id) REFERENCES exams(id)
);
```

### exam_result_details table:
```sql
CREATE TABLE exam_result_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    selected_option VARCHAR(1),
    is_correct BOOLEAN,
    result_id BIGINT,
    question_id BIGINT,
    FOREIGN KEY (result_id) REFERENCES exam_results(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);
```

## Summary

**Đã tích hợp hoàn chỉnh:**
- ✅ Models cho ExamResult (Request & Response)
- ✅ API interface và Repository
- ✅ ViewModel với submit functionality
- ✅ ExamTakingActivity submit logic với:
  - Tính điểm chi tiết (%, listening, reading)
  - Tạo request với tất cả details
  - Submit lên backend API
  - Error handling
- ✅ ExamResultActivity hiển thị listening/reading scores
- ✅ Layout updates với TOEIC scores
- ✅ Build successful

**Kết quả bài thi giờ được:**
1. Tính toán chính xác (listening/reading riêng biệt)
2. Submit lên backend và lưu vào database
3. Trả về ID để reference sau này
4. Hiển thị đầy đủ trên UI
5. Có fallback nếu API fail

---

**Status: ✅ HOÀN THÀNH - Sẵn sàng test với backend API!**

