# Exam Feature Implementation Plan

## Yêu cầu từ người dùng

### 1. Exam Type Selection Screen
Tại trang chủ, khi bấm vào "Đề thi", hiển thị các lựa chọn loại đề thi theo backend:

```kotlin
enum class ExamType {
    TOEIC_FULL_TEST,  // Đề thi TOEIC đầy đủ (200 câu)
    TOEIC_MINI_TEST,  // Đề rút gọn (50-100 câu)
    IELTS_ACADEMIC,   // IELTS Học thuật
    IELTS_GENERAL,    // IELTS Tổng quát
    MOCK_TEST         // Thi thử chung chung / Bài kiểm tra 15p
}
```

### 2. Backend API Endpoints

**Get Exams by Type:**
```
GET /api/v1/exams/type?examType={type}

Response:
{
    "code": 1000,
    "message": "Load exams by type successfully",
    "result": [
        {
            "id": 1,
            "title": "TOEIC Full Test #1",
            "description": "...",
            "duration": 120,
            "totalQuestions": 200,
            // ...
        }
    ]
}
```

**Submit Exam Result:**
```
POST /api/v1/exam-results

Request Body:
{
    "score": 750,
    "listeningScore": 380,
    "readingScore": 370,
    "correctCount": 180,
    "submitTime": "2026-02-15T10:00:00Z",
    "timeTaken": 7200,
    "examId": 1,
    "examResultDetailRequestList": [
        {
            "selectedOption": "A",
            "isCorrect": true,
            "question": {
                "id": 1
            }
        }
    ]
}

Response:
{
    "code": 1000,
    "message": "Exam result created successfully",
    "result": {
        "id": 123,
        // ...
    }
}
```

**Get Exam Result by ID:**
```
GET /api/v1/exam-results/{id}

Response:
{
    "code": 1000,
    "message": "Exam result retrieved successfully",
    "result": {
        "id": 123,
        "score": 750,
        "listeningScore": 380,
        "readingScore": 370,
        // ... includes details array
    }
}
```

---

## Implementation Tasks

### Phase 1: UI - Exam Type Selection

**1.1. Create ExamTypeSelectionFragment**
```kotlin
class ExamTypeSelectionFragment : Fragment() {
    // Display grid/list of exam types
    // Navigate to ExamListFragment with selected type
}
```

**Files to create:**
- `ExamTypeSelectionFragment.kt`
- `fragment_exam_type_selection.xml`
- `item_exam_type.xml` (for RecyclerView item)

**1.2. Update Navigation**
- Add fragment to navigation graph
- Link from HomeFragment to ExamTypeSelectionFragment

---

### Phase 2: API Integration - Get Exams by Type

**2.1. Create/Update API Interface**
```kotlin
interface ExamApi {
    @GET("api/v1/exams/type")
    suspend fun getExamsByType(
        @Query("examType") examType: String
    ): Response<ApiResponse<List<ExamResponse>>>
}
```

**2.2. Create ExamType enum**
```kotlin
enum class ExamType {
    TOEIC_FULL_TEST,
    TOEIC_MINI_TEST,
    IELTS_ACADEMIC,
    IELTS_GENERAL,
    MOCK_TEST;
    
    fun getDisplayName(): String {
        return when (this) {
            TOEIC_FULL_TEST -> "TOEIC Full Test (200 câu)"
            TOEIC_MINI_TEST -> "TOEIC Mini Test (50-100 câu)"
            IELTS_ACADEMIC -> "IELTS Academic"
            IELTS_GENERAL -> "IELTS General"
            MOCK_TEST -> "Mock Test / Bài kiểm tra"
        }
    }
}
```

**Files to create/update:**
- `ExamType.kt` (new)
- `ExamApi.kt` (update)

---

### Phase 3: Exam List by Type

**3.1. Create ExamListFragment**
```kotlin
class ExamListFragment : Fragment() {
    private lateinit var examType: ExamType
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        examType = arguments?.getString("examType")?.let {
            ExamType.valueOf(it)
        } ?: ExamType.MOCK_TEST
    }
    
    // Load exams by type
    // Display in RecyclerView
    // Navigate to ExamDetailFragment on click
}
```

**Files to create:**
- `ExamListFragment.kt`
- `fragment_exam_list.xml`

---

### Phase 4: Exam Taking & Result Submission

**4.1. Update ExamTakingFragment**
- Track user answers: `Map<Long, String>` (questionId -> selectedOption)
- Track time taken
- Calculate score before submission

**4.2. Create ExamResultRequest models**
```kotlin
data class ExamResultRequest(
    val score: Int,
    val listeningScore: Int,
    val readingScore: Int,
    val correctCount: Int,
    val submitTime: Instant,
    val timeTaken: Int,
    val examId: Long,
    val examResultDetailRequestList: List<ExamResultDetailRequest>
)

data class ExamResultDetailRequest(
    val selectedOption: String?,
    val isCorrect: Boolean,
    val question: QuestionRequest
)

data class QuestionRequest(
    val id: Long
)
```

**Files to create:**
- `ExamResultRequest.kt`
- `ExamResultDetailRequest.kt`
- `QuestionRequest.kt`

**4.3. Create ExamResultApi**
```kotlin
interface ExamResultApi {
    @POST("api/v1/exam-results")
    suspend fun submitExamResult(
        @Body request: ExamResultRequest
    ): Response<ApiResponse<ExamResultResponse>>
    
    @GET("api/v1/exam-results/{id}")
    suspend fun getExamResultById(
        @PathVariable("id") id: Long
    ): Response<ApiResponse<ExamResultResponse>>
}
```

**Files to create/update:**
- `ExamResultApi.kt` (update if exists)
- `ExamResultResponse.kt`
- `ExamResultDetail.kt`

---

### Phase 5: Result Display

**5.1. Create ExamResultFragment**
```kotlin
class ExamResultFragment : Fragment() {
    // Display exam result summary
    // Show score breakdown
    // Show correct/incorrect answers
    // Button to review answers
}
```

**Files to create:**
- `ExamResultFragment.kt`
- `fragment_exam_result.xml`
- `item_exam_result_detail.xml`

---

### Phase 6: Repository & ViewModel

**6.1. Update ExamRepository**
```kotlin
class ExamRepository(private val context: Context) {
    
    suspend fun getExamsByType(examType: ExamType): Result<List<ExamResponse>> {
        return try {
            val response = RetrofitClient.examApi.getExamsByType(examType.name)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } else {
                Result.failure(Exception("Failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun submitExamResult(request: ExamResultRequest): Result<ExamResultResponse> {
        // Similar pattern
    }
    
    suspend fun getExamResultById(id: Long): Result<ExamResultResponse> {
        // Similar pattern
    }
}
```

**6.2. Update ExamViewModel**
```kotlin
class ExamViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _examsByType = MutableLiveData<List<ExamResponse>>()
    val examsByType: LiveData<List<ExamResponse>> = _examsByType
    
    private val _examResult = MutableLiveData<ExamResultResponse>()
    val examResult: LiveData<ExamResultResponse> = _examResult
    
    fun loadExamsByType(examType: ExamType) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getExamsByType(examType)
            result.onSuccess { exams ->
                _examsByType.postValue(exams)
            }
            result.onFailure { error ->
                _errorMessage.postValue(error.message)
            }
            _isLoading.postValue(false)
        }
    }
    
    fun submitExamResult(request: ExamResultRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.submitExamResult(request)
            result.onSuccess { response ->
                _examResult.postValue(response)
            }
            result.onFailure { error ->
                _errorMessage.postValue(error.message)
            }
            _isLoading.postValue(false)
        }
    }
}
```

---

## UI/UX Flow

```
HomeFragment
    ↓ Click "Đề thi"
ExamTypeSelectionFragment
    ↓ Select type (e.g., TOEIC_FULL_TEST)
ExamListFragment (filtered by type)
    ↓ Select exam
ExamDetailFragment
    ↓ Click "Bắt đầu"
ExamTakingFragment
    ↓ Submit answers
    ↓ Calculate score locally
    ↓ Call API to submit result
ExamResultFragment
    ↓ Show results
    ↓ Option to review answers
```

---

## Data Models Summary

**Request Models:**
- ExamResultRequest
- ExamResultDetailRequest  
- QuestionRequest

**Response Models:**
- ExamResponse (existing?)
- ExamResultResponse
- ExamResultDetail

**Enums:**
- ExamType

---

## Testing Checklist

- [ ] Exam types display correctly
- [ ] API call with examType parameter works
- [ ] Exams filtered by type
- [ ] User can select and start exam
- [ ] Answer tracking works
- [ ] Time tracking works
- [ ] Score calculation is correct
- [ ] Submit result API works
- [ ] Result display shows all info
- [ ] Can review incorrect answers
- [ ] Loading states work
- [ ] Error handling works

---

## Estimated Implementation Time

- Phase 1: 2-3 hours
- Phase 2: 1-2 hours
- Phase 3: 2 hours
- Phase 4: 4-5 hours (most complex)
- Phase 5: 2-3 hours
- Phase 6: 2-3 hours
- Testing & Bug fixes: 2-3 hours

**Total: ~15-20 hours**

---

## Notes

1. Backend đã sẵn sàng với các API cần thiết
2. Cần implement các model classes theo đúng cấu trúc backend
3. Cần xử lý Instant type cho submitTime (đã có GsonBuilder config)
4. Score calculation logic cần match với backend
5. Listening vs Reading score split cần được xác định (có thể dựa vào question type/section)


